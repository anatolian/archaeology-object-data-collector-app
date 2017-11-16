// Main Screen
// @author: Kevin Trinh
package com.example.kevin.photosapp;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.IOException;
import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;
public class MainActivity extends AppCompatActivity
{
    LinearLayout llPhotoContainer;
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llPhotoContainer = (LinearLayout) findViewById(R.id.photo_container);
        findViewById(R.id.bt_gallery).setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked photo
             * @param v - photo
             */
            @Override
            public void onClick(View v)
            {
                EZPhotoPickConfig config = new EZPhotoPickConfig();
                config.photoSource = PhotoSource.GALLERY;
                config.isAllowMultipleSelect = false;
                config.exportingSize = 1000;
                EZPhotoPick.startPhotoPickActivity(MainActivity.this, config);
            }
        });
    }

    /**
     * Activity finished
     * @param requestCode - result request code
     * @param resultCode - result status
     * @param data - result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            return;
        }
        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE ||
                requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE)
        {
            Log.v("GOOD REQUEST CODE", "Good request code");
            Bitmap photo = null;
            try
            {
                photo = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                Log.v("GRABBING PHOTO", "Grabbing photo...");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if (photo != null)
            {
                Log.v("FILTERING", "Filtering...");
                filter(photo);
            }
        }
        else
        {
            Log.v("BAD REQUEST CODE", "Bad request code");
        }
    }

    /**
     * Filter image
     * @param photo - image to filter
     */
    public void filter(Bitmap photo)
    {
        // according to PhotoDirector, "white square" is RGB(214, 204, 167)
        // so correction should be RGB(+0, +10, +47)
        drawPhoto(photo);
    }

    /**
     * Display image
     * @param newPhoto - new image
     */
    public void drawPhoto(Bitmap newPhoto)
    {
        final Bitmap touchedPhoto = newPhoto;
        Log.v("DRAWING", "Drawing...");
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(touchedPhoto);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        llPhotoContainer.addView(iv);
        iv.setOnTouchListener(new ImageView.OnTouchListener() {
            /**
             * User touched image
             * @param v - image
             * @param event - touch event
             * @return Returns whether the event was handled
             */
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_UP:
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        int pixel = touchedPhoto.getPixel(x, y);
                        int[] rgbValues = new int[3];
                        rgbValues[0] = Color.red(pixel);
                        rgbValues[1] = Color.green(pixel);
                        rgbValues[2] = Color.blue(pixel);
                        int[] correctionMatrix = calcColorCorrectionMatrix(rgbValues,
                                maxChannelIndex(rgbValues));
                        Toast.makeText(getApplicationContext(), "RGB values: " + rgbValues[0]
                                + ", " + rgbValues[1] + ", " + rgbValues[2],
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "correction values: "
                                + correctionMatrix[0] + ", " + correctionMatrix[1] + ", "
                                + correctionMatrix[2], Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * color correction works by balancing RGB values on a white pixel. looks at highest channel
     * value increases other channels to match. according to PhotoDirector, "white" square is
     * RGB(214, 204, 167), so correction should be RGB(+0, +10, +47)
     * @param rgbValues - pixel
     * @param maxChannelIndex - location of max channel
     * @return Returns the color corrected pixel
     */
    public int[] calcColorCorrectionMatrix(int[] rgbValues, int maxChannelIndex)
    {
        int[] correctionMatrix = new int[3];
        for (int i = 0; i < rgbValues.length; i++)
        {
            correctionMatrix[i] = rgbValues[maxChannelIndex] - rgbValues[i];
        }
        return correctionMatrix;
    }

    /**
     * Find the max channel
     * @param rgbValues - 0 = red, 1 = green, 2 = blue
     * @return Returns the index of the channel with the highest value.
     */
    public int maxChannelIndex(int[] rgbValues)
    {
        int index = -1;
        int colorValue = -1;
        for (int i = 0; i < rgbValues.length; i++)
        {
            if (rgbValues[i] > colorValue)
            {
                index = i;
                colorValue = rgbValues[i];
            }
        }
        return index;
    }
}