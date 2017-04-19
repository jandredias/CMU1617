package cmu1617.andred.pt.locmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.LocMessReadMessage;
import cmu1617.andred.pt.locmess.Domain.UserProfile;

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
        registerMessageAsRead(mMessage);



        _message = (TextView) findViewById(R.id.show_message_message_body);
        _author = (TextView) findViewById(R.id.show_message_author);
        _location = (TextView) findViewById(R.id.show_message_location);

        _message .setText(mMessage.content());
        String color = "#"+Integer.toHexString(getResources().getColor(R.color.colorAccent, null) & 0x00ffffff);
        String openBracket = "<font color=\""+color+"\"><b>";
        String closeBracket = "</b></font>";
        _author .setText(Html.fromHtml(openBracket+"From: "+closeBracket+mMessage.authorId()));
        _location .setText(Html.fromHtml(openBracket+"In: "+closeBracket+mMessage.location().name()));
    }

    private void registerMessageAsRead(LocMessMessage message) {
        String readerId = new UserProfile(_db).userName();
        LocMessReadMessage readMessage = new LocMessReadMessage(_db,message.id());
        readMessage.completeObject(message.location().id(),message.authorId(),message.content(),readerId);
    }
}
