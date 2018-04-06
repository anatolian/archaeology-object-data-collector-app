/*
 * Copyright 2014 Sony Corporation
 */

package com.example.sony.cameraremote;

import com.example.sony.cameraremote.utils.DisplayHelper;
import com.example.sony.cameraremote.utils.SimpleRemoteApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DateListActivity extends ListActivity {

    private static final String TAG = DateListActivity.class.getSimpleName();

    private SimpleRemoteApi mRemoteApi;

    private DateListAdapter mDateArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() exec");

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_date_list);

        mDateArrayAdapter = new DateListAdapter(getApplicationContext());
        setListAdapter(mDateArrayAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() exec");

        SampleApplication app = (SampleApplication) getApplication();
        mRemoteApi = app.getRemoteApi();
        if (mRemoteApi == null) {
            Log.w(TAG, "Remote Api is null");
            DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
            return;
        }

        setProgressBarIndeterminateVisibility(true);
        updateDateList();
    }

    /**
     * download contents' date information from device and update date list
     */
    private void updateDateList() {
        Log.d(TAG, "updateDateList() exec.");

        new Thread() {

            @Override
            public void run() {
                try {
                    // get date list from device.
                    JSONObject replyJson = SimpleRemoteApiHelper.getContentDateList(mRemoteApi);
                    if (!SimpleRemoteApi.isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result").getJSONArray(0);

                        // update date list
                        updateDateAdapter(resultsObj);
                        return;
                    }
                } catch (IOException e) {
                    Log.w(TAG, "updateDateList: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "updateDateList: JSON format error:" + e.getMessage());
                }
                
                DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                DisplayHelper.setProgressIndicator(DateListActivity.this, false);
            }
        }.start();

    }

    /**
     * Pick up "Browsable" items to display.
     * 
     * @param items
     */
    private void updateDateAdapter(JSONArray items) {
        Log.d(TAG, "updateDateAdapter() exec.");
        List<DateInfo> dateList = new ArrayList<DateInfo>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonContent = items.getJSONObject(i);
                if ("true".equals(jsonContent.getString("isBrowsable"))) {
                    dateList.add(
                            new DateInfo(jsonContent.getString("title"),
                                    jsonContent.getString("uri")));
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "updateDateAdapter: JSON format error:" + e.getMessage());
            DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
            DisplayHelper.setProgressIndicator(DateListActivity.this, false);
        }

        addItemsToAdapter(dateList);
    }

    /**
     * add dateList to Adapter.
     * 
     * @param dateList
     */
    private void addItemsToAdapter(final List<DateInfo> dateList) {
        Log.d(TAG, "addItemsToAdapter() exec");

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mDateArrayAdapter.clearDates();
                for (DateInfo item : dateList) {
                    mDateArrayAdapter.addDate(item);
                }
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        Log.d(TAG, "Click:" + mDateArrayAdapter.getItem(position).getDate());
        Intent intent = new Intent(getApplicationContext(), ContentsGridActivity.class);
        intent.putExtra(ContentsGridActivity.PARAM_DATE, //
                mDateArrayAdapter.getItem(position).getUri());
        intent.putExtra(ContentsGridActivity.PARAM_TITLE, //
                mDateArrayAdapter.getItem(position).getDate());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        this.setListAdapter(null);
        super.onDestroy();
    }

    private static class DateInfo {
        private String mDate;

        private String mUri;

        public DateInfo(String date, String uri) {
            mDate = date;
            mUri = uri;
        }

        private String getDate() {
            return mDate;
        }

        private String getUri() {
            return mUri;
        }
    }

    /**
     * Adapter class for date list
     */
    private static class DateListAdapter extends BaseAdapter {

        private final List<DateInfo> mDateList;

        private final LayoutInflater mInflater;

        public DateListAdapter(Context context) {
            mDateList = new ArrayList<DateInfo>();
            mInflater = LayoutInflater.from(context);
        }

        public void addDate(DateInfo item) {
            mDateList.add(item);
            notifyDataSetChanged();
        }

        public void clearDates() {
            mDateList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDateList.size();
        }

        @Override
        public DateInfo getItem(int position) {
            return mDateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) convertView;
            if (textView == null) {
                textView = (TextView) mInflater.inflate(R.layout.date_list_item, parent, false);
            }

            textView.setText(getItem(position).getDate());

            return textView;
        }
    }
}
