package cmu1617.andred.pt.locmess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;
import pt.andred.cmu1617.LocMessAPIClientImpl;

/**
 * Created by miguel on 10/04/17.
 */

public class GetMessagesAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private final String Tag = "GetMessagesAsyncTask ";
    private final SQLDataStoreHelper _db;
    public GetMessagesAsyncTask(SQLDataStoreHelper db) {
        _db = db;
    }


    @Override
    protected void onPreExecute() {
        Log.wtf(Tag, "start");
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        String latitude = "0";
        String longitude= "0";
        String last_message_id = "0";
        List<String> ssid_list = new ArrayList<>();
        try {
            JSONArray messagesMap = LocMessAPIClientImpl.getInstance().getMessages(latitude,longitude,ssid_list,last_message_id);
            for (int i = 0; i < messagesMap.length(); i++) {
                JSONObject message_json = messagesMap.getJSONObject(i);
                String name = message_json.getString("name");
                String location_id = message_json.getString("location_id");
                String message_id = message_json.getString("message_id");
                String content = message_json.getString("content");
                String author = message_json.getString("author");
                String time_start = message_json.getString("time_start");
                String time_end = message_json.getString("time_end");
                LocMessMessage message = new LocMessMessage(_db, message_id);
                message.completeObject(location_id,author,content,time_start,time_end);
                Log.d(Tag, "new message");
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
    }


}

