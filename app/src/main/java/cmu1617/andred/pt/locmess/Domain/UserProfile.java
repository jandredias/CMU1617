package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;

import cmu1617.andred.pt.locmess.CryptographicOperations.CryptographicOperations;
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
    protected String _certificate;
    protected String _privateKey;

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


    public String newUser() {
        KeyPair keyPair = CryptographicOperations.generateKeys();

        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        String publicKeyString = Base64.encodeToString(publicKeyBytes,Base64.URL_SAFE);
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        String privateKeyString = Base64. encodeToString(privateKeyBytes,Base64.URL_SAFE);

        String[] selectionArgs = new String[]{_userName};
        ContentValues values = new ContentValues();
        values.put("private_key", privateKeyString);
        _db.getWritableDatabase().update(DataStore.SQL_LOGIN,
                values,
                "username = ?",
                selectionArgs);
        return publicKeyString;
    }

    public void user_certificate(String certificate) {
        String[] selectionArgs = new String[]{_userName};
        ContentValues values = new ContentValues();
        values.put("user_certificate", certificate);
        _db.getWritableDatabase().update(DataStore.SQL_LOGIN,
                values,
                "username = ?",
                selectionArgs);

        Log.wtf("user_certificate","set");
    }
}