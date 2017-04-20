package cmu1617.andred.pt.locmess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cmu1617.andred.pt.locmess.AsyncTasks.GetMessagesAsyncTask;

public class LocMessMainService extends Service {
    private static final String TAG = "LocMessMainService";
    private SQLDataStoreHelper _db;

    public LocMessMainService () {
        _db = new SQLDataStoreHelper(getBaseContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new GetMessagesAsyncTask(_db).execute();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Supported");
    }
}
