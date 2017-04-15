package cmu1617.andred.pt.locmess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cmu1617.andred.pt.locmess.AsyncTasks.GetKeywordsAsyncTask;
import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import pt.andred.cmu1617.APIException;
import pt.andred.cmu1617.LocMessAPIClientImpl;
import pt.andred.cmu1617.MessageConstraint;

public class NewMessageActivity extends AppCompatActivity implements OnTaskCompleted{

    private SQLDataStoreHelper dbHelper;
    private LocMessLocation mLocation;

    private EditText dateTimeBegin;
    private EditText dateTimeEnd;
    private EditText messageText;
    private Switch mySwitch;
    private boolean serverMode = true;
    private FloatingActionButton addConstraintButtom;
    private Button sendMessageToServer;
    private LinearLayout constraintsList;
    private List<ViewHolder> constraintsViewsList = new ArrayList<>();
    private GetKeywordsAsyncTask _task;
    private List<String> _spinnerItems = new ArrayList<>();
    private ArrayAdapter<String> _adapter;
    private boolean updateCompleted = false; // boolean that allows the recreation of cursors
    private View mainView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        mainView = findViewById(R.id.scroll_view_new_message);
        mProgressView = findViewById(R.id.progress_bar);
//        scrollView = (ScrollView) findViewById(R.id.scroll_view_new_message);
//        contentView = (ViewGroup) findViewById(R.id.container_new_message);
        sendMessageToServer = (Button) findViewById(R.id.send_new_message_to_server_button);
        messageText = (EditText) findViewById(R.id.post_message);
        dbHelper = new SQLDataStoreHelper(this);

        String location = getIntent().getStringExtra("location_id");
        mLocation = new LocMessLocation(dbHelper,location);


