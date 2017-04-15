package cmu1617.andred.pt.locmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cmu1617.andred.pt.locmess.Domain.LocMessMessage;

public class ShowMessageActivity extends AppCompatActivity {

    private SQLDataStoreHelper _db;
    private LocMessMessage mMessage;
    private TextView _message;
    private TextView _location;
    private TextView _author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        _db = new SQLDataStoreHelper(this);
        String message_id = getIntent().getStringExtra("message_id");
        mMessage = new LocMessMessage(_db,message_id);

        _message = (TextView) findViewById(R.id.show_message_message_body);
        _author = (TextView) findViewById(R.id.show_message_author);
        _location = (TextView) findViewById(R.id.show_message_location);

        _message .setText(mMessage.content());
        _author .setText("From: "+mMessage.authorId());
        _location .setText("In: " +mMessage.location().name());
    }
}
