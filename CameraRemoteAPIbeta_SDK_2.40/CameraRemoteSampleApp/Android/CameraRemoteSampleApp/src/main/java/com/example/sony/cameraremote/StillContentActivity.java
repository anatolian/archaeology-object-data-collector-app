/*
 * Copyright 2014 Sony Corporation
 */

package com.example.sony.cameraremote;

import com.example.sony.cameraremote.utils.DisplayHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class StillContentActivity extends Activity {

    private static final String TAG = StillContentActivity.class.getSimpleName();

    public static final String PARAM_IMAGE = "image";

    public static final String PARAM_FILE_NAME = "name";

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Log.d(TAG, "onCreate() exec");

        setContentView(R.layout.activity_still_content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() exec");
        mImageView = (ImageView) findViewById(R.id.still_content);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra(PARAM_FILE_NAME));
        String url = intent.getStringExtra(PARAM_IMAGE);

        setProgressBarIndeterminateVisibility(true);
        showImage(url);
    }

    private void showImage(final String url) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                InputStream istream = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                try {
                    URL imageUrl = new URL(url);
                    istream = imageUrl.openStream();
                    byte[] image = readBytes(istream);

                    // confirm image size
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(image, 0, image.length, options);

                    // resize rate
                    int scaleWidth = options.outWidth / mImageView.getWidth() + 1;
                    int scaleHeight = options.outHeight / mImageView.getHeight() + 1;
                    int resizeRate = Math.min(scaleWidth, scaleHeight);

                    // decode with specific resize rate.
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = resizeRate;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);

                    setImageToView(mImageView, bitmap);
                } catch (IOException e) {
                    Log.w(TAG, "showImage: IOException:" + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                } finally {
                    if (istream != null) {
                        try {
                            istream.close();
                        } catch (IOException e) {
                            Log.e(TAG, "showImage: fail stream close");
                            DisplayHelper.toast(getApplicationContext(), //
                                    R.string.msg_error_content);
                        }
                    }
                    DisplayHelper.setProgressIndicator(StillContentActivity.this, false);
                }
            }
        }).start();
    }

    private void setImageToView(final ImageView imageView, final Bitmap image) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                imageView.setImageBitmap(image);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        BitmapDrawable bmpDrawable = (BitmapDrawable) mImageView.getDrawable();
        if (bmpDrawable != null) {
            Bitmap bitmap = bmpDrawable.getBitmap();
            if (bitmap != null) {
                mImageView.setImageDrawable(null);
                bitmap.recycle();
            }
        }
    }

    /**
     * Reads byte array from input stream.
     * 
     * @param in
     * @return
     * @throws IOException
     */
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
                Log.d(TAG, "readByte() IOException");
            }
        }

        byte[] ret = tmpByteArray.toByteArray();
        return ret;
    }
}
