package cmu1617.andred.pt.locmess;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;

import static android.R.attr.value;

public class LocMessMainService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LocMessMainService";
    private GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GetMessagesAsyncTask task;
    public LocMessMainService () {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started + " + value);

        // Create an instance of GoogleAPIClient.
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//
//        }
//        mGoogleApiClient.connect();

        task = new GetMessagesAsyncTask(new SQLDataStoreHelper(getBaseContext()));
        task.execute();
//        createLocationRequest();
        return START_STICKY;
    }

//    private void createLocationRequest() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000); // millis
//        mLocationRequest.setFastestInterval(5000);// millis
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
//                        builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final com.google.android.gms.common.api.Status status = result.getStatus();
////                final LocationSettingsStates state = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can
//                        // initialize location requests here.
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied, but this can be fixed
//                        // by showing the user a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult( getactivity, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way
//                        // to fix the settings so we won't show the dialog.
//                        break;
//                }
//            }
//        });
//    }
//    private void requestLocationPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.class, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
//            ActivityCompat.requestPermissions(MainActivity.class,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    ACCESS_FINE_LOCATION_INTENT_ID);
//
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.class, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    ACCESS_FINE_LOCATION_INTENT_ID);
//        }
//    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service was Created");
    }


    @Override
    public void onDestroy() {

        mGoogleApiClient.disconnect();
        Log.d(TAG, "Service was Destroyed");

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
