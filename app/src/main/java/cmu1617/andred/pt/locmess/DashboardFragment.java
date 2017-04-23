package cmu1617.andred.pt.locmess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.UserProfile;

/**
 * Created by miguel on 06/04/17.
 */

public class DashboardFragment extends ListMessagesFragment {
    private final String TAG = "DashboardFragment";
    private DashboardFragment dashboardFragment;
    private BroadcastReceiver mMessageReceiver;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater,container,savedInstanceState);
        dashboardFragment = this;
        mMessageReceiver = new MyBroadcastReceiver();
        return v;
    }



    @Override
    public ListMessagesRecyclerViewAdapter createNewAdapter() {
        return new NewMessagesRecyclerViewAdapter();
    }

    @Override
    public void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
    @Override
    public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(getString(R.string.intent_broadcast_new_messages)));
        super.onResume();
    }

    protected class NewMessagesRecyclerViewAdapter extends ListMessagesRecyclerViewAdapter {


        List<LocMessMessage> messages = new ArrayList<>();

        NewMessagesRecyclerViewAdapter(){
            String[] selectionArgs = { new UserProfile(_dbHelper).userName() };
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_MESSAGES, //table name
                    DataStore.SQL_MESSAGES_COLUMNS, //columns to return
                    "enabled = 1 AND message_id NOT IN (SELECT message_id FROM "+DataStore.SQL_READ_MESSAGES+" WHERE reader_id = ? )", //selection string
                    selectionArgs, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null //limit
            );
            while(cursor.moveToNext()){
                messages.add(new LocMessMessage(_dbHelper,cursor.getString(0)));
            }
        }

        @Override
        public ListMessagesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View l = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new ListMessagesRecyclerViewAdapter.ViewHolder(l);
        }

        @Override
        public int getItemCount() {
//            String[] selectionArgs = { new UserProfile(_dbHelper).userName() };
//            Cursor cursor = _dbHelper.getReadableDatabase().query(
//                    true, //distinct
//                    DataStore.SQL_MESSAGES, //table name
//                    DataStore.SQL_MESSAGES_COLUMNS, //columns to return
//                    "enabled = 1 AND message_id NOT IN (SELECT message_id FROM "+DataStore.SQL_READ_MESSAGES+" WHERE reader_id = ? )", //selection string
//                    selectionArgs, //selection args
//                    null, //groupBy
//                    null, //having
//                    null, //orderBy
//                    null //limit
//            );
//            int c = cursor.getCount();
//            cursor.close();
//            Log.d("Messages",c+"");
//            return c;
            return messages.size();
        }

        public LocMessMessage getItem(int position) {
//            String[] selectionArgs = { new UserProfile(_dbHelper).userName() };
//            Cursor cursor = _dbHelper.getReadableDatabase().query(
//                    true, //distinct
//                    DataStore.SQL_MESSAGES, //table name
//                    DataStore.SQL_MESSAGES_COLUMNS, //columns to return
//                    "message_id NOT IN (SELECT message_id FROM "+DataStore.SQL_READ_MESSAGES+" WHERE reader_id = ? )", //selection string
//                    selectionArgs, //selection args
//                    null, //groupBy
//                    null, //having
//                    null, //orderBy
//                    null //limit
//            );
//            if (cursor.getCount() == 0) {
//                cursor.close();
//                return null;
//            }
//            cursor.moveToPosition(cursor.getCount() - position - 1);


            return messages.get(position);/*new LocMessMessage(_dbHelper, cursor.getString(0));*/

        }
    }
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.wtf(TAG,"request for restart view");
            new SimpleOkMessage(getContext(),"Messages Updated!");

            dashboardFragment.restartListView();

        }
    }
}
