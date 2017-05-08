package cmu1617.andred.pt.locmess;

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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;
import cmu1617.andred.pt.locmess.Domain.LocmessSettings;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

import static android.R.attr.value;

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
    private WifiManager wifiManager;
    //WiFi Direct
    private final IntentFilter mIntentFilter = new IntentFilter();
    BroadcastReceiver _mMessageReceiver = new LocMessBroadcastReceiver();
    private Serializable mManager;

    private static LocMessMainService _instance;

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
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        createLocationRequest();
        setAlarm();

//        new ReceiveWIFIMessagesAsync().execute();

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

    }

    public List<String> getSsidList() {
        return _ssidList;
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
        new SendWifiMessagesAsync().execute(simWifiP2pDeviceList);
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
                    new SendWifiMessagesAsync().execute();
                    break;
            }
        }
    }

    private class SendWifiMessagesAsync extends AsyncTask<SimWifiP2pDeviceList, Void, Void>{
        private SQLDataStoreHelper _db;
        private Cursor cursor;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _db = new SQLDataStoreHelper(getApplicationContext());
            cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES,
                    DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                    "jumped = 0 OR jumped = 1",
                    null, null, null, null
            );
        }

        @Override
        protected Void doInBackground(SimWifiP2pDeviceList... params) {
            Collection<SimWifiP2pDevice> device_list = params[0].getDeviceList();
            SimWifiP2pSocket sock;

            for(SimWifiP2pDevice device : device_list) {
                try {
                    cursor.moveToFirst();
                    sock = new SimWifiP2pSocket(device.getVirtIp(), device.getVirtPort());
                    BufferedReader sockIn = new BufferedReader( new InputStreamReader(sock.getInputStream()));
                    OutputStream sockOut = sock.getOutputStream();
                    while (cursor.moveToNext()) {
                        String tosend = "MESSAGE-LOCATION::"+cursor.getString(3);
                        sockOut.write(tosend.getBytes());
                        String response = sockIn.readLine();
                        if (response.equals("YES")){
                            tosend = "MESSAGE" +
                                    "::"+ cursor.getString(0) +//id
                                    "::"+ cursor.getString(1) +//content
                                    "::"+ cursor.getString(2) +//author
                                    "::"+ cursor.getString(4) +//time_start
                                    "::"+ cursor.getString(5) +//time_end
                                    "::"+ cursor.getString(6) //jumped
                                    ;
                            sockOut.write(tosend.getBytes());

                            String reply = sockIn.readLine();
                           /* if(reply.equals("SUCCESS")){
                                //FTODO
                            }*/

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }



    }

    private class ReceiveWIFIMessagesAsync extends AsyncTask<Void, Void, Void>{
        private WifiManager wifiManager;
        private String SSID;
        private SQLDataStoreHelper _db;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            _db = new SQLDataStoreHelper(getApplicationContext());
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

                        String response;
                        OutputStream sockOut = sock.getOutputStream();
                        if ((response =processInput(request)) != null){
                            sockOut.write(response.getBytes());
                            String message = sockIn.readLine();
                            String reply = processMessage(message);
                            sockOut.write(reply.getBytes());
                        }


                        sock.getOutputStream().write(("\n").getBytes());
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


        private String processInput(String input){
            String[] data = input.split("::");
            if(data[0].equals("MESSAGE-LOCATION")){
                if(data[1].equals(wifiManager.getConnectionInfo().getBSSID())) {
                    SSID = data[1];
                    return "YES";
                }
                return "NO";
            }
            return null;
        }
        private String processMessage(String message) {
            String[] data = message.split("::");
            int j;
            try {
                j = Integer.parseInt(data[7]);
            }catch (Exception e){
                return "ERROR";
            }
            ContentValues values = new ContentValues();
            values.put("id", data[1]);
            values.put("content", data[2]);
            values.put("author_id", data[3]);
            values.put("location_id", SSID);
            values.put("time_start", data[5]);
            values.put("time_end", data[6]);
            j++;
            values.put("jumped", Integer.toString(j));

            _db.getWritableDatabase().insert(
                    DataStore.SQL_WIFI_MESSAGES,
                    null,
                    values);
            return "SUCCESS";
        }
    }
}
