package cmu1617.andred.pt.locmess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.WIFILocation;
import pt.andred.cmu1617.APIException;
import pt.andred.cmu1617.LocMessAPIClientImpl;

public class NewWIFILocation extends AppCompatActivity {

    private int numberOfItemsInList = 0;
    private ArrayAdapter<String> _activeSsidListAdapter;
    private FloatingActionButton _addMoreSSIDButtom;
    private LinearLayout _ssidListLayout;
    private TextView location_name_view;
    private List<ViewHolder> _ssidsViewsList = new ArrayList<>();
    private List<String> _ssidListForAutoComplete = new ArrayList<>(); //for autocomplete
    private Button _addToServerButton;
    private View mMainView;
    private View mProgressView;
    private SQLDataStoreHelper _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wifilocation);
        numberOfItemsInList = 0;
        _db = new SQLDataStoreHelper(getApplicationContext());
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        try {
            ((TextView) findViewById(R.id.initial)).setText(getString(R.string.new_location_wifi_1) + info.getSSID() + getString(R.string.new_location_wifi_2));
        }catch (Exception e){
            //we're not connected to anything
        }
        _ssidListLayout = (LinearLayout) findViewById(R.id.ssid_list_layout);
        _addMoreSSIDButtom = (FloatingActionButton) findViewById(R.id.add_wifi_ssid_button);
        populateSsidList();
        _activeSsidListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _ssidListForAutoComplete);
        _addMoreSSIDButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewSSIDView();
            }
        });
        _addToServerButton = (Button) findViewById(R.id.add_wifi_location_to_server_button);
        _addToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAllSSIDs()) {
                    new NewWIFILocationAsync().execute();
                }
            }
        });
        mMainView = findViewById(R.id.scroll_view_add_wifi_location);
        mProgressView = findViewById(R.id.progress_bar);
        location_name_view = (TextView) findViewById(R.id.new_wifi_location_name);
        _addMoreSSIDButtom.performClick();
        location_name_view.requestFocus();
    }

    private void createNewSSIDView() {
        LayoutInflater inflater = getLayoutInflater();
        final View vi = inflater.inflate(R.layout.ssid_item, null);
        _ssidListLayout.addView(vi);

        final ViewHolder viewHolder = new ViewHolder(vi,numberOfItemsInList++);

        viewHolder._delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder._position;
                _ssidsViewsList.remove(viewHolder);
                _ssidListLayout.removeView(vi);
                for(ViewHolder oldViewHolder : _ssidsViewsList) {
                    if(oldViewHolder._position >position) {
                        oldViewHolder._position--;
                    }
//                            oldViewHolder._ssid.setText(oldViewHolder._position+"");
                }
                numberOfItemsInList--;
            }
        });
        _ssidsViewsList.add(viewHolder);
        vi.requestFocus();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView _ssid;
        ImageView _delete;
        View _holder;
        int _position;

        ViewHolder(View itemView, int position) {
            super(itemView);
            _holder = itemView;
            _ssid = (AutoCompleteTextView) itemView.findViewById(R.id.ssid_name_text_view);
//            _ssid.setText(position+"");
            _ssid.setAdapter(_activeSsidListAdapter);
            _delete = (ImageView) itemView.findViewById(R.id.delete_ssid_item);
            this._position = position;
        }
    }

    private void populateSsidList(){
        _ssidListForAutoComplete = LocMessMainService.getInstance().getSsidList();
        if(_ssidListForAutoComplete == null) {
            _ssidListForAutoComplete = new ArrayList<>();
        }
    }

    private Boolean validateAllSSIDs() {
        View focusView = null;
        Boolean cancel = false;
        if(location_name_view.getText().toString().equals("")){
            location_name_view.setError("Invalid Location Name");
            focusView = location_name_view;
            cancel = true;
        }


        List<String> ssids = new ArrayList<>();
        for (ViewHolder vh : _ssidsViewsList) {
            if (!vh._ssid.getText().toString().equals("")) {
                if (ssids.contains(vh._ssid.getText().toString())) {
                    vh._ssid.setError("Repeated SSID");
                    focusView = vh._ssid;
                    cancel = true;
                } else {
                    ssids.add(vh._ssid.getText().toString());
                }
            } else {
                vh._ssid.setError("Invalid SSID");
                focusView = vh._ssid;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

   protected class NewWIFILocationAsync extends AsyncTask<Void, Void, Boolean> {
        private String Tag = "NewWIFILocationAsync";
        protected List<String> _ssids;
        protected String location_name;


        @Override
        protected void onPreExecute() {
            Log.d(Tag,"start");
            showProgress(true);
            location_name = location_name_view.getText().toString();
            _ssids = new ArrayList<>();

            for ( ViewHolder vh : _ssidsViewsList) {
                _ssids.add(vh._ssid.getText().toString());
            }
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {



                String location_id = LocMessAPIClientImpl.getInstance().addLocation(location_name,_ssids);
                new WIFILocation(_db,location_id).completeObject(location_name,_ssids,"1");


            } catch (APIException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d(Tag,"end");
            showProgress(false);
            if(!success) {
                final Snackbar snackbar = Snackbar.make(mMainView, "Could not connect to server", Snackbar.LENGTH_LONG);
                snackbar.show();
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
            } else {
                onBackPressed();
            }
        }


        @Override
        protected void onCancelled() {
            Log.d(Tag,"cancelled");
            showProgress(false);
        }
    }
}
