package cmu1617.andred.pt.locmess.AsyncTasks;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
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
    private String _longitude = "";
    private String _latitude = "";
    private GoogleApiClient mGoogleApiClient;
    private OnTaskCompleted _listenerEnd;
    private int numberMessages;

    public GetMessagesAsyncTask(SQLDataStoreHelper db, OnTaskCompleted listener) {
        _db = db;
        _listenerEnd = listener;
    }



    @Override
    protected void onPreExecute() {

        Log.d(Tag, "start");


    }

    protected void disableAllMessages() {
        ContentValues values = new ContentValues();
        values.put("enabled","0");
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,values,null,null);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Log.d(Tag,"latitude: "+_latitude);
            Log.d(Tag,"longitude: "+_longitude);
            Log.d(Tag,"ssid_list: "+_ssid_list.toString() + " :size: " + _ssid_list.size());
            JSONArray messagesMap = LocMessAPIClientImpl.getInstance().getMessages(_latitude,_longitude,_ssid_list);

            disableAllMessages();
//            _db.getWritableDatabase().execSQL(DataStore.SQL_DELETE_MESSAGES);
//            _db.getWritableDatabase().execSQL(DataStore.SQL_CREATE_MESSAGES);


            numberMessages = messagesMap.length();
            for (int i = 0; i < numberMessages; i++) {
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
                message.completeObject(location_id,author,content,time_start,time_end,"1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean bogus){
        // your stuff
        Log.d(Tag, "end");
        _listenerEnd.onTaskCompleted(numberMessages);
    }


    public void setLongitude(Double longitude) {
        if (longitude != null) {
            _longitude = longitude.toString();
        } else {
            _longitude = "";
        }
    }
    public void setLatitude(Double latitude) {
        if (latitude != null) {
            _latitude = latitude.toString();
        } else {
            _latitude = "";
        }
    }

    public void setSsidList(List<String> ssidList) {
        _ssid_list = ssidList;
    }

}

