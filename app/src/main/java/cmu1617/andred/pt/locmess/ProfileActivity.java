package cmu1617.andred.pt.locmess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmu1617.andred.pt.locmess.AsyncTasks.GetKeywordsAsyncTask;
import cmu1617.andred.pt.locmess.Domain.UserKeyword;
import cmu1617.andred.pt.locmess.Domain.UserKeywordsDifference;
import cmu1617.andred.pt.locmess.Domain.UserProfile;
import pt.andred.cmu1617.APIException;
import pt.andred.cmu1617.LocMessAPIClientImpl;

public class ProfileActivity extends AppCompatActivity implements OnTaskCompleted {

    private ArrayAdapter<String> _keywordListAdapter;
    private SQLDataStoreHelper _db;
    private TextView _usernameView;
    private UserProfile _user;
    private LinearLayout _keywordsListLayout;
    private List<ViewHolder> _keywordsViewsList = new ArrayList<>();
    private FloatingActionButton _addKeywordButtom;
    private ArrayList<String> _keywordList = new ArrayList<>();
    private GetKeywordsAsyncTask _task;
    private Button _cancelButton;
    private Button _saveButton;
    private Map<String, String > _startUserValues;
    private View mainView;
    private View mProgressView;
    private int numberOfItemsInList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        numberOfItemsInList = 0;
        _db = new SQLDataStoreHelper(this);
        mainView = findViewById(R.id.scroll_view_profile);
        mProgressView = findViewById(R.id.progress_bar);
        _startUserValues = new HashMap<>();
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
        _keywordListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _keywordList);
        populateKeywordList();
        _task = new GetKeywordsAsyncTask(_db,this);
        _task.execute();

        _cancelButton = (Button) findViewById(R.id.cancel_button_profile);
        _saveButton = (Button) findViewById(R.id.save_button_profile);

        _cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        populateUserKeywords();
        _saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetUserKeywordsAsyncTask().execute();
            }
        });
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

    private ViewHolder createNewKeywordView() {
        LayoutInflater inflater = getLayoutInflater();
        final View vi = inflater.inflate(R.layout.keyword_profile_item, null);
        _keywordsListLayout.addView(vi);
        final ViewHolder viewHolder = new ViewHolder(vi,numberOfItemsInList++);

        viewHolder._delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder._position;
                _keywordsViewsList.remove(viewHolder);
                _keywordsListLayout.removeView(vi);
                for(ViewHolder oldViewHolder : _keywordsViewsList) {
                    if(oldViewHolder._position >position) {
                        oldViewHolder._position--;
                    }
//                            oldViewHolder._ssid.setText(oldViewHolder._position+"");
                }
                numberOfItemsInList--;
            }
        });

        _keywordsViewsList.add(viewHolder);
        vi.requestFocus();
        return viewHolder;
    }

    private void populateUserKeywords() {
        Cursor cursor = _db.getReadableDatabase().query(
                DataStore.SQL_USER_KEYWORDS, //table name
                DataStore.SQL_USER_KEYWORDS_COLUMNS, //columns to return
                null,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()) {
            ViewHolder viewHolder = createNewKeywordView();
            String keywordName = new UserKeyword(_db,cursor.getString(0)).name();
            String keywordValue = cursor.getString(1);
            viewHolder._keyword.setText(keywordName);
            viewHolder._value.setText(keywordValue);
            _startUserValues.put(keywordName,keywordValue);
        }
    }

    @Override
    public void onTaskCompleted(Object... args) {
        populateKeywordList();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView _keyword;
        EditText _value;
        ImageView _delete;
        View _holder;
        int _position;

        ViewHolder(View itemView,int position) {
            super(itemView);
            _holder = itemView;
            _keyword = (AutoCompleteTextView) itemView.findViewById(R.id.profile_keyword_name_text_view);
            _keyword.setAdapter(_keywordListAdapter);
            _value = (EditText) itemView.findViewById(R.id.profile_keyword_value_edit_text);
            _delete = (ImageView) itemView.findViewById(R.id.delete_profile_keyword_item);
            _position = position;
        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            mainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    protected class SetUserKeywordsAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String Tag = "SetUserKeywordAsyncTask";
        private Map<String,String> _endUserValues;


        @Override
        protected void onPreExecute() {
            Log.d(Tag,"start");
            showProgress(true);
            _endUserValues = new HashMap<>();

            for ( ViewHolder vh : _keywordsViewsList) {
                _endUserValues.put(vh._keyword.getText().toString(),vh._value.getText().toString());
            }
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {


                List<UserKeywordsDifference.Action> actions = new UserKeywordsDifference(_startUserValues,_endUserValues).getDifferences();
                for (UserKeywordsDifference.Action action : actions) {
                    String keyword_id = LocMessAPIClientImpl.getInstance().editProfileKeys(action._add,action._keywordName, action._keywordValue);
                    new UserKeyword(_db,keyword_id).completeObject(action._keywordName,action._keywordValue);
                }
            } catch (APIException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d(Tag,"end");
            showProgress(false);
            if(!success) {
                final Snackbar snackbar = Snackbar.make(mainView, "Could not connect to server", Snackbar.LENGTH_LONG);
                snackbar.show();
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
            } else {
                onBackPressed();
            }
        }


        @Override
        protected void onCancelled() {
            Log.d(Tag,"cancelled");
            showProgress(false);
        }
    }

}
