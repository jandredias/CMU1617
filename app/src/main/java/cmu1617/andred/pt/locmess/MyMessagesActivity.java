package cmu1617.andred.pt.locmess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.UserProfile;
import pt.andred.cmu1617.APIException;
import pt.andred.cmu1617.LocMessAPIClientImpl;

public class MyMessagesActivity extends AppCompatActivity {


    private final String TAG = "List Available Messages";
    protected SQLDataStoreHelper _dbHelper;
    private View _emptyView;
    protected View _mainView;
    private View _progressView;
    protected ListMyMessagesRecyclerViewAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    protected UserProfile _currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        _dbHelper = new SQLDataStoreHelper(this);
        _currentUser = new UserProfile(_dbHelper);

        _emptyView = findViewById(R.id.empty_messages);
        _mainView = findViewById(R.id.list_messages_fragment_main_view);
        _progressView = findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_messages_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    private void onItemClick(ListMyMessagesRecyclerViewAdapter mAdapter, int position) {

        LocMessMessage message = mAdapter.getItem(position);

        Intent intent = new Intent(this,ShowMessageActivity.class);
        intent.putExtra("message_id",message.id());
        intent.putExtra("author",message.authorId());
        intent.putExtra("location_id",message.location().id());
        intent.putExtra("content",message.content());
        intent.putExtra("post_timestamp",message.postTimestamp());

//        String timeEnd = message.timeEnd();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date endDate = null;
//        try {
//            endDate = dateFormat.parse(timeEnd);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Date now = new Date();
//        if(now.after(endDate)) {
//            new cleanExpiredMessages().execute();
//            new SimpleOkMessage(this,"Message has expired");
//        } else {
            startActivity(intent);
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _dbHelper.close();
    }



    @Override
    public void onStart(){
        super.onStart();
        // specify an adapter (see also next example)
        mAdapter = new ListMyMessagesRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        treatEmptyView();
    }

    protected void treatEmptyView(){

        if (_emptyView  != null) {
            if (mAdapter.getItemCount() != 0) {
                Log.d(TAG,"empty gone");
                _emptyView .setVisibility(View.GONE);
            } else {
                Log.d(TAG,"empty visible");
                _emptyView .setVisibility(View.VISIBLE);
            }
        }
    }

    protected static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }

    protected class ListMyMessagesRecyclerViewAdapter extends RecyclerView.Adapter<ListMyMessagesRecyclerViewAdapter.ViewHolder> {

        List<LocMessMessage> messages = new ArrayList<>();

        ListMyMessagesRecyclerViewAdapter(){
            messages = new ArrayList<>();
            String[] selectionArgs = { new UserProfile(_dbHelper).userName() };
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_MESSAGES, //table name
                    DataStore.SQL_MESSAGES_COLUMNS, //columns to return
                    "author_id = ? AND CURRENT_TIMESTAMP < time_end",
                    selectionArgs, //selection args
                    null, //groupBy
                    null, //having
                    "post_timestamp DESC", //orderBy
                    null //limit
            );
            while(cursor.moveToNext()){
                messages.add(new LocMessMessage(_dbHelper,cursor.getString(0)));
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LocMessMessage message = getItem(position);
            ViewHolder v = holder;
            v._message.setText(message.content());
            v._author.setText("By: " + message.authorId());
            if(message.location() != null) {
                v._location.setText(message.location().name());
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View l = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_with_delete_item, parent, false);
            return new ViewHolder(l);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public LocMessMessage getItem(int position) {
            return messages.get(position);/*new LocMessMessage(_dbHelper, cursor.getString(0));*/
        }


        protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            View _holder;
            TextView _location;
            TextView _author;
            TextView _message;
            View _delete_message_button;


            ViewHolder(View itemView) {
                super(itemView);
                _holder = itemView;
                _message = (TextView) itemView.findViewById(R.id.message_body_message_item);
                _location = (TextView) itemView.findViewById(R.id.location_name_message_item);
                _author = (TextView) itemView.findViewById(R.id.message_author_item);
                _delete_message_button = itemView.findViewById(R.id.delete_message_item);
                _delete_message_button.setOnClickListener(this);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
//                Toast.makeText(v.getContext(), "CLICK = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                if(v.getId() == _delete_message_button.getId()) {
                    onDeleteClick(mAdapter,position);
                } else {
                    onItemClick(mAdapter,position);
                }
            }
        }
    }

    private void onDeleteClick(ListMyMessagesRecyclerViewAdapter mAdapter, int position) {
        LocMessMessage message = mAdapter.getItem(position);

        String message_id = message.id();
        new DeleteMessageTask().execute(message_id);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            _mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            _mainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    _mainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            _progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            _mainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class DeleteMessageTask extends AsyncTask<String, Object, Boolean> {
        private String message_id;
        private String Tag = "DeleteMessageTask";
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                message_id = params[0];
                LocMessAPIClientImpl.getInstance().deleteMessage(message_id);
            } catch (APIException e) {
                return false;
            }
            return true;
        }

        @Override
        protected  void onPreExecute() {
            Log.wtf(Tag,"Pre execute");
            showProgress(true);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.wtf(Tag,"Post execute");

            if(!success) {
                final Snackbar snackbar = Snackbar.make(_mainView, "Could not connect to server", Snackbar.LENGTH_LONG);
                snackbar.show();
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
            } else {

                deleteMessage(message_id);
                ListMyMessagesRecyclerViewAdapter newA = new ListMyMessagesRecyclerViewAdapter();
                mRecyclerView.swapAdapter(newA,true);
                mAdapter = newA;
                treatEmptyView();
            }
            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            treatEmptyView();
        }
    }

    protected void deleteMessage(String message_id) {
        _dbHelper.getWritableDatabase().delete(DataStore.SQL_MESSAGES,"message_id = ?",new String[]{message_id});
        _dbHelper.getWritableDatabase().delete(DataStore.SQL_READ_MESSAGES,"message_id = ?",new String[]{message_id});
    }

}
