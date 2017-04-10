package cmu1617.andred.pt.locmess.Domain;

import android.content.ContentValues;
import android.database.Cursor;

import cmu1617.andred.pt.locmess.DataStore;
import cmu1617.andred.pt.locmess.SQLDataStoreHelper;


/**
 * Created by miguel on 10/04/17.
 */

public class UserKeyword extends Keyword {
    protected String _value;

    public UserKeyword(SQLDataStoreHelper db, String id) {
        super(db,id);

        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_USER_KEYWORDS, //table name
                DataStore.SQL_USER_KEYWORDS_COLUMNS, //columns to return
                "keyword_id = ?",
                selectionArgs,
                null, null, null
        );
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("keyword_id", _id);
            _db.getWritableDatabase().insert(DataStore.SQL_USER_KEYWORDS,
                    null,
                    values);
        }
        cursor.close();
    }

    public void completeObject(String keyword_name,String keyword_value) {
        super.completeObject(keyword_name);
        this.value(keyword_value);
    }

    public String value() {
        if (_value != null) return _value;
        String[] selectionArgs = { _id };
        Cursor cursor = _db.getReadableDatabase().query(DataStore.SQL_USER_KEYWORDS,
                DataStore.SQL_USER_KEYWORDS_COLUMNS,
                "keyword_id = ?",
                selectionArgs,
                null, null, null);

        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    public void value(String value) {
        String[] selectionArgs = {_id};
        ContentValues values = new ContentValues();
        values.put("keyword_value", value);
        _db.getWritableDatabase().update(DataStore.SQL_USER_KEYWORDS,
                values,
                "keyword_id = ?",
                selectionArgs);
        _value = value;
    }
}
