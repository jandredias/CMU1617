package cmu1617.andred.pt.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

/**
 * Created by Jorge Veiga on 19/05/2017.
 */

public class SendWifiDirectBReceiver extends BroadcastReceiver {
    private SimWifiP2pDeviceList _simWifiP2pDeviceList;
    private Context _context;
    private double _longitude;
    private double _latitude;
    private boolean _mLocationPermissionGranted = false;
    private static final String TAG ="SendWifiDirectBReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "BORADCAST RECEIVED");
        switch (intent.getAction()) {
            case LocMessIntent.NEW_PEERS_AVAILABLE:
                new SendWifiMessagesAsync().execute();
                break;
        }
    }

    public void start(){
        new SendWifiMessagesAsync().execute();
    }

    public void set_simWifiP2pDeviceList(SimWifiP2pDeviceList _simWifiP2pDeviceList) {
        this._simWifiP2pDeviceList = _simWifiP2pDeviceList;
    }

    public void set_context(Context _context) {
        this._context = _context;
    }

    public void set_longitude(double _longitude) {
        this._longitude = _longitude;
    }

    public void set_latitude(double _latitude) {
        this._latitude = _latitude;
    }

    public /*static */class SendWifiMessagesAsync extends AsyncTask<Void, Void, Void> {
        private SQLDataStoreHelper _db;
        private Cursor cursor;
        private ArrayList<Integer> GPS_location;
        private ArrayList<Integer> WIFI_location;
        private static final String TAG = "SendWifiMessagesAsync";
        // private Context _context;

  /*  public SendWifiMessagesAsync(Context c){
     _context = c;
    }*/

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "onPreExecute");
            super.onPreExecute();
            GPS_location = new ArrayList<>();
            _db = new SQLDataStoreHelper(_context);
            cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES,
                    DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                    "jumped = 0 OR jumped = 1",
                    null, null, null, null
            );
           /* if (!_mLocationPermissionGranted) {
                if (ContextCompat.checkSelfPermission(_context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    _mLocationPermissionGranted = true;
                    getGPSLocation();

                }
            } else {*/
                getGPSLocation();

           // }
            getWIFILocation();

        }

        private void getGPSLocation() {

            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_GPS_LOCATION,
                    DataStore.SQL_GPS_LOCATION_COLUMNS,
                    "enabled = 1",
                    null, null, null, null
            );
            cursor.moveToFirst();
            Location location = new Location("");
            location.setLongitude(_longitude);
            location.setLatitude(_latitude);

            double longitude;
            double latitude;
            Location loc;
            int radius;
            while (cursor.moveToNext()) {
                longitude = cursor.getDouble(2);
                latitude = cursor.getDouble(1);
                radius = cursor.getInt(3);
                loc = new Location("");
                loc.setLongitude(longitude);
                loc.setLatitude(latitude);
                if (loc.distanceTo(location) <= radius) {
                    GPS_location.add(cursor.getInt(0));

                }
            }
        }


        private void getWIFILocation() {
            List<String> ssid_list = LocMessMainService.getInstance().getSsidList();
            Object[] ssid_list_object =  ssid_list.toArray();


            String sql = "(0 = 1) ";
            String ssid_list_string = "";
            boolean k = false;
            for (String ssid : ssid_list) {
                sql += " OR (ssid = ? AND enabled = 1)";
                if (k)
                    ssid_list_string += "::" +ssid;
                else {
                    ssid_list_string += ssid;
                    k = true;
                }
            }
            Log.d(TAG, "SQL: " + sql);
            String[] ssid_list_array = null;
            if(k)
                ssid_list_array = ssid_list_string.split("::");

            Cursor cursor = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_LOCATION_SSID,
                    DataStore.SQL_WIFI_LOCATION_SSID_COLUMNS,
                    sql,
                    ssid_list_array,
                    null, null, null
            );
            cursor.moveToFirst();

            WIFI_location = new ArrayList<>();
            while (cursor.moveToNext()) {
                WIFI_location.add(cursor.getInt(0));
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e(TAG, "doInBackground");


            Collection<SimWifiP2pDevice> devices = _simWifiP2pDeviceList.getDeviceList();

            messageLoop:
            while (cursor.moveToNext()) {
                Log.e(TAG, "checking message: " + cursor.getString(0)+ " // " + cursor.getString(1) + " // " + cursor.getString(6));
                int jumped = cursor.getInt(6); //jumped
                if (jumped == 1) {
                    int a_int = cursor.getInt(3); //location_id
                    for (Integer loc : GPS_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                    for (Integer loc : WIFI_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                } else { // jumped == 0
                    int a_int = cursor.getInt(3); //location_id
                    for (Integer loc : GPS_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                    for (Integer loc : WIFI_location) {
                        if (loc == a_int) {
                            sendAll(devices, 1);
                            continue messageLoop;
                        }
                    }
                    sendAll(devices, 0);
                }
            }


            return null;
        }

        private void sendAll(Collection<SimWifiP2pDevice> device_list, int jumped) {
            String[] selectionArgs = {cursor.getString(0)};

            Cursor c = _db.getReadableDatabase().query(
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS,
                    DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS_COLUMNS,
                    "message_id = ?",
                    selectionArgs,
                    null,null, null
            );
            int number_restrictions = 0;
            String appendable ="";
            while (c.moveToNext()){
                number_restrictions++;
                appendable +=
                        "::" + c.getString(1) + //keyword_id
                                "::" + c.getString(2) + //keyword_value
                                "::" + c.getInt(3); //equal
            }
            String toPass = number_restrictions + appendable;

            String toSend = "MESSAGE" +
                    "::" + cursor.getString(0) +//id
                    "::" + cursor.getString(1) +//content
                    "::" + cursor.getString(2) +//author
                    "::" + cursor.getInt(3) +//location_id
                    "::" + cursor.getString(4) +//time_start
                    "::" + cursor.getString(5) +//time_end
                    "::" + jumped +//jumped
                    "::" + cursor.getString(7) +//timestamp
                    "::" + Base64.encodeToString(cursor.getString(8).getBytes(),Base64.NO_WRAP)  +//signature
                    "::" + cursor.getString(9) +//certificate
                    "::" +  Base64.encodeToString(cursor.getString(10).getBytes(),Base64.NO_WRAP) +//publicKey
                    "::" + toPass
                    ;
            sendAll_part2(device_list, toSend);
        }

        private void sendAll_part2(Collection<SimWifiP2pDevice> device_list, String toSend) {
            SimWifiP2pSocket sock = null;
            for (SimWifiP2pDevice device : device_list) {
                Log.e(TAG, "sendAll_part2, device: " + device.getVirtIp() + "::" +  device.getVirtPort());
                Log.e(TAG, "sendAll_part2, message: " + toSend);
                Log.d(TAG, "signature: " + cursor.getString(8));
                try {
                    sock = new SimWifiP2pSocket(device.getVirtIp(), device.getVirtPort());
                    OutputStream sockOut = sock.getOutputStream();
                    sockOut.write(toSend.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        sock.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
