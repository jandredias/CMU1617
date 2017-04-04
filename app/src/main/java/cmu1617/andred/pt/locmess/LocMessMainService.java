package cmu1617.andred.pt.locmess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocMessMainService extends Service {
    private static final String TAG = "LocMessMainService";

    public LocMessMainService () {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
