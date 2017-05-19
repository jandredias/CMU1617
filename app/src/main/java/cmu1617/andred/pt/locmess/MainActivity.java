package cmu1617.andred.pt.locmess;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.LocmessSettings;
import pt.andred.cmu1617.LocMessAPIClientImpl;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SimWifiP2pManager.PeerListListener,
        SimWifiP2pManager.GroupInfoListener

{
    private final String TAG = "MainActivity";
//    private TextView mTextMessage;
    private final int REQUEST_LOCATION = 200;
    private final int REQUEST_CHECK_SETTINGS = 300;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    ViewPager _mainViewPager;
    FragmentPagerAdapter _adapterViewPager;
    private SQLDataStoreHelper _db;
    private Fragment _main_fragment;
    private FragmentManager _fragmentManager = getSupportFragmentManager();
    private GoogleApiClient mGoogleApiClient;

    private SimWifiP2pManager mManager = null;
    private Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    SimWifiP2pBroadcastReceiver receiver;
    IntentFilter filter;
    IntentFilter filter2;
    private SendWifiDirectBReceiver swdbr;
    private static final boolean EMULATOR = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        _db = new SQLDataStoreHelper(this);
        _main_fragment = new DashboardFragment();
        final FragmentTransaction transaction = _fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, _main_fragment).commit();

        // To  identify click on the drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.left_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             //For left menu appear
             @Override
             public boolean onNavigationItemSelected(MenuItem item) {
                 // Handle navigation view item clicks here.

                 switch (item.getItemId()) {
                     case R.id.settings:
                         Log.wtf(TAG, "settings pressed");
//                                                                         startActivity(new Intent(this, SettingsActivity.class));
                         break;
                     case R.id.profile:
                         Log.wtf(TAG, "profile pressed");

                         Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                         startActivity(intent);
                         break;
                     case R.id.logout:
                         Log.wtf(TAG, "logout pressed");
                         LocMessAPIClientImpl.getInstance().logout();
                         new Login(_db).logout();
                         startActivity(new Intent(MainActivity.this, LoginActivity.class));
                         break;
                     case R.id.my_messages:
                         Log.wtf(TAG, "My Messages");
                         startActivity(new Intent(getBaseContext(), MyMessagesActivity.class));
                         break;
                 }

                 DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                 drawer.closeDrawer(GravityCompat.START);
                 return true;
             }
         });

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.three_buttom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.navigation_locations:
                        _main_fragment = new DualLocationsFragment();
                        break;
                    case R.id.navigation_dashboard:
                        _main_fragment = new DashboardFragment();
                        break;
                    case R.id.navigation_archive:
                        _main_fragment = new ArchiveFragment();
                        break;
                }
                final FragmentTransaction transaction = _fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, _main_fragment).commit();
                return true;
            }
        });
        bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);

        enableLocation();


        startService(new Intent(this, LocMessMainService.class));


        // initialize the Termite API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        filter.addAction(LocMessIntent.TEST_REQUEST);
        filter.addAction(LocMessIntent.NEW_MESSAGE);
        receiver = new SimWifiP2pBroadcastReceiver(this);

       /* Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        startService(new Intent(this, SimWifiP2pBroadcastReceiver.class));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);*/

        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        swdbr = new SendWifiDirectBReceiver();
        swdbr.set_context(getApplicationContext());
        filter2 = new IntentFilter();
        filter2.addAction(LocMessIntent.NEW_PEERS_AVAILABLE);

        if(EMULATOR) {
            boolean permission = false;
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                permission = true;
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
                if(!permission) {
                    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                        permission = true;
                }

            if(permission){

                Log.e(TAG, "It' an  Emulator and we have permissions");
                ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        swdbr.set_latitude(location.getLatitude());
                        swdbr.set_longitude(location.getLongitude());
                        Log.e("LocationListener", "Location updated");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
        registerReceiver(swdbr, filter2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        unregisterReceiver(swdbr);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        // callbacks for service binding, passed to bindService()
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("ServiceConnection", "onServiceConnected");
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(),
                    null);
            mBound = false;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("ServiceConnection", "onServiceDisconnected");
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    private void enableLocation() {
        if(LocmessSettings.trueIfAskedUserAlready()) { return; }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestPermissions();
        LocationRequest mLocationRequest = new LocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates mState = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        //Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        Toast.makeText(MainActivity.this, "RESOLUTION_REQUIRED", Toast.LENGTH_LONG).show();

                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        Toast.makeText(MainActivity.this, "SETTINGS_CHANGE_UNAVAILABLE", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void requestPermissions() {
        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
//        if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.)
//            ACCESS_WIFI_STATE
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStop() {
        if(mGoogleApiClient != null) mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //location is on
                        break;
                    case Activity.RESULT_CANCELED:
                        // user does not want to update setting. Handle it in a way that it will to affect your app functionality
                        Toast.makeText(MainActivity.this, "Without location messages cannot be received", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }


    public void wifiPeersChanged() {
        mManager.requestPeers(mChannel, this);
        Log.d(TAG, "Request for peers");
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
        Log.d(TAG, "Peers Available");
        swdbr.set_simWifiP2pDeviceList(simWifiP2pDeviceList);
        //LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent(LocMessIntent.NEW_PEERS_AVAILABLE));
        swdbr.start();
       /* new LocMessMainService.SendWifiMessagesAsync().execute(simWifiP2pDeviceList);*/


    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {

    }

    public void printToast(String s){
        Log.d(TAG, "Printing Toast: " + s);
        Toast.makeText(getBaseContext(), "Received this: " + s, Toast.LENGTH_LONG);
    }



}
