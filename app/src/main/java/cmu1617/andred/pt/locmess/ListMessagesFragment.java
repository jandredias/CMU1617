package cmu1617.andred.pt.locmess;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.UserProfile;

/**
 * Created by miguel on 06/04/17.
 */

public abstract class ListMessagesFragment extends Fragment {
    private final String TAG = "List Available Messages";
    protected SQLDataStoreHelper _dbHelper;
    private View _emptyView;
    protected View _mainView;
    private View _progressView;
    protected ListMessagesRecyclerViewAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    protected UserProfile _currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _dbHelper = new SQLDataStoreHelper(getContext());
        _currentUser = new UserProfile(_dbHelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _dbHelper.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_messages, container, false);
        _emptyView = view.findViewById(R.id.empty_messages);
        _mainView = view.findViewById(R.id.list_messages_fragment_main_view);
        _progressView = view.findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_messages_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new ClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view, int position) {
                LocMessMessage message = mAdapter.getItem(position);

                Intent intent = new Intent(getContext(),ShowMessageActivity.class);
                intent.putExtra("message_id",message.id());
                intent.putExtra("author",message.authorId());
                intent.putExtra("location_id",message.location().id());
                intent.putExtra("content",message.content());
                intent.putExtra("post_timestamp",message.postTimestamp());

                String timeEnd = message.timeEnd();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date endDate = null;
                try {
                    endDate = dateFormat.parse(timeEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date now = new Date();
                if(now.after(endDate)) {
                    new cleanExpiredMessages().execute();
                    new SimpleOkMessage(getContext(),"Message has expired");
                } else {
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        return view;
    }


    @Override
    public void onStart(){
        super.onStart();
        // specify an adapter (see also next example)
        mAdapter = createNewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        treatEmptyView();



    }

    public void restartListView() {
        ListMessagesRecyclerViewAdapter adapter = createNewAdapter();
        mRecyclerView.swapAdapter(adapter,false);

        mAdapter = adapter;
        treatEmptyView();
    }


    public abstract ListMessagesRecyclerViewAdapter createNewAdapter();

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

    private boolean checkIfEllipsized(TextView textView) {
        Layout layout = textView.getLayout();
        if (layout != null) {
            int lines = layout.getLineCount();
            if (lines > 0) {
                int ellipsisCount = layout.getEllipsisCount(lines - 1);
                if (ellipsisCount > 0) {
                    Log.d(TAG, "ellipsized: true. " + textView.getText().toString());
                    return true;
                }
            }
        }
        return false;
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

    protected abstract class ListMessagesRecyclerViewAdapter extends RecyclerView.Adapter<ListMessagesRecyclerViewAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View l = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new ViewHolder(l);
        }

        @Override
        public abstract int getItemCount();
        public abstract LocMessMessage getItem(int position);

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LocMessMessage message = getItem(position);
            ViewHolder v = holder;
            v._message.setText(message.content());
            v._author.setText("By: " + message.authorId());
            v._location.setText(message.location().name());




//            v._content_text = message.content();
//            v._author_text = message.authorId()
//            v._location_id_text = message.location().id();
//            v._message_id = message.id();
//            v._expire_date = message.timeEnd();

        }



        protected class ViewHolder extends RecyclerView.ViewHolder {
            View _holder;
            TextView _location;
            TextView _author;
            TextView _message;
//
//            String _content_text;
//            String _author_text;
//            String _location_id_text;
//            String _message_id;
//            String _expire_date;

            ViewHolder(View itemView) {
                super(itemView);
                _holder = itemView;
                _message = (TextView) itemView.findViewById(R.id.message_body_message_item);
                _location = (TextView) itemView.findViewById(R.id.location_name_message_item);
                _author = (TextView) itemView.findViewById(R.id.message_author_item);
            }
        }
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

    protected class cleanExpiredMessages extends AsyncTask<Void, Void, Boolean> {
        private String Tag = "cleanExpiredMessages";


        @Override
        protected void onPreExecute() {
            Log.d(Tag,"start");
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            _dbHelper.getWritableDatabase().delete(DataStore.SQL_MESSAGES,"time_end < datetime()",null);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d(Tag,"end");


        }


        @Override
        protected void onCancelled() {
            Log.d(Tag,"cancelled");
        }
    }
}
