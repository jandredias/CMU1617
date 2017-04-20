package cmu1617.andred.pt.locmess;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewWIFILocation extends AppCompatActivity {

    private ArrayAdapter<String> _keywordListAdapter;
    private FloatingActionButton _addKeywordButtom;
    private LinearLayout _keywordsListLayout;
    private List<ViewHolder> _keywordsViewsList = new ArrayList<>();
    private ArrayList<String> _keywordList = new ArrayList<>();
    private Button _cancelButton;
    private Button _saveButton;
   /* private WifiManager mWifiManager;
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                // add your logic here
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wifilocation);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        try {
            ((TextView) findViewById(R.id.initial)).setText(getString(R.string.new_location_wifi_1) + info.getSSID() + getString(R.string.new_location_wifi_2));
        }catch (Exception e){
            //we're not connected to anything
        }
        _keywordsListLayout = (LinearLayout) findViewById(R.id.profile_list_keywords);
        _addKeywordButtom  = (FloatingActionButton) findViewById(R.id.profile_add_keyword_buttom);
        _keywordListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _keywordList);
        _addKeywordButtom  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View vi = inflater.inflate(R.layout.keyword_profile_item, null);
                _keywordsListLayout.addView(vi);
                ViewHolder viewHolder = new ViewHolder(vi);
                _keywordsViewsList.add(viewHolder);
            }
        });
        populateKeywordList();
        _cancelButton = (Button) findViewById(R.id.cancel_button_profile);
        _cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        _saveButton = (Button) findViewById(R.id.save_button_profile);
        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // new SetUserKeywordsAsyncTask().execute(); TODO
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView _keyword;
        EditText _value;
        ImageView _delete;
        View _holder;

        ViewHolder(View itemView) {
            super(itemView);
            _holder = itemView;
            _keyword = (AutoCompleteTextView) itemView.findViewById(R.id.profile_keyword_name_text_view);
            _keyword.setAdapter(_keywordListAdapter);
            _value = (EditText) itemView.findViewById(R.id.profile_keyword_value_edit_text);
            _delete = (ImageView) itemView.findViewById(R.id.delete_profile_keyword_item);
        }
    }

    private void populateKeywordList (){
       /* mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();*/
    }
}
