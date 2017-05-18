package cmu1617.andred.pt.locmess;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;
import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.LocmessSettings;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

import static android.R.attr.value;
import static android.R.id.message;

public class LocMessMainService
        extends
        Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SimWifiP2pManager.PeerListListener,
        OnTaskCompleted {
    private static final String TAG = "LocMessMainService";
    private GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    private GetMessagesAsyncTask task;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private boolean mReadyToLaunchNewTask = true;
    private Double _latitude;
    private Double _longitude;
    private WifiManager mWifiManager ;
    private WifiReceiver mWifiScanReceiver;
    private List<ScanResult> wifiList;
    private List<String> _ssidList = new ArrayList<>();
    private int delay; //milliseconds
    private Handler alarmHandler;
    private LocationManager locationManager;
    //WiFi Direct
    private final IntentFilter mIntentFilter = new IntentFilter();
    BroadcastReceiver _mMessageReceiver = new LocMessBroadcastReceiver();
    private Serializable mManager;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static LocMessMainService _instance;
    private RefactorMessagesAsync rmaASYNC = new RefactorMessagesAsync();
    private SendWifiMessagesAsync swmASYNC = new SendWifiMessagesAsync();


    public static LocMessMainService getInstance() {
        if(_instance != null){
            _instance = new LocMessMainService();
        }
        return _instance;
    }

    public LocMessMainService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf(TAG, "Service Started + " + value);

        _instance = this;

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        alarmHandler = new Handler();
        locationManager =  (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        registerWifiReceiver();
        createLocationRequest();
        setAlarm();


      //  new ReceiveWIFIMessagesAsync().execute();
        SQLDataStoreHelper db = new SQLDataStoreHelper(getBaseContext());
        new Thread(new ReceiveWiFiDirectThread(db)).start();

        mManager = intent.getSerializableExtra("mManager");

        return START_STICKY;
    }

    private void registerWifiReceiver() {
        mWifiManager = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiReceiver();
        registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    private void setAlarm() {
        checkSettings();

        delay = LocmessSettings.getPeriodicityMilliSeconds(); //milliseconds
        Log.wtf(TAG, "Setting new alarm in " + delay + " milli seconds");

        alarmHandler.postDelayed(new Runnable(){
            public void run(){
                //do something
                executeNewTask();
            }
        }, delay);
    }

    private void checkSettings() {

        if ( !gpsIsOn() ) {            disableGPSMessages();        }
        requestWifiScan();
    }

    private boolean gpsIsOn() {
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }


    private void disableGPSMessages() {
        Log.wtf(TAG,"no gps..");
        _latitude = null;
        _longitude = null;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates(){
        startGPSUpdates();
        requestWifiScan();
    }

    private void requestWifiScan() {
        // Check for wifi is disabled
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
        }else if(mWifiManager.isScanAlwaysAvailable()) {
            mWifiManager.startScan();
        } else {
            Log.wtf(TAG,"no wifi list..");
            _ssidList = new ArrayList<>(); //state cleaning
        }
    }

    protected void startGPSUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; //if no permissions nothing to do here :(
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        mGoogleApiClient.disconnect();
        unregisterReceiver(mWifiScanReceiver);
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LocmessSettings.getPeriodicityMilliSeconds());
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mRequestingLocationUpdates = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onTaskCompleted(Object... args) {
        mReadyToLaunchNewTask = true;

        if((int) args[0] > 0 ) {
            buildNotification();
        }

        Intent intent = new Intent();
        intent.setAction(getString(R.string.intent_broadcast_new_messages));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        setAlarm();
    }

    private void buildNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true) // clear notification after click