        View.OnClickListener onEditTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = (EditText) v;
                edit.setCursorVisible(false);
                edit.setError(null);
                showTruitonTimePickerDialog(v);
                showTruitonDatePickerDialog(v);
            }
        };

        mySwitch = (Switch) findViewById(R.id.switch_send_mode);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                serverMode = !isChecked;
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });

        dateTimeBegin = (EditText) findViewById(R.id.edit_start_text);
        dateTimeEnd = (EditText) findViewById(R.id.edit_end_text);
        dateTimeBegin.setOnClickListener(onEditTextClickListener);
        dateTimeEnd.setOnClickListener(onEditTextClickListener);


        constraintsList = (LinearLayout) findViewById(R.id.list_constraints);


        sendMessageToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessageToServer();
            }
        });


        addConstraintButtom  = (FloatingActionButton) findViewById(R.id.add_constraint);
        addConstraintButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewConstraintView();
            }
        });

        _adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _spinnerItems);
        populateSpinnerItems();

        _task = new GetKeywordsAsyncTask(dbHelper,this);
        _task.execute();
    }

    private void populateSpinnerItems (){
        Cursor cursor = dbHelper.getReadableDatabase().query(
                true, //distinct
                DataStore.SQL_KEYWORDS, //table name
                DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                null, //selection string
                null, //selection args
                null, //groupBy
                null, //having
                DataStore.SQL_KEYWORDS_COLUMNS[1], //orderBy
                null
        );
        _spinnerItems.clear();
        while (cursor.moveToNext()) {
            _spinnerItems.add(cursor.getString(1));
        }
        updateCompleted = true;
    }

    class KeywordsIds {
        SQLDataStoreHelper _db;
        Cursor _allKeywordsCursorBegin;
        Cursor _allKeywordsCursorEnd;

        KeywordsIds (SQLDataStoreHelper db) {
            _db = db;
            _allKeywordsCursorBegin = dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_KEYWORDS, //table name
                    DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    DataStore.SQL_KEYWORDS_COLUMNS[1], //orderBy
                    null
            );
            _allKeywordsCursorEnd = dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_KEYWORDS, //table name
                    DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null
            );
        }

        private String checkIndex(String name, int position) {
            if(position >= _allKeywordsCursorBegin.getCount())
                return null;

            _allKeywordsCursorBegin.moveToPosition(_allKeywordsCursorBegin.getCount() - position - 1);
            String cursorName = _allKeywordsCursorBegin.getString(1); //name
            if (cursorName.equals(name)){
                return _allKeywordsCursorBegin.getString(0);
            } else {
                return null;
            }
        }

        String getKeywordNameId(String name, int position) {
            String possibleID = checkIndex(name,position);
            if(possibleID != null) {
                return possibleID;
            } else {
                return deepSearch(name);
            }
        }

        private String deepSearch(String name) {
            if(updateCompleted) {
                _allKeywordsCursorEnd = dbHelper.getReadableDatabase().query(
                        true, //distinct
                        DataStore.SQL_KEYWORDS, //table name
                        DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                        null, //selection string
                        null, //selection args
                        null, //groupBy
                        null, //having
                        null, //orderBy
                        null
                );
                updateCompleted = false;
            }
            while (_allKeywordsCursorEnd.moveToNext()) {
                if(name.equals(_allKeywordsCursorEnd.getString(1))){
                    return _allKeywordsCursorEnd.getString(0);
                }
            }
            return null;//let it explode dont care
        }
    }



    private void addMessageToServer(){
        boolean cancel = false;
        View focusView = null;
        List<MessageConstraint> messageConstraints = new ArrayList<>();

        KeywordsIds translator = new KeywordsIds(dbHelper);

        //check all values
        for(ViewHolder vh : constraintsViewsList) {
            String keywordName = vh._keyword.getSelectedItem().toString();
            int namePosition = vh._keyword.getSelectedItemPosition();

            String keywordID = translator.getKeywordNameId(keywordName,namePosition);

            Boolean equal = vh._equalOrDifferent.getSelectedItem().toString().equals("==");
            String keywordValue = vh._value.getText().toString();
            if(keywordValue.equals("")) {
                vh._value.setError("Invalid");
                focusView = vh._holder;
                cancel = true;
            } else {
                messageConstraints.add(new MessageConstraint(keywordID,keywordValue,equal));
            }
        }

        //check dates
        Date endDate = null;
        String end = dateTimeEnd.getText().toString();
        if(end.equals("")) {
            dateTimeEnd.setError("Date required");
            focusView = dateTimeEnd;
            cancel = true;
        } else {
            SimpleDateFormat screenFormatter = new SimpleDateFormat("'Until: 'dd/MM/yyyy' - 'HH:mm");
            SimpleDateFormat sqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                endDate = screenFormatter.parse(end);
                end = sqlFormatter.format(endDate);
            } catch (ParseException e) {
                dateTimeEnd.setError("Invalid Format, please choose again");
                focusView = dateTimeEnd;
                cancel = true;
            }

        }

        Date beginDate = null;
        String begin = dateTimeBegin.getText().toString();
        if(begin.equals("")) {
            dateTimeBegin.setError("Date required");
            focusView = dateTimeBegin;
            cancel = true;
        } else {
            SimpleDateFormat screenFormatter = new SimpleDateFormat("'From: 'dd/MM/yyyy' - 'HH:mm");
            SimpleDateFormat sqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                beginDate = screenFormatter.parse(begin);
                begin = sqlFormatter.format(beginDate);
            } catch (ParseException e) {
                dateTimeBegin.setError("Invalid Format, please choose again");
                focusView = dateTimeBegin;
                cancel = true;
            }
        }

        if (beginDate.compareTo(endDate)>0)
        {
            dateTimeEnd.setError("End sooner than begin");
            focusView = dateTimeEnd;
            cancel = true;
        }

        //Check message
        String text = messageText.getText().toString();
        if(text.equals("")) {
            messageText.setError("Message required");
            focusView = messageText;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
        } else {
            AddMessageToServerAsyncTask task = new AddMessageToServerAsyncTask(begin,end,text,messageConstraints);

            task.execute();

        }
    }

    private void createNewConstraintView(){
            LayoutInflater inflater = getLayoutInflater();
            View vi = inflater.inflate(R.layout.keyword_message_item, null);
            constraintsList.addView(vi);
            constraintsViewsList.add(new ViewHolder(vi));
    }

    @Override
    public void onTaskCompleted() {
        populateSpinnerItems();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Spinner _keyword;
        Spinner _equalOrDifferent;
        EditText _value;
        View _holder;

        ViewHolder(View itemView) {
            super(itemView);
                _holder = itemView;
                _keyword = (Spinner) itemView.findViewById(R.id.keyword_spinner);
                _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                _keyword.setAdapter(_adapter);
                _equalOrDifferent = (Spinner) itemView.findViewById(R.id.equal_diff_spinner);
                _value = (EditText) itemView.findViewById(R.id.edit_text_keyword_item);
        }


    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(v);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        private final EditText _v;

        public DatePickerFragment(View v) {
            _v = (EditText) v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String previousText  = _v.getText().toString();
            if(previousText.equals(""))
                previousText = _v.getHint().toString();
            previousText = previousText.split(":")[0]+": ";
            _v.setText(previousText + day + "/" + (month + 1) + "/" + year);
        }
    }

    public void showTruitonTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(v);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        private final EditText _v;

        public TimePickerFragment(View v) {
            _v = (EditText) v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            _v.setText(_v.getText() + " - " + hourOfDay + ":"	+ minute);
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

    protected class AddMessageToServerAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String _dateBegin;
        private String _dateEnd;
        private String _message;
        private List<MessageConstraint> _list;
        private String Tag = "AddMessageAsyncTask";

        AddMessageToServerAsyncTask(String dateBegin, String dateEnd, String message, List<MessageConstraint> list) {
            _list = list;
            _dateBegin = dateBegin;
            _dateEnd = dateEnd;
            _message = message;
        }

        @Override
        protected void onPreExecute() {
            Log.d(Tag,"start");
            Log.d(Tag,"start");
            showProgress(true);
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String message_id = LocMessAPIClientImpl.getInstance().addMessage(mLocation.id(),_message, _dateBegin, _dateEnd, _list);
            } catch (APIException e) {
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
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }


        @Override
        protected void onCancelled() {
            Log.d(Tag,"cancelled");
            showProgress(false);
        }
    }
}
