package cmu1617.andred.pt.locmess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andre on 21/01/17.
 */

public class SQLDataStoreHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "LocMess.db";

    public SQLDataStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_LOCATION);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_WIFI_LOCATION_SSID);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_GPS_LOCATION);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_LOGIN);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_KEYWORDS);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_USER_KEYWORDS);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_MESSAGES);
        sqLiteDatabase.execSQL(DataStore.SQL_CREATE_READ_MESSAGES);




//        sqLiteDatabase.execSQL(DataStore.SQL_POPULATE_WIFI_LOCATION);
        sqLiteDatabase.execSQL(DataStore.SQL_POPULATE_MESSAGES);
//        sqLiteDatabase.execSQL(DataStore.SQL_POPULATE_WIFI_LOCATION_SSID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_LOCATION);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_WIFI_LOCATION_SSID);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_GPS_LOCATION);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_LOGIN);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_KEYWORDS);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_USER_KEYWORDS);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_MESSAGES);
        sqLiteDatabase.execSQL(DataStore.SQL_DELETE_READ_MESSAGES);

        onCreate(sqLiteDatabase);
    }
}
