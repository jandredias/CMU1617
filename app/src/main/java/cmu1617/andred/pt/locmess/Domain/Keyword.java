package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;

/**
 * Created by miguel on 10/04/17.
 */

public class Keyword {
    protected SQLDataStoreHelper _db;
    protected String _id;
    protected String _name;

    public Keyword  (SQLDataStoreHelper db, String id) {
        _db = db;
        _id = id;

        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_KEYWORDS, //table name
                DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                "keyword_id = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("keyword_id", _id);
            _db.getWritableDatabase().insert(DataStore.SQL_KEYWORDS,
                    null,
                    values);
        }
        cursor.close();
    }

    public void completeObject(String name) {
        this.name(name);
    }

    public void name(String name) {

        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("keyword_name", name);
        _db.getWritableDatabase().update(DataStore.SQL_KEYWORDS,
                values,
                "keyword_id = ?",
                selectionArgs);
        _name = name;
    }

    public String name() {
        if (_name != null) return _name;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_KEYWORDS,
                DataStore.SQL_KEYWORDS_COLUMNS,
                "keyword_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        _name =cursor.getString(1);
        return _name;
    }

    public String id(){
        return _id;
    }


}
