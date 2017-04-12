package cmu1617.andred.pt.locmess;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private SQLDataStoreHelper _db;
    private TextView _usernameView;
    private UserProfile _user;
    private LinearLayout _keywordsList;
    private List<ViewHolder> _keywordsViewsList = new ArrayList<>();
    private FloatingActionButton _addKeywordButtom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        _db = new SQLDataStoreHelper(this);

        _keywordsList = (LinearLayout) findViewById(R.id.profile_list_keywords);

        _user = new UserProfile(_db);


        _usernameView = (TextView) findViewById(R.id.profile_username_text_view);
        _usernameView.setText(_user.userName());

        _addKeywordButtom  = (FloatingActionButton) findViewById(R.id.profile_add_keyword_buttom);
        _addKeywordButtom  .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewKeywordView();
            }
        });
    }

    private void createNewKeywordView() {
        LayoutInflater inflater = getLayoutInflater();
        View vi = inflater.inflate(R.layout.keyword_profile_item, null);
        _keywordsList.addView(vi);
        _keywordsViewsList.add(new ProfileActivity.ViewHolder(vi));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView _keyword;
        EditText _value;
        ImageView _delete;
        View _holder;

        ViewHolder(View itemView) {
            super(itemView);
            _holder = itemView;
            _keyword = (TextView) itemView.findViewById(R.id.profile_keyword_name_text_view);
            _value = (EditText) itemView.findViewById(R.id.profile_keyword_value_edit_text);
            _delete = (ImageView) itemView.findViewById(R.id.delete_profile_keyword_item);
            _value = (EditText) itemView.findViewById(R.id.edit_text_keyword_item);
        }
    }
}
