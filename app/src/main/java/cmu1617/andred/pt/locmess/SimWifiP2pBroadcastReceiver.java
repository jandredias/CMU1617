package cmu1617.andred.pt.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;


/**
 * Created by Jorge Veiga on 06/05/2017.
 */

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver{
    private MainActivity _MA;
    private String TAG = "SimWifiP2pBdReceiver";
    public SimWifiP2pBroadcastReceiver(MainActivity ma){
        super();
        _MA = ma;
        Log.d(TAG, "Created");
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receiving Broadcast");
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");
            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -
                    1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "WIFI direct enabled");
                Toast.makeText(_MA, "WiFi Direct enabled",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "WIFI direct disabled");
                Toast.makeText(_MA, "WiFi Direct disabled",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
            _MA.wifiPeersChanged();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.
                equals(action)) {

            Log.d(TAG, "WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION");
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);

        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.
                equals(action)) {

            Log.d(TAG, "WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION");
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        }

        else if(LocMessIntent.TEST_REQUEST.equals(action)){
            Log.d(TAG, "Received TEST BROADCAST");
            _MA.wifiPeersChanged();
        }
    }
}
