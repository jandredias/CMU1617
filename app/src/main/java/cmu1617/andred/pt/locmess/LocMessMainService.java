package cmu1617.andred.pt.locmess;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
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

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;
import cmu1617.andred.pt.locmess.Domain.LocmessSettings;

import static android.R.attr.value;

public class LocMessMainService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnTaskCompleted {
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
    WifiP2pManager.Channel mChannel;
    WiFiDirectBroadcastReceiver mReceiver;



    public LocMessMainService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf(TAG, "Service Started + " + value);

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

        //WiFi Direct
        //  Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiP2pManager mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this, new SQLDataStoreHelper(this));


        return START_STICKY;
    }


    private void registerWifiReceiver() {
        registerReceiver(mReceiver, mIntentFilter);
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
        mReceiver.set_latitude(_latitude);
        mReceiver.set_logintude(_longitude);
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
}
