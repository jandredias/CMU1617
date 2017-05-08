package cmu1617.andred.pt.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.LocMessWIFIMessage;

/**
 * Created by Jorge Veiga on 25/04/2017.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private LocMessMainService mActivity;
    private WifiP2pManager.PeerListListener mPeerListListener;;
    private WifiP2pDeviceList mDeviceList;
    private final String TAG = "WiFiDBroadcastReceiver";
    private List<WifiP2pDevice> _peers = new ArrayList<>();
    private SQLDataStoreHelper _dbHelper;

    private static Double _latitude = 38.736946;
    private static Double _logintude = -9.142685;

    public static void set_latitude(Double _latitude) {
        WiFiDirectBroadcastReceiver._latitude = _latitude;
    }

    public static void set_logintude(Double _logintude) {
        WiFiDirectBroadcastReceiver._logintude = _logintude;
    }


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       LocMessMainService activity, SQLDataStoreHelper db) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this._dbHelper = db;

        mPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
                if (!refreshedPeers.equals(_peers)) {
                    _peers.clear();
                    _peers.addAll(refreshedPeers);
                    sendMessages();
                }

                if (_peers.size() == 0) {
                    Log.d(TAG, "No devices found");
                }
            }
        };

       mManager.discoverPeers(mChannel, new DiscoverPeersActionListener());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // activity.setIsWifiP2pEnabled(true);
            } else {
                //activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerListListener);
            }
           // mPeerListListener.onPeersAvailable(mDeviceList);
            //sendMessages();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/

        }/*else if(LocMessIntent.CREATED_NEW_WIFI_MESSAGE_REQUEST.equals(action)){

        }*/

    }


    private void sendMessages(){
        new SendMessageAsyncTask(_dbHelper, _peers).execute();

    }

    public static boolean checkinGPSLocation(LatLng location, float radius,  LatLng mine) {
        Location lA = new Location("A");
        lA.setLatitude(location.latitude);
        lA.setLongitude(location.longitude);
        Location lB = new Location("B");
        lB.setLatitude(mine.latitude);
        lB.setLongitude(mine.longitude);

        return lA.distanceTo(lB) <= radius;
    }

   /* public static boolean checkInLocation(LocMessLocation location){

        LatLng loc = new LatLng(location.latitude(), location.longitude);

    }*/

    public class DiscoverPeersActionListener implements WifiP2pManager.ActionListener {

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(int reason) {

        }
    }
    public static class SendMessageAsyncTask extends AsyncTask {

       /* private Context context;*/
        private SQLDataStoreHelper _dbHelper;
        private List<LocMessWIFIMessage> _messages;
        private List<WifiP2pDevice> _peers;
        private final String TAG = "SendMessageAsyncTask";

        public SendMessageAsyncTask(/*Context context,*/ SQLDataStoreHelper dbHelper, List<WifiP2pDevice> peers) {
           /* this.context = context;*/
            this._dbHelper = dbHelper;
            this._peers = peers;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String[] selectionArgs = {"0"};
           Cursor cursor = _dbHelper.getReadableDatabase().query(
                   DataStore.SQL_WIFI_MESSAGES,
                   DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                   "jumped = ?",
                   selectionArgs,
                   null, null, null);

            while (cursor.moveToNext()) {
                LocMessLocation location = new LocMessLocation(_dbHelper, cursor.getString(3));
                if ( /*checkinLocation(location)*/ false) { //FIXME
                    LocMessWIFIMessage message = new LocMessWIFIMessage(_dbHelper, cursor.getString(0));
                    _messages.add(message);
                }
            }
            cursor.close();

        }

        @Override
        protected String doInBackground(Object[] params) {


                for(WifiP2pDevice device: _peers ) {
                    Log.d(TAG, "send messages, device: " + device.toString());
                }
                return null;

        }

        /**
         * Start activity that can handle the JPEG image
         */
        @Override
        protected void onPostExecute(Object result) {

        }


    }

}
