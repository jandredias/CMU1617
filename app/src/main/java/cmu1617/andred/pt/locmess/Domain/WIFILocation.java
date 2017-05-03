package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public void completeObject(String name,List<String> ssidList, String enabled) {
        super.completeObject(name);
        this.ssidList(ssidList);
        this.enabled(enabled);
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
            _db.getWritableDatabase().insertWithOnConflict(DataStore.SQL_WIFI_LOCATION_SSID,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
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
                ssidList.add(cursor.getString(1));
            }
        }
        _ssidList = ssidList;
        return ssidList;

    }
    public boolean enabled(){
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_LOCATION_SSID,
                DataStore.SQL_WIFI_LOCATION_SSID_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.moveToFirst();
        return cursor.getInt(2) == 1;
    }

    public void enabled(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("enabled", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_LOCATION_SSID,
                values,
                "location_id = ?",
                selectionArgs);
    }

}
