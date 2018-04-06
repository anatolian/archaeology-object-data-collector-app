/*
 * Copyright 2014 Sony Corporation
 */

package com.example.sony.cameraremote;

import com.example.sony.cameraremote.utils.DisplayHelper;
import com.example.sony.cameraremote.utils.SimpleRemoteApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class ContentsGridActivity extends Activity {

    private static final String TAG = ContentsGridActivity.class.getSimpleName();

    private static final int THREAD_NUM = 8;

    public static final String PARAM_DATE = "date";

    public static final String PARAM_TITLE = "title";

    private static final List<String> STREAMING_API = Collections.unmodifiableList(
            Arrays.asList(
                    "setStreamingContent",
                    "startStreaming",
                    "stopStreaming"));

    private SimpleRemoteApi mRemoteApi;

    private PhotoGridAdapter mPhotoGridAdapter;

    private GridView mGridView;

    private SimpleCameraEventObserver mEventObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() exec");

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // set grid view
        setContentView(R.layout.activity_contents_grid);
        mGridView = (GridView) findViewById(R.id.contents_grid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() exec");

        // get RemoteApi
        SampleApplication app = (SampleApplication) getApplication();
        mRemoteApi = app.getRemoteApi();
        if (mRemoteApi == null) {
            Log.w(TAG, "RemoteApi is null");
            DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
            return;
        }

        // set Title
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(PARAM_TITLE));

        // set adapter
        mPhotoGridAdapter = new PhotoGridAdapter(this);
        mGridView.setAdapter(mPhotoGridAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "ContentsGridActivity Click" + position);

                Intent intent = null;
                PhotoThumbnail item = mPhotoGridAdapter.getItem(position);

                if ("still".equals(item.getContentKind())) {
                    // Still content, start StillContentActivity
                    Log.d(TAG, "Item[" + position + "] is still content");
                    intent = new Intent(getApplicationContext(), StillContentActivity.class);
                    intent.putExtra(StillContentActivity.PARAM_IMAGE, item.getLargeUrl());
                    intent.putExtra(StillContentActivity.PARAM_FILE_NAME, item.getFileName());
                } else if ("movie_mp4".equals(item.getContentKind())
                        || "movie_xavcs".equals(item.getContentKind())) {
                    Log.d(TAG, "Item[" + position + "] is movie content");
                    if (isStreamingPlayable()) {
                        intent = new Intent(getApplicationContext(), MovieContentActivity.class);
                        intent.putExtra(MovieContentActivity.PARAM_MOVIE, item.getUri());
                        intent.putExtra(MovieContentActivity.PARAM_FILE_NAME, item.getFileName());
                    } else {
                        DisplayHelper.toast(getApplicationContext(), R.string.msg_error_streaming_playback);
                        return;
                    }
                } else {
                    Log.w(TAG,
                            "Item[" + position + "] is unknown content type :"//
                                    + item.getContentKind());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                    return;
                }
                startActivity(intent);
            }
        });

        setProgressBarIndeterminateVisibility(true);

        // start SimpleCameraEventObserver
        mEventObserver = app.getCameraEventObserver();
        mEventObserver.activate();
        setCameraStatusChangeListener();
        mEventObserver.start();
    }

    /**
     * @return Return {@code true} if movie contents are playable.
     */
    private boolean isStreamingPlayable() {
        SampleApplication app = (SampleApplication) getApplication();
        return app.getSupportedApiList().containsAll(STREAMING_API);
    }

    /**
     * download thumbnail data from device and update them to view
     */
    private void updateThumbnails() {
        Log.d(TAG, "updateThumbnails() exec.");

        final String uri = getIntent().getStringExtra(PARAM_DATE);

        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = SimpleRemoteApiHelper.getContentListOfDay(
                            mRemoteApi, uri);
                    if (SimpleRemoteApi.isErrorReply(replyJson)) {
                        // error case
                        JSONArray resultsObj = replyJson.getJSONArray("error");
                        int resultCode = resultsObj.getInt(0);
                        if (resultCode == 7) {
                            // not available now.
                            // call again after camera status is changed to
                            // ContentsTransfer.
                        } else {
                            mEventObserver.release();
                            mEventObserver = null;
                            Log.w(TAG, "updateThumbnails: Error:" + resultCode);
                            DisplayHelper.toast(getApplicationContext(), //
                                    R.string.msg_error_content);
                            DisplayHelper.setProgressIndicator(//
                                    ContentsGridActivity.this, false);
                        }
                    } else {
                        // success case
                        JSONArray resultParams = replyJson.getJSONArray("result");
                        JSONArray resultsObj = resultParams.getJSONArray(0);

                        updatePhotoAdapter(resultsObj);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "updateThumbnails: IOException: " + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                    DisplayHelper.setProgressIndicator(ContentsGridActivity.this, false);
                } catch (JSONException e) {
                    Log.w(TAG, "updateThumbnails: JSON format error." + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                    DisplayHelper.setProgressIndicator(ContentsGridActivity.this, false);
                }
            }
        }.start();

    }

    private void setCameraStatusChangeListener() {
        // call getContentsList after camera status is changed to ContentsTransfer.
        Log.d(TAG, "update after status change.");
        mEventObserver
                .setEventChangeListener(new SimpleCameraEventObserver.ChangeListenerTmpl() {

                    @Override
                    public void onCameraStatusChanged(String status) {
                        Log.d(TAG, "onCameraStatusChanged:" + status);
                        if ("ContentsTransfer".equals(status)) {
                            updateThumbnails();
                        }
                    }

                    @Override
                    public void onResponseError() {
                        Log.d(TAG, "onResponseError");
                        DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                        DisplayHelper.setProgressIndicator(ContentsGridActivity.this, false);
                    }
                });
    }

    /**
     * Pick up non Browsable items to display.
     * 
     * @param items
     */
    private void updatePhotoAdapter(JSONArray items) {
        Log.d(TAG, "updatePhotoAdapter() exec.");
        List<PhotoThumbnail> photoList = new ArrayList<PhotoThumbnail>();

        JSONObject jsonContent;
        boolean error_content = false;
        for (int i = 0; i < items.length(); i++) {
            try {
                jsonContent = items.getJSONObject(i);
                if ("false".equals(jsonContent.getString("isBrowsable"))) {
                    JSONObject contentInfo = jsonContent.getJSONObject("content");
                    String fileName = contentInfo.getJSONArray("original").getJSONObject(0)//
                            .getString("fileName");
                    photoList.add(new PhotoThumbnail(//
                            contentInfo.optString("thumbnailUrl"), //
                            contentInfo.optString("largeUrl"), //
                            jsonContent.optString("contentKind"), //
                            jsonContent.optString("uri"), //
                            fileName));
                }
            } catch (JSONException e) {
                Log.w(TAG, "updatePhotoAdapter: JSON format error." + e.getMessage());
                error_content = true;
            }
        }
        if (error_content) {
            DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
        }
        addItemsToAdapter(photoList);
    }

    /*
     * add thumbnails to Adapter.
     */
    private void addItemsToAdapter(final List<PhotoThumbnail> photoList) {
        Log.d(TAG, "addItemsToAdapter() exec");
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mPhotoGridAdapter.clearThumbnails();
                for (PhotoThumbnail item : photoList) {
                    mPhotoGridAdapter.addThumbnail(item);
                }
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() exec");
        if (mEventObserver != null) {
            mEventObserver.release();
            mEventObserver.clearEventChangeListener();
        }
        mPhotoGridAdapter.release();
        mGridView.setAdapter(null);
    }

    private static class PhotoThumbnail {

        private String mThumbnailUrl;

        private String mLargeUrl;

        private String mContentKind;

        private String mUri;

        private String mFileName;

        PhotoThumbnail(String thumbnailUrl, String largeUrl, //
                String contentKind, String uri, String fileName) {
            mThumbnailUrl = thumbnailUrl;
            mLargeUrl = largeUrl;
            mContentKind = contentKind;
            mUri = uri;
            mFileName = fileName;
        }

        private String getThumbnailUrl() {
            return mThumbnailUrl;
        }

        private String getLargeUrl() {
            return mLargeUrl;
        }

        private String getContentKind() {
            return mContentKind;
        }

        private String getUri() {
            return mUri;
        }

        private String getFileName() {
            return mFileName;
        }
    }

    /**
     * adapter class for photo thumbnail
     */
    private static class PhotoGridAdapter extends BaseAdapter {

        private final List<PhotoThumbnail> mPhotoList;

        private final LayoutInflater mInflater;

        private boolean mIsActive;

        private ExecutorService mExecutor;

        private Handler mUiHandler;

        private Map<View, String> mViewMap;

        private Activity mActivity;

        PhotoGridAdapter(Activity activity) {
            mPhotoList = new ArrayList<PhotoThumbnail>();
            mInflater = LayoutInflater.from(activity.getApplicationContext());
            mIsActive = true;
            mExecutor = Executors.newFixedThreadPool(THREAD_NUM);
            mUiHandler = new Handler();
            mViewMap = new ConcurrentHashMap<View, String>();
            mActivity = activity;
        }

        public void addThumbnail(PhotoThumbnail item) {
            mPhotoList.add(item);
            notifyDataSetChanged();
        }

        public void clearThumbnails() {
            mPhotoList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public PhotoThumbnail getItem(int position) {
            return mPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = (View) mInflater.inflate(R.layout.photo_grid_item, parent, false);
            } else {
                // recycle bitmap that is used in old view
                final ImageView imageView = //
                (ImageView) convertView.findViewById(R.id.photo_grid_image);
                recycleBitmap(imageView);

                // clear still and movie mark.
                View textView = convertView.findViewById(R.id.text_movie_mark);
                textView.setVisibility(View.GONE);
            }

            mViewMap.put(convertView, getItem(position).getThumbnailUrl());

            downloadImage(position, convertView);
            return convertView;
        }

        private void downloadImage(final int position, final View view) {

            final PhotoThumbnail item = getItem(position);
            if(TextUtils.isEmpty(item.getThumbnailUrl())) {
                Log.d(TAG, "downloadImage: thumbnailUrl is empty");
                if (mIsActive) {
                    DisplayHelper.toast(mActivity.getApplicationContext(), //
                            R.string.msg_error_content);
                    DisplayHelper.setProgressIndicator(mActivity, false);
                }
                return;
            }
            if ("still".equals(item.getContentKind()) && TextUtils.isEmpty(item.getLargeUrl())) {
                Log.d(TAG, "downloadImage: largeUrl is empty");
                if (mIsActive) {
                    DisplayHelper.toast(mActivity.getApplicationContext(), //
                            R.string.msg_error_content);
                    DisplayHelper.setProgressIndicator(mActivity, false);
                }
                return;
            }

            try {
                mExecutor.execute(new Runnable() {

                    @Override
                    public void run() {
                        InputStream istream = null;
                        try {
                            URL url = new URL(item.getThumbnailUrl());
                            istream = url.openStream();
                            byte[] imageByte = readBytes(istream);

                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
                                    imageByte.length);

                            if (mActivity == null) {
                                throw new IOException("mActivity is null");
                            }
                            final Drawable image =
                                    new BitmapDrawable(//
                                            mActivity.getApplicationContext().getResources(), //
                                            bitmap);

                            // Check whether the url is latest or not.
                            boolean isUrlLatest = false;
                            String latestUrl = mViewMap.get(view);
                            isUrlLatest = (latestUrl != null//
                                    && latestUrl.equals(item.getThumbnailUrl()));

                            if (isUrlLatest) {
                                // this url is the latest. Apply this bitmap to
                                // the view.
                                mUiHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        setImageToView(view, image);

                                        if ("still".equals(item.getContentKind())) {
                                            TextView textView = (TextView) view
                                                    .findViewById(R.id.text_movie_mark);
                                            textView.setText("still");
                                            textView.setVisibility(View.VISIBLE);
                                        } else if ("movie_mp4".equals(item.getContentKind())
                                                || "movie_xavcs".equals(item.getContentKind())) {
                                            TextView textView = (TextView) view
                                                    .findViewById(R.id.text_movie_mark);
                                            textView.setText("movie");
                                            textView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            } else {
                                // this url is not the latest.
                                // Ignore this bitmap and recycle.
                                Log.d(TAG, "downloaded bitmap is not latest.");
                                bitmap.recycle();
                            }

                        } catch (IOException e) {
                            Log.d(TAG, "downloadImage: IOException: " + e.getMessage());
                            if (mIsActive) {
                                DisplayHelper.toast(mActivity.getApplicationContext(), //
                                        R.string.msg_error_content);
                                DisplayHelper.setProgressIndicator(mActivity, false);
                            }
                        } finally {
                            if (istream != null) {
                                try {
                                    istream.close();
                                } catch (IOException e) {
                                    Log.e(TAG,
                                            "downloadImage: fail stream close :" + e.getMessage());
                                }
                            }
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
                Log.d(TAG, "downloadImage: RejectedExecutionException");
            }
        }

        private void setImageToView(final View view, final Drawable image) {
            final ImageView imageView = (ImageView) view.findViewById(R.id.photo_grid_image);

            recycleBitmap(imageView);

            imageView.setImageDrawable(image);
        }

        private void release() {
            Log.d(TAG, "release() exec");
            mIsActive = false;
            mExecutor.shutdown();
            mExecutor.shutdownNow();

            for (View view : mViewMap.keySet()) {
                final ImageView imageView = (ImageView) view.findViewById(R.id.photo_grid_image);
                recycleBitmap(imageView);

                View textView = view.findViewById(R.id.text_movie_mark);
                textView.setVisibility(View.GONE);
            }

            mViewMap.clear();
            mPhotoList.clear();
            mActivity = null;
        }

        private void recycleBitmap(final ImageView imageView) {
            // recycle bitmap that is used in old view
            BitmapDrawable bmpDrawable = (BitmapDrawable) imageView.getDrawable();
            if (bmpDrawable != null) {
                Bitmap bitmap = bmpDrawable.getBitmap();
                if (bitmap != null) {
                    imageView.setImageDrawable(null);
                    bitmap.recycle();
                }
            }
        }

        private static byte[] readBytes(InputStream in) throws IOException {
            ByteArrayOutputStream tmpByteArray = new ByteArrayOutputStream();
            byte[] buffer = new byte[32000];
            try {
                while (true) {
                    int readlen = in.read(buffer, 0, buffer.length);
                    if (readlen < 0) {
                        break;
                    }
                    tmpByteArray.write(buffer, 0, readlen);
                }
            } finally {
                try {
                    tmpByteArray.close();
                } catch (IOException e) {
                    Log.d(TAG, "readBytes() IOException.");
                }
            }

            byte[] ret = tmpByteArray.toByteArray();
            return ret;

        }
    }
}
