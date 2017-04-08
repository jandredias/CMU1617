package cmu1617.andred.pt.locmess;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cmu1617.andred.pt.locmess.Domain.LocMessLocation;
import pt.andred.cmu1617.LocMessAPIClientImpl;

/**
 * Created by miguel on 07/04/17.
 */

public class LocationFragment extends Fragment {
    private String Tag = "Location Fragment";
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SQLDataStoreHelper _dbHelper;
    private SwipeRefreshLayout _refreshLayout;
    private View _emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _dbHelper = new SQLDataStoreHelper(getContext());

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

        View view = inflater.inflate(R.layout.location_list_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.location_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        _noMessagesLayout = v.findViewById(R.id.empty_chats);
//
//        if (_noMessagesLayout != null) {
//            if (_adapter.getItemCount() != 0) {
//                _noMessagesLayout.setVisibility(View.GONE);
//            } else {
//                _noMessagesLayout.setVisibility(View.VISIBLE);
//            }
//        }
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LocMessLocation location = mAdapter.getItem(position);
                Toast.makeText(getContext(), location.name() + " is selected!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(),ListMessagesActivity.class);
                intent.putExtra("location_id",location.id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        _emptyView = view.findViewById(R.id.empty_locations);
        _refreshLayout = (SwipeRefreshLayout) view;
        final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetLocationsTask().execute();
            }
        };

        _refreshLayout.setOnRefreshListener(onRefreshListener);
        _refreshLayout.post(new Runnable() {
            @Override public void run() {
                _refreshLayout.setRefreshing(true);
                onRefreshListener.onRefresh();

            }
        });
        treatEmptyView();

        return view;
    }

    private void treatEmptyView(){

        if (_emptyView  != null) {
            if (mAdapter.getItemCount() != 0) {
                _emptyView .setVisibility(View.GONE);
            } else {
                _emptyView .setVisibility(View.VISIBLE);
            }
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View l = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
            return new ViewHolder(l);
        }

        public LocMessLocation getItem(int position) {
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    DataStore.SQL_WIFI_LOCATION, //table name
                    DataStore.SQL_WIFI_LOCATION_COLUMNS, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null //orderBy
            );
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            cursor.moveToPosition(cursor.getCount() - position - 1);

            return new LocMessLocation(_dbHelper, cursor.getString(0));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LocMessLocation location = getItem(position);
            ViewHolder v = holder;

            v._name.setText(location.name());
        }

        @Override
        public int getItemCount() {
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    DataStore.SQL_WIFI_LOCATION, //table name
                    DataStore.SQL_WIFI_LOCATION_COLUMNS, //columns to return
                    null, //selection string
                    null, //selection args
                    null, //groupBy
                    null, //having
                    null //orderBy
            );
            int c = cursor.getCount();
            cursor.close();
            return c;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View _holder;
            TextView _name;



            ViewHolder(View itemView) {
                super(itemView);
                _holder = itemView;
                _name = (TextView) itemView.findViewById(R.id.location_name);
            }
        }
    }
    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class GetLocationsTask extends AsyncTask<Object, Object, Boolean> {
        @Override
        protected  void onPreExecute() {
            Log.wtf(Tag,"Pre execute");
            if (_refreshLayout != null) {
                _refreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            JSONObject result = LocMessAPIClientImpl.getInstance().listLocations();
            try {
                JSONArray wifiMap = null;
                JSONArray gpsMap = null;

                wifiMap = result.getJSONArray("wifi");
                gpsMap = result.getJSONArray("coordinates");

                for (int i = 0; i < wifiMap.length(); i++) {
                    JSONObject location = wifiMap.getJSONObject(i);
                    String name = location.getString("name");
                    String location_id = location.getString("location_id");
                    String[] ssid_list = location.getString("list").split(",");
                    Log.d(Tag,"new location");
                    LocMessLocation loc = new LocMessLocation(_dbHelper, location_id);
                    loc.name(name);
                }
                for (int i = 0; i < gpsMap.length(); i++) {
                    JSONObject location = gpsMap.getJSONObject(i);
                    String name = location.getString("name");
                    String location_id = location.getString("location_id");
                    String latitude = location.getString("latitude");
                    String longitude = location.getString("longitude");
                    String radius = location.getString("radius");
                    Log.d(Tag,"new location");
                    LocMessLocation loc = new LocMessLocation(_dbHelper, location_id);
                    loc.name(name);
//                    LocMessLocation loc = new LocMessLocation(_dbHelper, location_id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            Log.wtf(Tag,"Post execute");
            if (_refreshLayout != null) {
                _refreshLayout.setRefreshing(false);
            }
            RecyclerViewAdapter newA = new RecyclerViewAdapter();


            mRecyclerView.swapAdapter(newA,true);
            mAdapter = newA;

        }

        @Override
        protected void onCancelled() {
            if (_refreshLayout != null) {
                _refreshLayout.setRefreshing(false);
            }
            treatEmptyView();


        }
    }

}