//                        .setContentText("Hello World!")
                        .setContentTitle("Messages Available");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void executeNewTask() {
        if(mReadyToLaunchNewTask) {
            mReadyToLaunchNewTask = false;

            task = new GetMessagesAsyncTask(new SQLDataStoreHelper(getBaseContext()), this);
            task.setLatitude(_latitude);
            task.setLongitude(_longitude);
            task.setSsidList(_ssidList);
            task.execute();


        }

        rmaASYNC.execute();

    }


    private class RefactorMessagesAsync extends AsyncTask<Void, Void, Void>{
        private ArrayList<Integer> GPS_location;
        private ArrayList<Integer> WIFI_location;
        private SQLDataStoreHelper _db;
        private boolean mLocationPermissionGranted = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GPS_location = new ArrayList<>();
            _db = new SQLDataStoreHelper(getApplicationContext());

            if (!mLocationPermissionGranted) {
                if (ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getGPSLocation();

                }
            } else {
                getGPSLocation();

            }
            getWIFILocation();
        }
        private void getGPSLocation() {
            Location location = null;
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                //The permission is already being checked above, so this won't happen
            }
            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_GPS_LOCATION,
                    DataStore.SQL_GPS_LOCATION_COLUMNS,
                    "enabled = 1",
                    null, null, null, null
            );
            cursor.moveToFirst();

            double longitude;
            double latitude;
            Location loc;
            int radius;
            while (cursor.moveToNext()) {
                longitude = cursor.getDouble(2);
                latitude = cursor.getDouble(1);
                radius = cursor.getInt(3);
                loc = new Location("");
                loc.setLongitude(longitude);
                loc.setLatitude(latitude);
                if (loc.distanceTo(location) <= radius) {
                    GPS_location.add(cursor.getInt(0));
                    break;
                }
            }
        }


        private void getWIFILocation() {
            List<String> ssid_list = LocMessMainService.getInstance().getSsidList();
            String[] ssid_list_string = (String[]) ssid_list.toArray();
            String sql = "where (0 = 1) ";
            for (String ssid : ssid_list) {
                sql += " OR (ssid = ? AND enabled = 1)";
            }

            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_LOCATION_SSID,
                    DataStore.SQL_WIFI_LOCATION_SSID_COLUMNS,
                    sql,
                    ssid_list_string, null, null, null
            );
            cursor.moveToFirst();

            WIFI_location = new ArrayList<>();
            while (cursor.moveToNext()) {
                WIFI_location.add(cursor.getInt(0));
            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            String[] first = {
                    DataStore.SQL_WIFI_MESSAGES,
                    DataStore.SQL_MESSAGES
            };
            String[][] second = {
                    DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                    DataStore.SQL_MESSAGES_COLUMNS
            };
            int[] third ={
                    5,
                    5
            };
            for (int i = 0; i<2; i++) {


                Cursor cursor = _db.getReadableDatabase().query(
                        first[i],
                        second[i],
                        null,
                        null, null, null, null
                );
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                messageLoop:
                while (cursor.moveToNext()) {
                    String date2 = cursor.getString(third[i]);
                    Date date3;
                    try {
                        date3 = dateFormat.parse(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (date.after(date3)) {
                        String[] selectionArgs = {cursor.getString(0)};
                        _db.getWritableDatabase().delete(
                                DataStore.SQL_WIFI_MESSAGES,
                                "message_id = ?",
                                selectionArgs
                        );

                    }
                    else if(i == 0){
                        int loc_int = cursor.getInt(3); //location_id
                        for (Integer loc : GPS_location) {
                            if (loc == loc_int) {
                                makeChange(cursor);
                                continue messageLoop;
                            }
                        }
                        for (Integer loc : WIFI_location) {
                            if (loc == loc_int) {
                                makeChange(cursor);
                                continue messageLoop;
                            }
                        }
                    }
                }
            }
            return null;
        }
        private void makeChange(Cursor cursor){
            String[] selectionArgs = {cursor.getString(0)};
            Cursor restrictions = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS,
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS_COLUMNS,
                    "message_id = ?",
                    selectionArgs,
                    null, null, null
            );



            while (restrictions.moveToNext()){
                selectionArgs = new String[]{restrictions.getString(1)};
                Cursor user_restrictions = _db.getReadableDatabase().query(
                        DataStore.SQL_USER_KEYWORDS,
                        DataStore.SQL_USER_KEYWORDS_COLUMNS,
                        "keyword_id",
                        selectionArgs,
                        null, null, null
                );
                boolean enabled = restrictions.getInt(3) ==1;

                if(user_restrictions.moveToFirst()){
                    if(restrictions.getString(2).equals(user_restrictions.getString(1)))
                        if(!enabled)
                            return;
                    else if(enabled)
                        return;
                }
                else if(enabled)
                    return;

            }



            LocMessMessage LMm = new LocMessMessage(_db, cursor.getString(0));
            String location = Integer.toString(cursor.getInt(3));
            LMm.completeObject(location,cursor.getString(2),cursor.getString(1),
                    cursor.getString(4),cursor.getString(5),cursor.getString(7),"1",
                    cursor.getString(8),cursor.getString(9),cursor.getString(10));
        }
    }

    public List<String> getSsidList() {
        return _ssidList;
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
       swmASYNC.execute(simWifiP2pDeviceList);
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            _ssidList = new ArrayList<>();

            wifiList = mWifiManager.getScanResults();
            Log.d(TAG,"Received Wifi List: " + wifiList.size() + " elements");

            for(int i = 0; i < wifiList.size(); i++){
                _ssidList.add(wifiList.get(i).SSID);
            }
        }
    }

    private class LocMessBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "RECEIVED A BROADCAST");
            switch (intent.getAction()) {
                case LocMessIntent.NEW_PEERS_AVAILABLE:
                    swmASYNC.execute();
                    break;
            }
        }
    }

    private class SendWifiMessagesAsync extends AsyncTask<SimWifiP2pDeviceList, Void, Void> {
        private SQLDataStoreHelper _db;
        private Cursor cursor;
        private boolean mLocationPermissionGranted = false;
        private ArrayList<Integer> GPS_location;
        private ArrayList<Integer> WIFI_location;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GPS_location = new ArrayList<>();
            _db = new SQLDataStoreHelper(getApplicationContext());
            cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES,
                    DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                    "jumped = 0 OR jumped = 1",
                    null, null, null, null
            );
            if (!mLocationPermissionGranted) {
                if (ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getGPSLocation();

                }
            } else {
                getGPSLocation();

            }
            getWIFILocation();

        }

        private void getGPSLocation() {
            Location location = null;
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                //The permission is already being checked above, so this won't happen
            }
            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_GPS_LOCATION,
                    DataStore.SQL_GPS_LOCATION_COLUMNS,
                    "enabled = 1",
                    null, null, null, null
            );
            cursor.moveToFirst();

            double longitude;
            double latitude;
            Location loc;
            int radius;
            while (cursor.moveToNext()) {
                longitude = cursor.getDouble(2);
                latitude = cursor.getDouble(1);
                radius = cursor.getInt(3);
                loc = new Location("");
                loc.setLongitude(longitude);
                loc.setLatitude(latitude);
                if (loc.distanceTo(location) <= radius) {
                    GPS_location.add(cursor.getInt(0));
                    break;
                }
            }
        }


        private void getWIFILocation() {
            List<String> ssid_list = LocMessMainService.getInstance().getSsidList();
            String[] ssid_list_string = (String[]) ssid_list.toArray();
            String sql = "where (0 = 1) ";
            for (String ssid : ssid_list) {
                sql += " OR (ssid = ? AND enabled = 1)";
            }

            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_LOCATION_SSID,
                    DataStore.SQL_WIFI_LOCATION_SSID_COLUMNS,
                    sql,
                    ssid_list_string, null, null, null
            );
            cursor.moveToFirst();

            WIFI_location = new ArrayList<>();
            while (cursor.moveToNext()) {
                WIFI_location.add(cursor.getInt(0));
            }

        }

        @Override
        protected Void doInBackground(SimWifiP2pDeviceList... params) {

            cursor.moveToFirst();
            Collection<SimWifiP2pDevice> devices = params[0].getDeviceList();

            messageLoop:
            while (cursor.moveToNext()) {
                int jumped = cursor.getInt(6); //jumped
                if (jumped == 1) {
                    int a_int = cursor.getInt(3); //location_id
                    for (Integer loc : GPS_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                    for (Integer loc : WIFI_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                } else { // jumped == 0
                    int a_int = cursor.getInt(3); //location_id
                    for (Integer loc : GPS_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                    for (Integer loc : WIFI_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                    sendAll(devices, 0);
                }
            }


            return null;
        }

        private void sendAll(Collection<SimWifiP2pDevice> device_list, int jumped) {
            String[] selectionArgs = {cursor.getString(0)};

            Cursor c = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS,
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS_COLUMNS,
                    "message_id = ?",
                    selectionArgs,
                    null,null, null
            );
            int number_restrictions = 0;
            String appendable ="";
            while (c.moveToNext()){
                number_restrictions++;
                appendable +=
                        "::" + c.getString(1) + //keyword_id
                        "::" + c.getString(2) + //keyword_value
                        "::" + c.getInt(3); //equal
            }
            String toPass = number_restrictions + appendable;

            String toSend = "MESSAGE" +
                    "::" + cursor.getString(0) +//id
                    "::" + cursor.getString(1) +//content
                    "::" + cursor.getString(2) +//author
                    "::" + cursor.getInt(3) +//location_id
                    "::" + cursor.getString(4) +//time_start
                    "::" + cursor.getString(5) +//time_end
                    "::" + jumped +//jumped
                    "::" + cursor.getString(7) +//timestamp
                    "::" + cursor.getString(8) +//signature
                    "::" + cursor.getString(9) +//certificate
                    "::" + cursor.getString(10) +//publicKey
                    "::" + toPass
                    ;
            sendAll_part2(device_list, toSend);
        }

        private void sendAll_part2(Collection<SimWifiP2pDevice> device_list, String toSend) {
            SimWifiP2pSocket sock = null;
            for (SimWifiP2pDevice device : device_list) {
                try {
                    sock = new SimWifiP2pSocket(device.getVirtIp(), device.getVirtPort());
                    OutputStream sockOut = sock.getOutputStream();
                    sockOut.write(toSend.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        sock.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
  /*  private class ReceiveWIFIMessagesAsync extends AsyncTask<Void, Void, Void>{
        private SQLDataStoreHelper _db;
        private int current_mule = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _db = new SQLDataStoreHelper(getApplicationContext());
            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES,
                    DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                    "jumped = 1",
                    null, null, null, null
            );
            if(cursor.moveToFirst()) current_mule = 1;
            while(cursor.moveToNext()) current_mule++;
        }

        @Override
        protected Void doInBackground(Void... params) {

            SimWifiP2pSocketServer mSrvSocket = null;
            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String request = sockIn.readLine();
                        processInput(request);

                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }


        private void processInput(String input){
            String[] data = input.split("::");
            int jumped;
            try {
                jumped = Integer.parseInt(data[7]);
            }catch (Exception e){
                return;
            }

            if(jumped==0){
                if(current_mule<MAX_MULE_WIFI_MESSAGES){
                    jumped=1;
                    current_mule++;
                }
                else
                    return;
            }
            else jumped = 2;
            ContentValues values = new ContentValues();
            try {
                values.put("id", data[1]);
                values.put("content", data[2]);
                values.put("author_id", data[3]);
                values.put("location_id", Integer.parseInt(data[4]));
                values.put("time_start", data[5]);
                values.put("time_end", data[6]);
                values.put("jumped", jumped);
                values.put("timestamp", data[8]);
                values.put("signature", data[9]);
                values.put("certificate", data[10]);
                values.put("publicKey", data[11]);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }

            _db.getWritableDatabase().insert(
                    DataStore.SQL_WIFI_MESSAGES,
                    null,
                    values);
        }
    }*/
}
