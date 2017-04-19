package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmu1617.andred.pt.locmess.Domain.GPSLocation;
import cmu1617.andred.pt.locmess.Domain.LocMessLocation;

/**
 * Created by miguel on 06/04/17.
 */

public class GPSLocationsFragment extends ListLocationsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public RecyclerViewAdapter createNewAdapter() {
        return new GPSRecycleViewAdapter();
    }

    @Override
    public void onClick(View v) {
       switch(v.getId()){
           case R.id.add_location:
               startActivity(new Intent(getActivity(), NewGPSLocation2.class));
       }

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
