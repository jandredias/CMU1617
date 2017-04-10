package cmu1617.andred.pt.locmess;

import android.database.Cursor;
import android.util.Log;

import cmu1617.andred.pt.locmess.Domain.GPSLocation;
import cmu1617.andred.pt.locmess.Domain.LocMessLocation;

/**
 * Created by miguel on 06/04/17.
 */

public class GPSLocationsFragment extends ListLocationsFragment {

    @Override
    public RecyclerViewAdapter createNewAdapter() {
        return new GPSRecycleViewAdapter();
    }


    private class GPSRecycleViewAdapter extends RecyclerViewAdapter {
        public LocMessLocation getItem(int position) {
            String[] columns = {"location_id"};
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_GPS_LOCATION, //table name
                    columns, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null
            );
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            cursor.moveToPosition(cursor.getCount() - position - 1);

            return new GPSLocation(_dbHelper, cursor.getString(0));
        }


        public int getItemCount() {
            String[] columns = {"location_id"};
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_GPS_LOCATION, //table name
                    columns, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null
            );
            int c = cursor.getCount();
            cursor.close();
            Log.d("GPS LOCATIONS",c+"");
            return c;
        }

    }

}
