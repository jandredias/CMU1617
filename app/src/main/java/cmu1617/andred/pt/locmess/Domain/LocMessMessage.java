package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by Miguel on 15/04/2017.
 */

public class LocMessMessage {
    private SQLDataStoreHelper _db;
    private String _id;
    private LocMessLocation _location;
    private String _authorId;
    private String _content;
    private String _timeStart;
    private String _timeEnd;
//    private DateT
    public LocMessMessage(SQLDataStoreHelper dbHelper, String message_id) {
        _id = message_id;
        _db = dbHelper;

        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES, //table name
                DataStore.SQL_MESSAGES_COLUMNS, //columns to return
                "message_id = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("message_id", _id);
            _db.getWritableDatabase().insert(DataStore.SQL_MESSAGES,
                    null,
                    values);
        }
        cursor.close();
    }

    public void completeObject(String locationId,String authorId, String content, String timeStart, String timeEnd, String enabled) {
        this.location(locationId);
        this.authorId(authorId);
        this.content(content);
        this.timeStart(timeStart);
        this.timeEnd(timeEnd);
        this.enabled(enabled);
    }

    public String id() {
        return _id;
    }

    public void timeEnd(String timeEnd) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("time_end", timeEnd);
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _timeEnd = timeEnd;
    }

    public String timeEnd() {
        if (_timeEnd != null) return _timeEnd;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES,
                DataStore.SQL_MESSAGES_COLUMNS,
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
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _timeStart = timeStart;
    }
    public String timeStart() {
        if (_timeStart != null) return _timeStart;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES,
                DataStore.SQL_MESSAGES_COLUMNS,
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
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _content = content;
    }
    public String content() {
        if (_content != null) return _content;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES,
                DataStore.SQL_MESSAGES_COLUMNS,
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
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _authorId = authorId;
    }
    public String authorId() {
        if (_authorId != null) return _authorId;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES,
                DataStore.SQL_MESSAGES_COLUMNS,
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
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _location = new LocMessLocation(_db,locationId);
    }
    public LocMessLocation location() {
        if (_location != null) return _location;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES,
                DataStore.SQL_MESSAGES_COLUMNS,
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

    public boolean enabled(){
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_MESSAGES,
                DataStore.SQL_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.moveToFirst();
        return cursor.getInt(6) == 1;
    }

    public void enabled(String enabled) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("enabled", enabled);
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
    }
}
