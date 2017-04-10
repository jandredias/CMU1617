package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by Miguel on 08/04/2017.
 */

public class WIFILocation extends LocMessLocation {

    private List<String> _ssidList;

    public WIFILocation(SQLDataStoreHelper dbHelper, String location_id) {
        super(dbHelper,location_id);
    }

    public void completeObject(String name,List<String> ssidList) {
        super.completeObject(name);
        this.ssidList(ssidList);
    }

    public void ssidList(List<String> ssidList) {
        String[] selectionArgs = {this.id()};
        ContentValues values = new ContentValues();
        values.put("location_id", this.id());
        for(String ssid : ssidList) {
            if(values.containsKey("ssid")) {
                values.remove("ssid");
            }
            values.put("ssid", ssid);
            _db.getWritableDatabase().insert(DataStore.SQL_WIFI_LOCATION_SSID,
                    null,
                    values
            );

        }
        _ssidList = ssidList;
    }

    public List<String> ssidList() {
        if(_ssidList != null) return null;
        String[] selectionArgs = { this.id() };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_WIFI_LOCATION_SSID,
                DataStore.SQL_WIFI_LOCATION_SSID_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        List<String> ssidList = new ArrayList<String>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                ssidList.add(cursor.getString(2));
            }
        }
        _ssidList = ssidList;
        return ssidList;

    }

}
