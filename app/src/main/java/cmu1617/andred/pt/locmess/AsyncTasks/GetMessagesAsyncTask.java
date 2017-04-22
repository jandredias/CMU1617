package cmu1617.andred.pt.locmess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.Settings;
import cmu1617.andred.pt.locmess.LocMessMainService;
import cmu1617.andred.pt.locmess.OnTaskCompleted;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;
import pt.andred.cmu1617.LocMessAPIClientImpl;

/**
 * Created by miguel on 10/04/17.
 */

public class GetMessagesAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private final String Tag = "GetMessagesAsyncTask ";
    private final SQLDataStoreHelper _db;
    private List<String> _ssid_list = new ArrayList<>();
    private int _last_message_id = 0;
    private String _longitude = "";
    private String _latitude = "";
    private GoogleApiClient mGoogleApiClient;
    private OnTaskCompleted _listenerEnd;
    public GetMessagesAsyncTask(SQLDataStoreHelper db, OnTaskCompleted listener) {
        _db = db;
        _listenerEnd = listener;
    }


    @Override
    protected void onPreExecute() {

        Log.wtf(Tag, "start");


    }
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Log.d(Tag,"latitude: "+_latitude);
            Log.d(Tag,"longitude: "+_longitude);
            Log.d(Tag,"ssid_list: "+_ssid_list.toString() + " :size: " + _ssid_list.size());
            Log.d(Tag,"last_message_id: "+_last_message_id);
            JSONArray messagesMap = LocMessAPIClientImpl.getInstance().getMessages(_latitude,_longitude,_ssid_list,_last_message_id+"");
            if (messagesMap != null) {
                _db.getWritableDatabase().execSQL(DataStore.SQL_DELETE_MESSAGES);
                _db.getWritableDatabase().execSQL(DataStore.SQL_CREATE_MESSAGES);
            }
            for (int i = 0; i < messagesMap.length(); i++) {
                JSONObject message_json = messagesMap.getJSONObject(i);
                String location_name = message_json.getString("name");
                String location_id = message_json.getString("location_id");
                String message_id = message_json.getString("message_id");
                String content = message_json.getString("content");
                String author = message_json.getString("author");
                String time_start = message_json.getString("time_start");
                String time_end = message_json.getString("time_end");

                new LocMessLocation(_db,location_id).name(location_name);

                LocMessMessage message = new LocMessMessage(_db, message_id);
                message.completeObject(location_id,author,content,time_start,time_end);
                Log.d(Tag, "new message");
                if(message_json.getInt("message_id") > _last_message_id ) {
                    _last_message_id = message_json.getInt("message_id");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean bogus){
        // your stuff
        Log.wtf(Tag, "end");
        _listenerEnd.onTaskCompleted();
    }


    public void setLongitude(double longitude) {
        _longitude = longitude + "";
    }
    public void setLatitude(double latitude) {
        _latitude = latitude + "";
    }

    public void setSsidList(List<String> ssidList) {
        _ssid_list = ssidList;
    }
}

