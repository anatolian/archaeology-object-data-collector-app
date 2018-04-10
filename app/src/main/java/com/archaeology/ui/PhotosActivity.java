// Apply color correction
// @author: Kevin Trinh
package com.archaeology.ui;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.archaeology.R;
import com.archaeology.util.MagnifyingGlass;
public class PhotosActivity extends AppCompatActivity
{
    static Bitmap correctedPhoto;
    ImageView mCapturedImage;
    int northing, easting, find;
    Uri location;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        Bundle b = getIntent().getExtras();
        northing = Integer.parseInt(b.getString("northing"));
        easting = Integer.parseInt(b.getString("easting"));
        find = Integer.parseInt(b.getString("find"));
        location = (Uri) b.get("location");
        mCapturedImage = findViewById(R.id.capturedImage);
        try
        {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), location);
            mCapturedImage.setImageBitmap(bmp);
            filter(bmp);
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), "Could not open image", Toast.LENGTH_SHORT).show();
            Log.v("Image Loading", e.getMessage());
        }
        findViewById(R.id.save_image).setOnClickListener(new View.OnClickListener() {
            /**
             * Save image selected
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                try
                {
                    File parent = new File(Environment.getExternalStorageDirectory() + "/Archaeology/");
                    if (!parent.exists())
                    {
                        parent.mkdirs();
                    }
                    File f = new File(parent, + easting + "_" + northing + "_" + find + ".jpg");
                    f.createNewFile();
                    FileOutputStream outStream = new FileOutputStream(f);
                    mCapturedImage.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    Toast.makeText(getApplicationContext(), "Image stored at " + f.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Could not save file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Apply color correction
     * @param NEW_PHOTO - corrected image
     */
    public void filter(final Bitmap NEW_PHOTO)
    {
        final MagnifyingGlass IV = new MagnifyingGlass(this);
        IV.init(NEW_PHOTO);
        IV.setImageBitmap(NEW_PHOTO);
        IV.setScaleType(ImageView.ScaleType.FIT_CENTER);
        IV.setOnTouchListener(new ImageView.OnTouchListener() {
            /**
             * User touched image
             * @param v - image
             * @param event - touch event
             * @return Returns true
             */
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                IV.onTouchEvent(event);
                correctedPhoto = NEW_PHOTO;
                return true;
            }
        });
    }
}