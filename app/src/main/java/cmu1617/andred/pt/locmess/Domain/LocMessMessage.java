package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import cmu1617.andred.pt.locmess.CryptographicOperations.CryptographicOperations;
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
    private String _postTimestamp;

    private byte[] _signature;
    private byte[] _user_certificate;
    private byte[] _authorPublicKey;


    private String Tag = "LocMessMessage";


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

    public void completeObject(String locationId,String authorId, String content, String timeStart, String timeEnd, String postTimestamp ,String enabled,String signature, String certificate, String authorPublicKey) {
        this.location(locationId);
        this.authorId(authorId);
        this.content(content);
        this.timeStart(timeStart);
        this.timeEnd(timeEnd);
        this.postTimestamp(postTimestamp);
        this.enabled(enabled);
        this.signature(signature);
        this.certificate(certificate);
        this.authorPublicKey(authorPublicKey);
        this.validateMessage();
    }

    private void validateMessage() {
        Log.wtf(Tag,"validating message");
        byte[] location = new byte[0];
        try {
            location = this.location().id().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] authorId = new byte[0];
        try {
            authorId = this.authorId().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] content = new byte[0];
        try {
            content = this.content().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] timeStart = new byte[0];
        try {
            timeStart = this.timeStart().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] timeEnd = new byte[0];
        try {
            timeEnd = this.timeEnd().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] postTimestamp = new byte[0];
        try {
            postTimestamp = this.postTimestamp().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


//        Log.w(Tag,"size: " + new String(location).length()+" location: " + Base64.encodeToString(location,Base64.URL_SAFE));
//        Log.w(Tag,"size: " + new String(authorId).length()+" authorId: " + Base64.encodeToString(authorId,Base64.URL_SAFE));
        Log.w(Tag,"size: " + new String(content).length()+" content: " + new String(content) + "|");
//        Log.w(Tag,"size: " + new String(timeStart).length()+" timeStart: " + Base64.encodeToString(timeStart,Base64.URL_SAFE));
//        Log.w(Tag,"size: " + new String(timeEnd).length()+" timeEnd: " + Base64.encodeToString(timeEnd,Base64.URL_SAFE));
//        Log.w(Tag,"size: " + new String(postTimestamp).length()+" postTimestamp: " + Base64.encodeToString(postTimestamp,Base64.URL_SAFE));

        byte[] toVerifyCertificate = CryptographicOperations.concat(authorId,_authorPublicKey);
        boolean certificateValid = CryptographicOperations.verifyDigitalSignature(_user_certificate,toVerifyCertificate,CryptographicOperations.getServerPublicKey());

        if(!certificateValid) {
            eraseMessage();
            return;
        }
        Log.wtf(Tag,"certificate is valid");

        byte[] toSign = CryptographicOperations.concat(location, authorId, content, timeStart, timeEnd, postTimestamp);
//        Log.w(Tag,"size: " + new String(toSign).length()+" Concat: " + Base64.encodeToString(toSign,Base64.DEFAULT));
//        Log.w(Tag,"size: " + new String(_authorPublicKey).length()+" Public: " + Base64.encodeToString(_authorPublicKey,Base64.DEFAULT));
//        Log.w(Tag,"size: " + new String(toSign).length()+" ToSign: " + Base64.encodeToString(toSign,Base64.DEFAULT));
//        Log.w(Tag,"size: " + new String(_signature).length()+" Signat: " + Base64.encodeToString(_signature,Base64.DEFAULT));


        boolean valid = CryptographicOperations.verifyDigitalSignature(_signature,toSign,_authorPublicKey);
//        Log.w(Tag,"valid: " + valid);
        Log.w(Tag,"message is valid: " + valid);

        if(!valid) {eraseMessage(); return;}
    }

    public void eraseMessage(){
        Log.wtf(Tag,"message erased: " + _id);
        String[] selectionArgs = {_id};
        _db.getWritableDatabase().delete(
                DataStore.SQL_MESSAGES,
                "message_id = ?",
                selectionArgs
        );
        this._authorId=null;
        this._content=null;
        this._timeStart=null;
        this._timeEnd=null;
        this._postTimestamp=null;
        this._signature=null;
        this._user_certificate=null;
        this._authorPublicKey=null;
        this._id = null;
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

    public void postTimestamp(String postTimestamp) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("post_timestamp", postTimestamp);
        _db.getWritableDatabase().update(DataStore.SQL_MESSAGES,
                values,
                "message_id = ?",
                selectionArgs);
        _postTimestamp = postTimestamp;
    }

    public String postTimestamp(){
        if (_postTimestamp!= null) return _postTimestamp;
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
        _postTimestamp =cursor.getString(6);
        return _postTimestamp ;
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
        return cursor.getInt(7) == 1;
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

    public void certificate(String certificate) {
        byte[] certificate_bytes = Base64.decode(certificate,Base64.URL_SAFE);
        _user_certificate = certificate_bytes;
    }

    public void signature(String signature) {
        byte[] signature_bytes = Base64.decode(signature,Base64.URL_SAFE);
        _signature = signature_bytes;
    }

    public void authorPublicKey(String authorPublicKey ) {
        byte[] authorPublicKey_bytes = Base64.decode(authorPublicKey,Base64.URL_SAFE);
        _authorPublicKey = authorPublicKey_bytes;
    }

    public static String produce_signature(String privateKey, String locationId,String authorId,String content,String timeStart, String timeEnd,String postTimestamp) {
        byte[] privateKeyBytes = Base64.decode(privateKey,Base64.URL_SAFE);

        byte[] bytes_location      = new byte[0];
        byte[] bytes_timeStart     = new byte[0];
        byte[] bytes_timeEnd       = new byte[0];
        byte[] bytes_postTimestamp = new byte[0];
        byte[] bytes_content       = new byte[0];
        byte[] bytes_authorId      = new byte[0];
        try {
            bytes_location = locationId.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            bytes_authorId = authorId.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            bytes_content = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            bytes_timeStart = timeStart.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            bytes_timeEnd = timeEnd.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            bytes_postTimestamp = postTimestamp.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        Log.w("Producing Signature","size: " + new String(bytes_location     ).length()+ " location: "  +      Base64.encodeToString(bytes_location     ,Base64.URL_SAFE));
//        Log.w("Producing Signature","size: " + new String(bytes_authorId     ).length()+ " authorId: "  +      Base64.encodeToString(bytes_authorId     ,Base64.URL_SAFE));
//        Log.w("Producing Signature","size: " + new String(bytes_content      ).length()+ " content: "  +       Base64.encodeToString(bytes_content      ,Base64.URL_SAFE));
//        Log.w("Producing Signature","size: " + new String(bytes_timeStart    ).length()+ " timeStart: "  +     Base64.encodeToString(bytes_timeStart    ,Base64.URL_SAFE));
//        Log.w("Producing Signature","size: " + new String(bytes_timeEnd      ).length()+ " timeEnd: "  +       Base64.encodeToString(bytes_timeEnd      ,Base64.URL_SAFE));
//        Log.w("Producing Signature","size: " + new String(bytes_postTimestamp).length()+ " postTimestamp: "  + Base64.encodeToString(bytes_postTimestamp,Base64.URL_SAFE));

        byte[] toSign = CryptographicOperations.concat(bytes_location, bytes_authorId, bytes_content, bytes_timeStart, bytes_timeEnd, bytes_postTimestamp);
//        Log.wtf("Produce Signature","Concat: " + Base64.encodeToString(toSign,Base64.URL_SAFE));
//        Log.wtf("Produce Signature","size: " + new String(toSign).length()+" Concat: " + Base64.encodeToString(toSign,Base64.DEFAULT));

        byte[] signature = CryptographicOperations.makeDigitalSignature(toSign,privateKeyBytes);

        return Base64.encodeToString(signature,Base64.URL_SAFE);
    }
}
