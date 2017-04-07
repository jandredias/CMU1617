package cmu1617.andred.pt.locmess;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;

/**
 * Created by miguel on 07/04/17.
 */

public class LocationFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.location_list_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.location_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

//        _noMessagesLayout = v.findViewById(R.id.empty_chats);
//
//        if (_noMessagesLayout != null) {
//            if (_adapter.getItemCount() != 0) {
//                _noMessagesLayout.setVisibility(View.GONE);
//            } else {
//                _noMessagesLayout.setVisibility(View.VISIBLE);
//            }
//        }

        return view;
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View l = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
            return new ViewHolder(l);
        }

        public Chat getItem(int position) {
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    DataStore.SQL_CHATS, //table name
                    DataStore.SQL_CHATS_COLUMNS, //columns to return
                    "closed is 0", //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null //orderBy
            );
            if (cursor.getCount() == 0) {
                Log.d(TAG, "no chats are available on database");
                cursor.close();
                return null;
            }
            cursor.moveToPosition(cursor.getCount() - position - 1);

            return new Chat(_dbHelper, cursor.getString(0));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Chat p = getItem(position);
            ViewHolder v = holder;

            v._position.setText(position + "");
            v._question.setText(p.question().question());

            if (p.lastMessage() != null) {
                String message = "";
                if (p.yours()) {
                    message = "You: ";
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    message += new String(Base64.decode(p.lastMessage(), Base64.DEFAULT), StandardCharsets.UTF_8);
                } else {
                    message += new String(Base64.decode(p.lastMessage(), Base64.DEFAULT));
                }
                v._timestamp.setText(message);
            } else {
                v._timestamp.setText("");
            }

            if(p.user() != null) {
                v._username.setText(p.user().name().split(" ")[0]);
                if (v._photo != null) {
                    Picasso.with(getContext()).
                            load(p.user().photo()).
                            resize(v._photo.getLayoutParams().width, v._photo.getLayoutParams().height).
                            centerCrop().
                            transform(new AuxiliarMethods.ImageTrans_CircleTransform()).
                            into(v._photo);
                }
            }
            v._city.setText(p.question().city().name());

            if (p.unread() == 0) {
                v._notifications.setVisibility(View.INVISIBLE);
                v._notifications.setText("");
            } else {
                v._notifications.setVisibility(View.VISIBLE);
                v._notifications.setText(p.unread() + "");
            }
            if (p.amILocal()) {
                // FIXME: 12/02/17 Deprecated
                v._holder.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            } else {
                // FIXME: 12/02/17 Deprecated
                v._holder.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }

        @Override
        public int getItemCount() {
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    DataStore.SQL_CHATS, //table name
                    DataStore.SQL_CHATS_COLUMNS, //columns to return
                    "closed is 0", //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null //orderBy
            );
            int c = cursor.getCount();
            cursor.close();
            return c;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            protected View _holder;
            protected ImageView _photo;
            protected TextView _notifications;
            protected TextView _username;

            protected TextView _city;

            protected TextView _question;

            protected TextView _timestamp;

            protected TextView _position;

            public ViewHolder(View itemView) {
                super(itemView);
                _holder = itemView;
                _position = (TextView) itemView.findViewById(R.id.position);
                _question = (TextView) itemView.findViewById(R.id.question);
                _timestamp = (TextView) itemView.findViewById(R.id.timestamp);
                _notifications = (TextView) itemView.findViewById(R.id.notification_number);
                _city = (TextView) itemView.findViewById(R.id.city);
                _username = (TextView) itemView.findViewById(R.id.user_name);
                _photo = (ImageView) itemView.findViewById(R.id.user_photo);
            }
        }
    }
}
