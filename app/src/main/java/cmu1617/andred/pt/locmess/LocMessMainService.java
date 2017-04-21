package cmu1617.andred.pt.locmess;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;

import static android.R.attr.value;

public class LocMessMainService extends Service implements GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "LocMessMainService";
    private GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private GetMessagesAsyncTask task;
    public LocMessMainService () {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started + " + value);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        mGoogleApiClient.connect();

        task = new GetMessagesAsyncTask(new SQLDataStoreHelper(getBaseContext()));
        task.execute();
//        createLocationRequest();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
