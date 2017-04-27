package cmu1617.andred.pt.locmess;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.WIFILocation;

/**
 * Created by miguel on 06/04/17.
 */


public class WifiLocationsFragment extends ListLocationsFragment {

    private String ssid;

    @Override
    public RecyclerViewAdapter createNewAdapter() {
        return new WIFIRecycleViewAdapter();
    }

    @Override
    protected void disableLocation(String location_id) {
        ContentValues values = new ContentValues();
        values.put("enabled", 0);
        _dbHelper.getWritableDatabase().update(DataStore.SQL_WIFI_LOCATION_SSID, values, "location_id = ?", new String[]{location_id});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_location:
                /*if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (action)) {
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {*/
                startActivity(new Intent(getActivity(), NewWIFILocation.class));
                        /*WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo info = wifiManager.getConnectionInfo();
                        ssid = info.getSSID();
                        new SimpleDialog(this, ssid).show(getFragmentManager(), TAG);*/
                   /* }
                }*/
        }

    }


    private class WIFIRecycleViewAdapter extends RecyclerViewAdapter {
        public LocMessLocation getItem(int position) {
            String[] columns = {"location_id"};
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_WIFI_LOCATION_SSID, //table name
                    columns, //columns to return
                    "enabled = 1", //selection string
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
                    "enabled = 1", //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null //limit
            );
            int c = cursor.getCount();
            cursor.close();
            Log.d("WIFI LOCATIONS", c + "");
            return c;
        }

    }

    @Override
    protected void onItemClick(RecyclerViewAdapter mAdapter, int position) {
        LocMessLocation location = mAdapter.getItem(position);
        Intent i = new Intent(getActivity(), ShowWIFILocation.class);
        i.putExtra("id", location.id());

        startActivity(i);
    }

//    public void setNewLocation(String name, String ssid) {
//        new NewWIFILocationAsync().execute(name, ssid);
//    }
//
//    @SuppressLint("ValidFragment")
//    public static class SimpleDialog extends DialogFragment {
//
//        private WifiLocationsFragment _n;
//        private String _s;
//
//        public SimpleDialog(WifiLocationsFragment n, String s) {
//            _n = n;
//            _s = s;
//        }
//
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            // Get the layout inflater
//            LayoutInflater inflater = getActivity().getLayoutInflater();
//            View v = inflater.inflate(R.layout.name_location_dialog, null);
//
//            final EditText editText = (EditText) v.findViewById(R.id.written_text);
//            final TextView t = (TextView) v.findViewById(R.id.initial);
//            t.setVisibility(View.VISIBLE);
//            t.setText(getActivity().getString(R.string.new_location_wifi_1) + _s + getActivity().getString(R.string.new_location_wifi_2));
//
//            builder.setView(v)
//                    // Add action buttons
//                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            _n.setNewLocation(editText.getText().toString(), _s);
//                            getDialog().cancel();
//
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            getDialog().cancel();
//                        }
//                    });
//            return builder.create();
//
//        }
//    }
//
//    public static class NewWIFILocationAsync extends AsyncTask<String, String, String> {
//
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            JSONObject result;
//            try {
//                result = LocMessAPIClientImpl.getInstance().newWIFILocation(params[0], params[1]);
//                if (result.getInt("status") != 200) {
//                    Log.e(TAG, "Error in sending to server, status= " + result.getInt("status"));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, "not sent to server, there was an exception");
//            }
//            Log.d(TAG, "Sent to server and everything is fine");
//            return null;
//        }
//    }

}
