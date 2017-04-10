package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by Miguel on 08/04/2017.
 */

public class GPSLocation extends LocMessLocation {
    private int _radius;
    private double _longitude;
    private double _latitude;

    public GPSLocation(SQLDataStoreHelper dbHelper, String location_id) {
        super(dbHelper, location_id);
    }

    /**
     * @param name
     * @param latitude
     * @param longitude
     * @param radius
     */
    public void completeObject(String name,double latitude,double longitude, int radius) {
        super.completeObject(name);



        ContentValues values = new ContentValues();
        values.put("location_id", this.id());
        values.put("radius", radius);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        _db.getWritableDatabase().insertWithOnConflict(DataStore.SQL_GPS_LOCATION,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        _radius = radius;
        _longitude = longitude;
        _latitude = latitude;

    }

    private void radius(int radius) {
        String[] selectionArgs = {this.id()};
        ContentValues values = new ContentValues();
        values.put("radius", radius);
        _db.getWritableDatabase().update(DataStore.SQL_GPS_LOCATION,
                values,
                "location_id = ?",
                selectionArgs);
        _radius = radius;

    }

    private void longitude(double longitude) {
        String[] selectionArgs = {this.id()};
        ContentValues values = new ContentValues();
        values.put("longitude", longitude);
        _db.getWritableDatabase().update(DataStore.SQL_GPS_LOCATION,
                values,
                "location_id = ?",
                selectionArgs);
        _longitude = longitude;
    }


    private void latitude(double latitude) {
        String[] selectionArgs = {this.id()};
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        _db.getWritableDatabase().update(DataStore.SQL_GPS_LOCATION,
                values,
                "location_id = ?",
                selectionArgs);
        _latitude = latitude;
    }

    public double longitude() {
        if (_longitude != 0) return _longitude;
        String[] selectionArgs = { this.id() };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_GPS_LOCATION,
                DataStore.SQL_GPS_LOCATION_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return 0;
        }
        cursor.moveToFirst();
        _longitude =cursor.getDouble(4);
        return _longitude;
    }

    public int radius() {
        if (_radius != 0) return _radius;
        String[] selectionArgs = { this.id() };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_GPS_LOCATION,
                DataStore.SQL_GPS_LOCATION_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return 0;
        }
        cursor.moveToFirst();
        _radius =cursor.getInt(5);
        return _radius;
    }

    public double latitude() {
        if (_latitude != 0) return _latitude;
        String[] selectionArgs = { this.id() };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_GPS_LOCATION,
                DataStore.SQL_GPS_LOCATION_COLUMNS,
                "location_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return 0;
        }
        cursor.moveToFirst();
        _latitude =cursor.getDouble(3);
        return _latitude;
    }
}
