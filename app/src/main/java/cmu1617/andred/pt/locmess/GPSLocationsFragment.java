package cmu1617.andred.pt.locmess;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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
    protected void onItemClick(RecyclerViewAdapter mAdapter, int position) {
            GPSLocation location = (GPSLocation) mAdapter.getItem(position);
            Intent i = new Intent(getActivity(), ShowGPSLocation.class);
            i.putExtra("name", location.name());
            i.putExtra("latitude", location.latitude());
            i.putExtra("longitude", location.longitude());
            i.putExtra("radius", location.radius());

            startActivity(i);
    }

    @Override
    public RecyclerViewAdapter createNewAdapter() {
        return new GPSRecycleViewAdapter();
    }

    @Override
    public void onClick(View v) {
       switch(v.getId()){
           case R.id.add_location:
               startActivity(new Intent(getActivity(), NewGPSLocation.class));
       }
    }


    private class GPSRecycleViewAdapter extends RecyclerViewAdapter {

        List<LocMessLocation> locations = new ArrayList<>();


        GPSRecycleViewAdapter(){
            locations = new ArrayList<>();
            String[] columns = {"location_id"};
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_GPS_LOCATION, //table name
                    columns, //columns to return
                    "enabled = 1", //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null
            );
            while(cursor.moveToNext()){
                locations.add(new GPSLocation(_dbHelper,cursor.getString(0)));
            }
        }

        public LocMessLocation getItem(int position) {
            return locations.get(position);
        }


        public int getItemCount() {
            return locations.size();
//
//            String[] columns = {"location_id"};
//            Cursor cursor = _dbHelper.getReadableDatabase().query(
//                    true, //distinct
//                    DataStore.SQL_GPS_LOCATION, //table name
//                    columns, //columns to return
//                    "enabled = 1", //selection string
//                    null, //selection args
//                    null, //groupBy
//                    null, //having
//                    null, //orderBy
//                    null
//            );
//            int c = cursor.getCount();
//            cursor.close();
//            Log.d("GPS LOCATIONS",c+"");
//            return c;
        }



    }
    @Override
    protected void disableLocation(String location_id) {
        ContentValues values = new ContentValues();
        values.put("enabled",0);
        _dbHelper.getWritableDatabase().update(DataStore.SQL_GPS_LOCATION,values,"location_id = ?",new String[]{location_id});
    }




}
