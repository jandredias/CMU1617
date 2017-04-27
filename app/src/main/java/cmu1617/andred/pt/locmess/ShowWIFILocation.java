package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cmu1617.andred.pt.locmess.Domain.WIFILocation;

public class ShowWIFILocation extends AppCompatActivity {

    private SQLDataStoreHelper _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_wifilocation);
        Intent i = getIntent();
        String id = i.getStringExtra("id");
        _db = new SQLDataStoreHelper(this);
        WIFILocation _location = new WIFILocation(_db,id);

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _location.ssidList());
        ListView lstView = (ListView) findViewById(R.id.list_view_show_wifi_ssids);
        lstView.setAdapter(itemsAdapter);
    }
}
