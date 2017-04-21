package cmu1617.andred.pt.locmess;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
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

import cmu1617.andred.pt.locmess.Domain.Settings;
import pt.andred.cmu1617.LocMessAPIClientImpl;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = "MainActivity";
//    private TextView mTextMessage;
    private final int REQUEST_LOCATION = 200;
    private final int REQUEST_CHECK_SETTINGS = 300;
    ViewPager _mainViewPager;
    FragmentPagerAdapter _adapterViewPager;
    private SQLDataStoreHelper _db;
    private Fragment _main_fragment;
    private FragmentManager _fragmentManager = getSupportFragmentManager();
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        _db = new SQLDataStoreHelper(this);
        _main_fragment = new DualLocationsFragment();
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

        enableLocation();


        Log.wtf(TAG,"Calling service next");
        startService(new Intent(this, LocMessMainService.class));
        Log.wtf(TAG,"Called service ");
    }

    private void enableLocation() {
        if(Settings.trueIfAskedUserAlready()) { return; }

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
                        Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        } else {
                            //Already has permission
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // user does not want to update setting. Handle it in a way that it will to affect your app functionality
                        Toast.makeText(MainActivity.this, "Without location messages cannot be received", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }
}
