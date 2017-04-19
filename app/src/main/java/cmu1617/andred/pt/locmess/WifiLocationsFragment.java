package cmu1617.andred.pt.locmess;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.WIFILocation;
import pt.andred.cmu1617.LocMessAPIClientImpl;

import static android.content.ContentValues.TAG;

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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_location:
                /*if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals (action)) {
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {*/
                        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo info = wifiManager.getConnectionInfo();
                        ssid = info.getSSID();
                        new SimpleDialog(this, ssid).show(getFragmentManager(), TAG);
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

    public void setNewLocation(String name, String ssid){
        new NewWIFILocationAsync().execute(name, ssid);
    }

    @SuppressLint("ValidFragment")
    public static class SimpleDialog extends DialogFragment {

        private WifiLocationsFragment _n;
        private String _s;

        public SimpleDialog(WifiLocationsFragment n, String s) {
            _n = n;
            _s = s;
        }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.name_location_dialog, null);

        final EditText editText = (EditText) v.findViewById(R.id.written_text);
        final TextView t = (TextView) v.findViewById(R.id.initial);
        t.setVisibility(View.VISIBLE);
        t.setText(getActivity().getString(R.string.new_location_wifi_1) + _s + getActivity().getString(R.string.new_location_wifi_2));

        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       _n.setNewLocation(editText.getText().toString(), _s);
                        getDialog().cancel();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();

    }
}

public static class NewWIFILocationAsync extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... params) {
        try {
            LocMessAPIClientImpl.getInstance().newWIFILocation(params[0], params[1]);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "not sent to server");
        }
        return null;
    }
}

}
