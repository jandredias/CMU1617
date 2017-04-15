package cmu1617.andred.pt.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;

public class ListMessagesActivity extends AppCompatActivity {

    private LocMessLocation mLocation;
    private SQLDataStoreHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_messages);

        dbHelper = new SQLDataStoreHelper(this);

        String location = getIntent().getStringExtra("location_id");
        mLocation = new LocMessLocation(dbHelper,location);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_message);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NewMessageActivity.class);
                intent.putExtra("location_id",mLocation.id());
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

//        View emptyView = view.findViewById(R.id.empty_messages);
//        if (emptyView != null) {
//            if (mAdapter.getItemCount() != 0) {
//                emptyView.setVisibility(View.GONE);
//            } else {
//                emptyView.setVisibility(View.VISIBLE);
//            }
//        }
    }


}
