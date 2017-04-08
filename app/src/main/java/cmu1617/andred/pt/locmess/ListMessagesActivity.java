package cmu1617.andred.pt.locmess;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ListMessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View view = View.inflate(R.layout.activity_location_messages)
        setContentView(R.layout.activity_location_messages);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_message);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
