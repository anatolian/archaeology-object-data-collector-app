/*
 * Copyright 2014 Sony Corporation
 */

package com.example.sony.cameraremote;

import com.example.sony.cameraremote.utils.DisplayHelper;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.io.IOException;

public class MovieContentActivity extends Activity {

    public static final String PARAM_MOVIE = "movie";

    public static final String PARAM_FILE_NAME = "name";

    private static final String TAG = MovieContentActivity.class.getSimpleName();

    private SimpleStreamSurfaceView mStreamSurface;

    private SimpleRemoteApi mRemoteApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() exec");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SampleApplication app = (SampleApplication) getApplication();
        mRemoteApi = app.getRemoteApi();
        if (mRemoteApi == null) {
            Log.w(TAG, "RemoteApi is null");
            DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
            return;
        }

        Log.d(TAG, "onResume() exec");

        Log.d(TAG, "prepare mStreamSurface");
        mStreamSurface = new SimpleStreamSurfaceView(getApplicationContext());

        setContentView(mStreamSurface);

        if (mStreamSurface.isStarted()) {
            return;
        }

        setProgressBarIndeterminateVisibility(true);

        final Intent intent = getIntent();
        setTitle(intent.getStringExtra(PARAM_FILE_NAME));
        final String uri = intent.getStringExtra(PARAM_MOVIE);

        startStreaming(uri);
    }

    private void startStreaming(final String uri) {
        new Thread() {

            @Override
            public void run() {

                JSONObject replyJson = null;

                try {
                    // Make target device ready to start content streaming
                    replyJson = mRemoteApi.setStreamingContent(uri);
                    if (SimpleRemoteApi.isErrorReply(replyJson)) {
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_connection);
                        return;
                    }

                    JSONObject resultsObj = replyJson.getJSONArray("result").getJSONObject(0);

                    if (resultsObj.length() < 1) {
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_connection);
                        return;
                    }

                    // Obtain streaming URL from the result.
                    String streamingUrl = resultsObj.getString("playbackUrl");

                    DisplayHelper.setProgressIndicator(MovieContentActivity.this, false);

                    // Make target device to start content streaming
                    replyJson = mRemoteApi.startStreaming();

                    if (SimpleRemoteApi.isErrorReply(replyJson)) {
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_connection);
                        return;
                    }

                    // Start retrieving image stream.
                    if (mStreamSurface == null) {
                        return;
                    }
                    mStreamSurface.start(streamingUrl, //
                            new SimpleStreamSurfaceView.StreamErrorListener() {

                                @Override
                                public void onError(StreamErrorReason reason) {
                                    Log.w(TAG, "Error startStreaming():" + reason.toString());
                                    DisplayHelper.toast(getApplicationContext(), //
                                            R.string.msg_error_connection);
                                    stopStreaming();
                                }
                            });

                } catch (IOException e) {
                    Log.w(TAG, "startStreaming: IOException: " + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_connection);
                    DisplayHelper.setProgressIndicator(MovieContentActivity.this, false);
                } catch (JSONException e) {
                    Log.w(TAG, "startStreaming: JSONException: " + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_connection);
                    DisplayHelper.setProgressIndicator(MovieContentActivity.this, false);
                }
            }
        }.start();
    }

    private void stopStreaming() {
        new Thread() {

            @Override
            public void run() {
                try {
                    mRemoteApi.stopStreaming();
                } catch (IOException e) {
                    Log.w(TAG, "stopStreaming: IOException: " + e.getMessage());
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() exec");
        mStreamSurface.stop();
        mStreamSurface = null;
        stopStreaming();
    }
}
