package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by Miguel on 15/04/2017.
 */

public class LocMessReadMessage {
    private SQLDataStoreHelper _db;
    private String _id;
    private LocMessLocation _location;
    private String _authorId;
    private String _readerId;
    private String _content;
    private String _postTimestamp;

    //    private DateT
    public LocMessReadMessage(SQLDataStoreHelper dbHelper, String message_id) {
        _id = message_id;
        _db = dbHelper;

        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_READ_MESSAGES, //table name
                DataStore.SQL_READ_MESSAGES_COLUMNS, //columns to return
                "message_id = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("message_id", _id);
            _db.getWritableDatabase().insert(DataStore.SQL_READ_MESSAGES,
                    null,
                    values);
        }
        cursor.close();
    }

    public void completeObject(String locationId,String authorId, String content,String readerId,String postTimestamp) {
        this.location(locationId);
        this.authorId(authorId);
        this.readerId(readerId);
        this.content(content);
        this.postTimestamp(postTimestamp);
    }

    public String id() {
        return _id;
    }

    public void content(String content) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("content", content);
        _db.getWritableDatabase().update(DataStore.SQL_READ_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _content = content;
    }
    public String content() {
        if (_content != null) return _content;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_READ_MESSAGES,
                DataStore.SQL_READ_MESSAGES_COLUMNS,
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
        _db.getWritableDatabase().update(DataStore.SQL_READ_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _authorId = authorId;
    }
    public String authorId() {
        if (_authorId != null) return _authorId;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_READ_MESSAGES,
                DataStore.SQL_READ_MESSAGES_COLUMNS,
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

    public void readerId(String readerId) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("reader_id", readerId);
        _db.getWritableDatabase().update(DataStore.SQL_READ_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _readerId = readerId;
    }
    public String readerId() {
        if (_readerId != null) return _readerId;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_READ_MESSAGES,
                DataStore.SQL_READ_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _readerId =cursor.getString(3);
        return _readerId;
    }

    public void location(String locationId) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("location_id", locationId);
        _db.getWritableDatabase().update(DataStore.SQL_READ_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _location = new LocMessLocation(_db,locationId);
    }
    public LocMessLocation location() {
        if (_location != null) return _location;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_READ_MESSAGES,
                DataStore.SQL_READ_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _location  = new LocMessLocation(_db,cursor.getString(4));
        return _location;
    }

    public void postTimestamp(String postTimestamp) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("post_timestamp", postTimestamp);
        _db.getWritableDatabase().update(DataStore.SQL_READ_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _postTimestamp = postTimestamp;
    }

    public String postTimestamp(){
        if (_postTimestamp!= null) return _postTimestamp;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_READ_MESSAGES,
                DataStore.SQL_READ_MESSAGES_COLUMNS,
                "message_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _postTimestamp =cursor.getString(5);
        return _postTimestamp ;
    }

}
