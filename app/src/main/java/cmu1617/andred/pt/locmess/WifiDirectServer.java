package cmu1617.andred.pt.locmess;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by Jorge Veiga on 06/05/2017.
 */

public class WifiDirectServer extends Thread {
    private SimWifiP2pSocketServer mSrvSocket = null;
    private MainActivity _MA;
    private final String TAG = "WifiDirectServer";
    public WifiDirectServer(MainActivity ma){
        _MA = ma;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "Thread is running");
            mSrvSocket = new SimWifiP2pSocketServer(10001);
            SimWifiP2pSocket sock = mSrvSocket.accept();
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String s = sockIn.readLine();
            _MA.printToast(s);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
