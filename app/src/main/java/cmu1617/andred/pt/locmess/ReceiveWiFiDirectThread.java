package cmu1617.andred.pt.locmess;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by Jorge Veiga on 17/05/2017.
 */

public class ReceiveWiFiDirectThread extends Thread {
    private SQLDataStoreHelper _db;
    private int current_mule = 0;
    private int PORT = 10001;
    private static final String TAG = "ReceiveWiFiDirectThread";
    private final static int MAX_MULE_WIFI_MESSAGES = 10;

    public ReceiveWiFiDirectThread (SQLDataStoreHelper db) {
    _db = db;
    }

    public void run() {

        checkCurrentMule();


        SimWifiP2pSocketServer mSrvSocket = null;
        try {
            mSrvSocket = new SimWifiP2pSocketServer(
                    PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SimWifiP2pSocket sock = mSrvSocket.accept();
                try {
                    BufferedReader sockIn = new BufferedReader(
                            new InputStreamReader(sock.getInputStream()));
                    String request = sockIn.readLine();
                    processInput(request);

                } catch (IOException e) {
                    Log.d("Error reading socket:", e.getMessage());
                } finally {
                    sock.close();
                }
            } catch (IOException e) {
                Log.d("Error socket:", e.getMessage());
                break;
                //e.printStackTrace();
            }
        }
    }


    private void processInput(String input){
        Log.e(TAG, "Received: "+ input);
        String[] data = input.split("::");
        int jumped;
        try {
            jumped = Integer.parseInt(data[7]);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        String[] selectionArgs = {data[1]};
           Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES,
                    DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                    "message_id = ?",
                   selectionArgs,
                    null, null, null);
        if(cursor.moveToFirst()){
            Log.e(TAG, "message is already in database");
            return; //already in database
        }

        checkCurrentMule();

        if(jumped==0){
            if(current_mule<MAX_MULE_WIFI_MESSAGES){
                jumped=1;
                current_mule++;
                Log.e(TAG, "Current_mule is now: " + current_mule);
            }
            else {
                Log.e(TAG, "Current mule max reached");
                return;
            }
        }
        else jumped = 2;
        ContentValues values = new ContentValues();
        try {
            values.put("message_id", data[1]);
            values.put("content", data[2]);
            values.put("author_id", data[3]);
            values.put("location_id", Integer.parseInt(data[4]));
            values.put("time_start", data[5]);
            values.put("time_end", data[6]);
            values.put("jumped", jumped);
            values.put("timestamp", data[8]);
            Log.d(TAG, "Signature = "+ new String(Base64.decode(data[9], Base64.DEFAULT)));
            values.put("signature", new String(Base64.decode(data[9], Base64.DEFAULT)));
            values.put("certificate", data[10]);
            values.put("publicKey", new String(Base64.decode(data[11], Base64.DEFAULT)));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        _db.getWritableDatabase().insert(
                DataStore.SQL_WIFI_MESSAGES,
                null,
                values);

        int number_restrictions= Integer.parseInt(data[12]);
        if(number_restrictions == 0) return;
        for(int i = 0; i<number_restrictions; i++){
            values = new ContentValues();
            values.put("message_id", data[1]);
            values.put("keyword_id", data[12+3*i+1]);
            values.put("keyword_value", data[12+3*i+2]);
            values.put("equal", data[12+3*i+3]);
            _db.getWritableDatabase().insert(
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS,
                    null,
                    values
            );
        }
        Log.e(TAG, "Wrote on database;");
    }

    private void checkCurrentMule(){

        int current = 0;

        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "jumped = 1",
                null, null, null, null
        );
        while(cursor.moveToNext()) current++;
        current_mule = current;
        Log.d(TAG, "Current mule is: " + current_mule);
    }
}
