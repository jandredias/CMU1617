package cmu1617.andred.pt.locmess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import cmu1617.andred.pt.locmess.Domain.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private SQLDataStoreHelper _db;
    private TextView _usernameView;
    private UserProfile _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        _db = new SQLDataStoreHelper(this);

        _user = new UserProfile(_db);


        _usernameView = (TextView) findViewById(R.id.profile_username_text_view);
        _usernameView.setText(_user.userName());

    }
}
