package cmu1617.andred.pt.locmess;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.UserProfile;

public class ProfileActivity extends AppCompatActivity {

    private ArrayAdapter<String> _keywordListAdapter;
    private SQLDataStoreHelper _db;
    private TextView _usernameView;
    private UserProfile _user;
    private LinearLayout _keywordsListLayout;
    private List<ViewHolder> _keywordsViewsList = new ArrayList<>();
    private FloatingActionButton _addKeywordButtom;
    private ArrayList<String> _keywordList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        _db = new SQLDataStoreHelper(this);

        _keywordsListLayout = (LinearLayout) findViewById(R.id.profile_list_keywords);

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
        populateKeywordList();
        _keywordListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _keywordList);



    }

    private void populateKeywordList (){
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_KEYWORDS, //table name
                DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                null,
                null,
                null, null, null
        );
        _keywordList.clear();
        while (cursor.moveToNext()) {
            _keywordList.add(cursor.getString(1));
        }
    }

    private void createNewKeywordView() {
        LayoutInflater inflater = getLayoutInflater();
        View vi = inflater.inflate(R.layout.keyword_profile_item, null);
        _keywordsListLayout.addView(vi);
        _keywordsViewsList.add(new ProfileActivity.ViewHolder(vi));
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
            _value = (EditText) itemView.findViewById(R.id.edit_text_keyword_item);
        }
    }
}
