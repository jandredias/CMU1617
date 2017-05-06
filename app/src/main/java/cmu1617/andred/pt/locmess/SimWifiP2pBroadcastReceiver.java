package cmu1617.andred.pt.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;


/**
 * Created by Jorge Veiga on 06/05/2017.
 */

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver{
    private MainActivity _MA;
    private String TAG = "SimWifiP2pBdReceiver";
    public SimWifiP2pBroadcastReceiver(MainActivity ma){
        _MA = ma;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(SimWifiP2pBroadcast.EXTRA_WIFI_STATE, -
                    1);
            if (state == SimWifiP2pBroadcast.WIFI_P2P_STATE_ENABLED) {
            } else {
            }
        } else if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
            _MA.wifiPeersChanged();

        } else if (SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION.
                equals(action)) {
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        } else if (SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.
                equals(action)) {
            SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                    SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
        }
    }
}
