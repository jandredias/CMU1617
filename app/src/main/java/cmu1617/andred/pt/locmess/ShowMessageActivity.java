package cmu1617.andred.pt.locmess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import cmu1617.andred.pt.locmess.Domain.LocMessReadMessage;
import cmu1617.andred.pt.locmess.Domain.UserProfile;

public class ShowMessageActivity extends AppCompatActivity {

    private SQLDataStoreHelper _db;
    private LocMessReadMessage mMessage;
    private TextView _message;
    private TextView _location;
    private TextView _author;
    private TextView _date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);
        _db = new SQLDataStoreHelper(this);
        String message_id = getIntent().getStringExtra("message_id");
        String content = getIntent().getStringExtra("content");
        String author_id = getIntent().getStringExtra("author");
        String location_id = getIntent().getStringExtra("location_id");
        String post_timestamp = getIntent().getStringExtra("post_timestamp");

        registerMessageAsRead(message_id,content,author_id,location_id,post_timestamp);



        _message = (TextView) findViewById(R.id.show_message_message_body);
        _author = (TextView) findViewById(R.id.show_message_author);
        _location = (TextView) findViewById(R.id.show_message_location);
        _date = (TextView ) findViewById(R.id.show_message_post_timestamp);

        _message .setText(mMessage.content());
        String color = "#"+Integer.toHexString(getResources().getColor(R.color.colorAccent, null) & 0x00ffffff);
        String openBracket = "<font color=\""+color+"\"><b>";
        String closeBracket = "</b></font>";
        _author .setText(Html.fromHtml(openBracket+"From: "+closeBracket+mMessage.authorId()));
        _location .setText(Html.fromHtml(openBracket+"In: "+closeBracket+mMessage.location().name()));
        _date .setText(Html.fromHtml(openBracket+"At: "+closeBracket+mMessage.postTimestamp()));
    }

    private void registerMessageAsRead(String message_id, String content, String author_id, String location_id, String post_timestamp) {
        String readerId = new UserProfile(_db).userName();
        mMessage = new LocMessReadMessage(_db,message_id);
        mMessage.completeObject(location_id,author_id,content,readerId, post_timestamp);
    }
}
