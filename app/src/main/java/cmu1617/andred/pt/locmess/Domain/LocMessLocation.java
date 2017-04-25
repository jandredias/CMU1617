package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by miguel on 07/04/17.
 */

public class LocMessLocation {

    protected String _id;
    private String _name;
    private boolean _gps = false;
    protected SQLDataStoreHelper _db;

    public LocMessLocation(SQLDataStoreHelper dbHelper, String location_id) {
        _db = dbHelper;
        _id = location_id;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_LOCATION, //table name
                DataStore.SQL_LOCATION_COLUMNS, //columns to return
                "location_id = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("location_id", _id);
            _db.getWritableDatabase().insert(DataStore.SQL_LOCATION,
                    null,
                    values);
        }
        cursor.close();
       /* Cursor cursor2 = _db.getReadableDatabase().query(
                DataStore.SQL_GPS_LOCATION, //table name
                DataStore.SQL_LOCATION_COLUMNS, //columns to return
                "location_id = ?",
                selectionArgs,
                null, null, null
        );*/


    }

    protected void completeObject(String name) {
        this.name(name);
    }

    public String id(){
        return _id;
    }

    public void name(String name) {

        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("name", name);
        _db.getWritableDatabase().update(DataStore.SQL_LOCATION,
                values,
                "location_id = ?",
                selectionArgs);
        _name = name;
    }

    public String name() {
        if (_name != null) return _name;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_LOCATION,
                DataStore.SQL_LOCATION_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _name = cursor.getString(1);
        return _name;
    }

}
