package cmu1617.andred.pt.locmess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.Keyword;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;
import pt.andred.cmu1617.LocMessAPIClientImpl;

/**
 * Created by miguel on 10/04/17.
 */

public class GetKeyworksAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private final String Tag = "GetKeyworksAsyncTask ";
    private final SQLDataStoreHelper _db;
    private List<Keyword> list = new ArrayList<>();

    public GetKeyworksAsyncTask(SQLDataStoreHelper db) {
        _db = db;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            JSONArray keywordsMap = LocMessAPIClientImpl.getInstance().listKeywords();
            for (int i = 0; i < keywordsMap.length(); i++) {
                JSONObject keyword_json = keywordsMap.getJSONObject(i);
                String name = keyword_json.getString("name");
                String keyword_id = keyword_json.getString("keyword_id");
                Log.d(Tag, "new keyword");
                Keyword keyword = new Keyword(_db, keyword_id);
                keyword.completeObject(name);
                list.add(keyword);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}

