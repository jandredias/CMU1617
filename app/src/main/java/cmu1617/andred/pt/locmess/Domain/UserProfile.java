package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by miguel on 10/04/17.
 */

public class UserProfile {
    protected SQLDataStoreHelper _db;
    protected String _userName;
    protected String _accessToken;
    protected String _refreshToken;

    public UserProfile(SQLDataStoreHelper dbHelper) {
        _db = dbHelper;
        String[] selectionArgs = {"1"};
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_LOGIN, //table name
                DataStore.SQL_LOGIN_COLUMNS, //columns to return
                "valid = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            //Damn... there is no current user then..
        } else {
            cursor.moveToFirst();
            _userName = cursor.getString(0);
            _accessToken = cursor.getString(1);
            _refreshToken= cursor.getString(2);
        }
        cursor.close();
    }

    //New login
    public void newLogin(String userName, String accessToken, String refreshToken) {
        _userName = userName;
        _accessToken = accessToken;
        _refreshToken = refreshToken;

        //Erase all others
        String[] selectionArgs = {"1"};
        ContentValues values = new ContentValues();
        values.put("valid", 0);
        _db.getWritableDatabase().update(DataStore.SQL_LOGIN,
                values,
                "valid = ?",
                selectionArgs);

        //Put new login
        values = new ContentValues();
        values.put("username", _userName);
        values.put("access_token", _accessToken);
        values.put("refresh_token", _refreshToken);
        values.put("valid", 1);


        _db.getWritableDatabase().insertWithOnConflict(DataStore.SQL_LOGIN,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

    }

    public String userName() {
        return _userName;
    }
    public String refreshToken() {
        return _refreshToken;
    }
    public String accessToken() {
        return _accessToken;
    }


}