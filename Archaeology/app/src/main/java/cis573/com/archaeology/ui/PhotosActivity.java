package cis573.com.archaeology.ui;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import cis573.com.archaeology.R;
import cis573.com.archaeology.util.MagnifyingGlass;
import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;
public class PhotosActivity extends AppCompatActivity {
    LinearLayout llPhotoContainer;
    static Bitmap correctedPhoto;
    int northing, easting, context, sample, number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        Bundle b = getIntent().getExtras();
        northing = Integer.parseInt(b.getString("northing"));
        easting = Integer.parseInt(b.getString("easting"));
        context = Integer.parseInt(b.getString("context"));
        sample = Integer.parseInt(b.getString("sample"));
        number = Integer.parseInt(b.getString("number"));
        llPhotoContainer = (LinearLayout) findViewById(R.id.photo_container);
        findViewById(R.id.bt_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EZPhotoPickConfig config = new EZPhotoPickConfig();
                config.photoSource = PhotoSource.GALLERY;
                config.isAllowMultipleSelect = false;
                config.exportingSize = 1000;
                EZPhotoPick.startPhotoPickActivity(PhotosActivity.this, config);
            }
        });
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
                    File parent = new File(Environment.getExternalStorageDirectory()
                            + "/Archaeology/");
                    if (!parent.exists())
                    {
                        parent.mkdirs();
                    }
                    File f = new File(parent, + easting + "_" + northing + "_" + context + "_"
                            + sample + "_" + number + ".jpg");
                    f.createNewFile();
                    FileOutputStream outStream = new FileOutputStream(f);
                    correctedPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    Toast.makeText(getApplicationContext(), "Image stored at " +
                            f.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Could not save file",
                            Toast.LENGTH_SHORT).show();
                    Log.v("PHOTOS: ", e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE) {

            Log.v("GOOD REQUEST CODE", "Good request code");
            Bitmap photo = null;
            try {
                photo = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                Log.v("GRABBING PHOTO", "Grabbing photo...");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(photo != null) {
                Log.v("FILTERING", "Filtering...");
                filter(photo);
            }
        } else {
            Log.v("BAD REQUEST CODE", "Bad request code");
        }
    }


    public void filter(final Bitmap newPhoto)
    {
        Log.v("DRAWING", "Drawing...");
        final MagnifyingGlass iv = new MagnifyingGlass(this);
        iv.init(newPhoto);
        iv.setImageBitmap(newPhoto);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        llPhotoContainer.addView(iv);
        iv.setOnTouchListener(new ImageView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                iv.onTouchEvent(event);
                correctedPhoto = newPhoto;
                return true;
            }
        });
    }
}