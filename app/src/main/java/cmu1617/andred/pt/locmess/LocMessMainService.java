package cmu1617.andred.pt.locmess;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;
import cmu1617.andred.pt.locmess.Domain.Settings;

import static android.R.attr.value;

public class LocMessMainService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnTaskCompleted {
    private static final String TAG = "LocMessMainService";
    private GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GetMessagesAsyncTask task;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private boolean mReadyToLaunchNewTask = true;
    private boolean mNewLocation;
    private double _latitude;
    private double _longitude;
    private WifiManager mWifiManager ;
    private WifiReceiver mWifiScanReceiver;
    private List<ScanResult> wifiList;
    private List<String> _ssidList = new ArrayList<>();

    public LocMessMainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started + " + value);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiReceiver();
        registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        createLocationRequest();
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates(){
        startGPSUpdates();
        startWifiUpdates();
    }

    private void startWifiUpdates() {
        // Check for wifi is disabled
        if (mWifiManager.isWifiEnabled() == false) {

            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
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
        mLocationRequest.setInterval(Settings.getPeriodicitySeconds()*1000);
        mLocationRequest.setFastestInterval(Settings.getPeriodicitySeconds()*1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mRequestingLocationUpdates = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();
        mNewLocation = true;
        executeNewTask();
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onTaskCompleted() {
        mReadyToLaunchNewTask = true;
        if(mNewLocation) {
            executeNewTask();
        }
    }

    private void executeNewTask() {
        if(mReadyToLaunchNewTask) {
            mNewLocation = false;
            mReadyToLaunchNewTask = false;
            task = new GetMessagesAsyncTask(new SQLDataStoreHelper(getBaseContext()),this);
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

            for(int i = 0; i < wifiList.size(); i++){
                _ssidList.add(wifiList.get(i).SSID);
            }
        }
    }
}
