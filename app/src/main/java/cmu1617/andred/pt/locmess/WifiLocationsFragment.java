package cmu1617.andred.pt.locmess;

import android.database.Cursor;
import android.util.Log;
import android.view.View;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.WIFILocation;

/**
 * Created by miguel on 06/04/17.
 */


public class WifiLocationsFragment extends ListLocationsFragment {

    @Override
    public RecyclerViewAdapter createNewAdapter() {
        return new WIFIRecycleViewAdapter();
    }

    @Override
    public void onClick(View v) {
        
    }


    private class WIFIRecycleViewAdapter extends RecyclerViewAdapter {
        public LocMessLocation getItem(int position) {
            String[] columns = {"location_id"};
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_WIFI_LOCATION_SSID, //table name
                    columns, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null //limit
            );
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            cursor.moveToPosition(cursor.getCount() - position - 1);

            return new WIFILocation(_dbHelper, cursor.getString(0));
        }


        public int getItemCount() {
            String[] columns = {"location_id"};
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_WIFI_LOCATION_SSID, //table name
                    columns, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null //limit
            );
            int c = cursor.getCount();
            cursor.close();
            Log.d("WIFI LOCATIONS",c+"");
            return c;
        }

    }

}
