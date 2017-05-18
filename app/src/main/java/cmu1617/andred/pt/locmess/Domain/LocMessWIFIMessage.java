package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;
import pt.andred.cmu1617.MessageConstraint;

/**
 * Created by Jorge Veiga on 25/04/2017.
 */

public class LocMessWIFIMessage {
    private final String TAG = "LocMessWIFIMessage";
    private SQLDataStoreHelper _db;
    private String _id;
    private LocMessLocation _location;
    private String _authorId;
    private String _content;
    private String _timeStart;
    private String _timeEnd;
    private String _jumped;
    private String _timestamp;
    private String _signature;
    private String _certificate;
    private String _publicKey;
    private  List<MessageConstraint> _messageConstraints;
    public LocMessWIFIMessage(SQLDataStoreHelper dbHelper) {

        _db = dbHelper;
        _id = new UserProfile(_db).userName() +"::"+ (System.currentTimeMillis()/1000);

        ContentValues values = new ContentValues();
        values.put("message_id", _id);
        _db.getWritableDatabase().insert(DataStore.SQL_WIFI_MESSAGES,
                null,
                values);

    }

    public LocMessWIFIMessage(SQLDataStoreHelper dbHelper, String id){
        _db = dbHelper;
        _id = id;
    }

    public void completeObject(String locationId, String content, String timeStart, String timeEnd,
                               String jumped, String timestamp, String signature, String certificate,
                               String publicKey,  List<MessageConstraint> messageConstraints) {

        this.authorId(new UserProfile(_db).userName());

        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("location_id", locationId);
        values.put("time_start", timeStart);
        values.put("time_end", timeEnd);
        values.put("content", content);
        values.put("jumped", jumped);
        values.put("timestamp", timestamp);
        values.put("signature", signature);
        values.put("certificate", certificate);
        values.put("publicKey", publicKey);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        values = new ContentValues();
        for(MessageConstraint mc : messageConstraints){
            values.put("message_id", _id);
            values.put("keyword_id", mc.getID());
            values.put("keyword_value", mc.getKeywordValue());
            values.put("equal", mc.getEqual());
            _db.getWritableDatabase().insert(DataStore.SQL_WIFI_MESSAGES_RESTRICTIONS,
                    null, values);

        }

        Log.w(TAG,"New message: " + _id + " // " + content + " // " + timeStart + "-//-" + timeEnd);
    }

    public String id() {
        return _id;
    }

    public void timeEnd(String timeEnd) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("time_end", timeEnd);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _timeEnd = timeEnd;
    }

    public String timeEnd() {
        if (_timeEnd != null) return _timeEnd;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _timeEnd =cursor.getString(5);
        return _timeEnd;
    }

    public void timeStart(String timeStart) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("time_start", timeStart);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _timeStart = timeStart;
    }
    public String timeStart() {
        if (_timeStart != null) return _timeStart;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _timeStart =cursor.getString(4);
        return _timeStart;
    }
    public void content(String content) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("content", content);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _content = content;
    }
    public String content() {
        if (_content != null) return _content;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _content =cursor.getString(1);
        return _content ;
    }

    public void authorId(String authorId) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("author_id", authorId);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _authorId = authorId;
    }
    public String authorId() {
        if (_authorId != null) return _authorId;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _authorId =cursor.getString(2);
        return _authorId;
    }

    public void location(String locationId) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("location_id", locationId);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _location = new LocMessLocation(_db,locationId);
    }
    public LocMessLocation location() {
        if (_location != null) return _location;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _location  = new LocMessLocation(_db,cursor.getString(3));
        return _location;
    }

    public String jumped(){
        if (_jumped != null) return _jumped;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();

        _jumped = cursor.getString(6);
        return _jumped;
    }

    public void jumped(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("jumped", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _jumped= enabled;
    }
    public String timestamp(){
        if (_timestamp != null) return _timestamp;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _timestamp = cursor.getString(7);
        return _timestamp;
    }

    public void timestamp(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("timestamp", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _timestamp= enabled;
    }
    public String signature(){
        if (_signature != null) return _signature;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _signature = cursor.getString(8);
        return _signature;
    }

    public void signature(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("signature", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _signature= enabled;
    }
    public String certificate(){
        if (_certificate != null) return _certificate;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _certificate = cursor.getString(9);
        return _certificate;
    }

    public void certificate(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("certificate", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _certificate= enabled;
    }
    public String publicKey(){
        if (_publicKey != null) return _publicKey;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_WIFI_MESSAGES,
                DataStore.SQL_WIFI_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _publicKey = cursor.getString(10);
        return _publicKey;
    }

    public void publicKey(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("publicKey", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_WIFI_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _publicKey= enabled;
    }
}
