package cmu1617.andred.pt.locmess;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by miguel on 07/04/17.
 */

class LocMessLocation {

    private String _id;
    private String _name;
    private SQLDataStoreHelper _db;

    public LocMessLocation(SQLDataStoreHelper dbHelper, String location_id) {
        _db = dbHelper;
        _id = location_id;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_LOCATION, //table name
                DataStore.SQL_WIFI_LOCATION_COLUMNS, //columns to return
                "location_id = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("location_id", _id);
            _db.getWritableDatabase().insert(DataStore.SQL_WIFI_LOCATION,
                    null,
                    values);
        }
        cursor.close();
    }

    public void name(String name) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("name", name);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_LOCATION,
                values,
                "location_id = ?",
                selectionArgs);
    }

    public String name() {
        if (_name != null) return _name;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_WIFI_LOCATION,
                DataStore.SQL_WIFI_LOCATION_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(1);
    }

}
