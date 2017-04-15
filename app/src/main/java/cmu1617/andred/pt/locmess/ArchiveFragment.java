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

public class ArchiveFragment extends ListMessagesFragment {
    private final String TAG = "List Available Messages";

    @Override
    public ListMessagesRecyclerViewAdapter createNewAdapter() {
        return new ReadMessagesRecyclerViewAdapter();
    }

    protected class ReadMessagesRecyclerViewAdapter extends ListMessagesRecyclerViewAdapter {

        @Override
        public int getItemCount() {
            String[] selectionArgs = { _currentUser.userName() };
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_READ_MESSAGES, //table name
                    DataStore.SQL_READ_MESSAGES_COLUMNS, //columns to return
                    "reader_id = ?", //selection string
                    selectionArgs, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null //limit
            );
            int c = cursor.getCount();
            cursor.close();
            Log.d("Read Messages",c+"");
            return c;
        }

        public LocMessMessage getItem(int position) {
            String[] selectionArgs = { _currentUser.userName() };
            Cursor cursor = _dbHelper.getReadableDatabase().query(
                    true, //distinct
                    DataStore.SQL_READ_MESSAGES, //table name
                    DataStore.SQL_READ_MESSAGES_COLUMNS, //columns to return
                    "reader_id = ?", //selection string
                    selectionArgs, //selection args
                    null, //groupBy
                    null, //having
                    null, //orderBy
                    null //limit
            );
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            cursor.moveToPosition(cursor.getCount() - position - 1);

            return new LocMessMessage(_dbHelper, cursor.getString(0));
        }
    }

}
