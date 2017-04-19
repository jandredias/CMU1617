package cmu1617.andred.pt.locmess;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import cmu1617.andred.pt.locmess.Domain.LocMessMessage;
import cmu1617.andred.pt.locmess.Domain.UserProfile;

/**
 * Created by miguel on 06/04/17.
 */

public abstract class ListMessagesFragment extends Fragment {
    private final String TAG = "List Available Messages";
    protected SQLDataStoreHelper _dbHelper;
    private View _emptyView;
    private ListMessagesRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_messages_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LocMessMessage message = mAdapter.getItem(position);

                Intent intent = new Intent(getContext(),ShowMessageActivity.class);
                intent.putExtra("message_id",message.id());
                startActivity(intent);
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


    public abstract ListMessagesRecyclerViewAdapter createNewAdapter();

    private void treatEmptyView(){

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
//            v._name.setText(location.name());
        }



        protected class ViewHolder extends RecyclerView.ViewHolder {
            View _holder;
            TextView _location;
            TextView _author;
            TextView _message;

            ViewHolder(View itemView) {
                super(itemView);
                _holder = itemView;
                _message = (TextView) itemView.findViewById(R.id.message_body_message_item);
                _location = (TextView) itemView.findViewById(R.id.location_name_message_item);
                _author = (TextView) itemView.findViewById(R.id.message_author_item);
            }
        }
    }

}
