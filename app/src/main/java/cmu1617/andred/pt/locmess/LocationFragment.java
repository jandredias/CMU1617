package cmu1617.andred.pt.locmess;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by miguel on 07/04/17.
 */

public class LocationFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SQLDataStoreHelper _dbHelper;

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
}
