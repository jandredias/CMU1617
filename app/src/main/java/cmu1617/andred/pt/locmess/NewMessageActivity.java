package cmu1617.andred.pt.locmess;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cmu1617.andred.pt.locmess.AsyncTasks.GetKeyworksAsyncTask;
import cmu1617.andred.pt.locmess.Domain.LocMessLocation;

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
    private GetKeyworksAsyncTask _task;
    private List<String> _spinnerItems;
    private ArrayAdapter<String> _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
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

        populateSpinnerItems();

        _task = new GetKeyworksAsyncTask(dbHelper,this);
        _task.execute();
    }

    private void populateSpinnerItems (){
        Cursor cursor = dbHelper.getReadableDatabase().query(
                DataStore.SQL_KEYWORDS, //table name
                DataStore.SQL_KEYWORDS_COLUMNS, //columns to return
                null,
                null,
                null, null, null
        );
        _spinnerItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            _spinnerItems.add(cursor.getString(1));
        }
        _adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _spinnerItems);

    }

    private void addMessageToServer(){
        boolean cancel = false;
        View focusView = null;

        //check all values
        for(ViewHolder vh : constraintsViewsList) {
            String keywordValue = vh._value.getText().toString();
            if(keywordValue.equals("")) {
                vh._value.setError("Invalid");
                focusView = vh._holder;
                cancel = true;
            }
        }

        //check dates
        String end = dateTimeEnd.getText().toString();
        if(end.equals("")) {
            dateTimeEnd.setError("Date required");
            focusView = dateTimeEnd;
            cancel = true;
        }
        String begin = dateTimeBegin.getText().toString();
        if(begin.equals("")) {
            dateTimeBegin.setError("Date required");
            focusView = dateTimeBegin;
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
        _task.getList();

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
}
