// Camera activity for updating many images
package excavation.excavation_app.com.appenginedemo;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.sample.AddSamplePhotoTask;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.appenginedemo.R;
public class ActivityCamera1 extends ActivityBase
{
    LayoutInflater inflater;
    RelativeLayout rLayout;
    ImageView cameraImage;
    static String imagePath;
    String samNo, conNo, material, type, sam, north, east, img, act3D;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    // file url to store image/video
    private Uri fileURI;
    /**
     * Activity launch
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        rLayout = (RelativeLayout) inflater.inflate(R.layout.activity_camera,null);
        wrapper.addView(rLayout);
        linearLayout2.setVisibility(View.GONE);
        cameraImage = (ImageView) findViewById(R.id.cemera_image);
        if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
                || getIntent().hasExtra("imagePath"))
        {
            north = getIntent().getExtras().getString("north");
            east = getIntent().getExtras().getString("east");
            img = getIntent().getExtras().getString("imagePath");
        }
        if (getIntent().hasExtra("samp_no") || getIntent().hasExtra("material")
                || getIntent().hasExtra("type") || getIntent().hasExtra("Context_no")
                || getIntent().hasExtra("sam"))
        {
            samNo = getIntent().getExtras().getString("samp_no");
            material = getIntent().getExtras().getString("material");
            type = getIntent().getExtras().getString("type");
            conNo = getIntent().getExtras().getString("Context_no");
            sam = getIntent().getExtras().getString("sam");
        }
        if (getIntent().hasExtra("3d"))
        {
            act3D = getIntent().getExtras().getString("3d");
        }
        captureImage();
    }

    /**
     * Release activity
     */
    public void release()
    {
    }

    /**
     * Take picture
     */
    public void captureImage()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileURI = getOutputMediaFileURI(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Activity ended
     * @param requestCode - result request code
     * @param resultCode - result code
     * @param data - returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                // successfully captured the image display it in image view
                try
                {
                    Bitmap thumbnail = decodeURI(fileURI);
                    cameraImage.setVisibility(View.VISIBLE);
                    cameraImage.setImageBitmap(thumbnail);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                if (sam != null && sam.length() > 0)
                {
                    if (AppConstants.sampleSelectedImg != null &&
                            AppConstants.sampleSelectedImg.size() > 0)
                    {
                        if (imagePath != null && imagePath.length() > 0)
                        {
                            Log.e("array image size > 0", imagePath);
                            AppConstants.sampleSelectedImg.add(imagePath);
                        }
                    }
                    else
                    {
                        AppConstants.sampleSelectedImg = new ArrayList<>();
                        if (imagePath != null && imagePath.length() > 0)
                        {
                            Log.e("array image size is 0", imagePath);
                            AppConstants.sampleSelectedImg.add(imagePath);
                        }
                    }
                    if (AppConstants.sampleSelectedImg != null
                            && AppConstants.sampleSelectedImg.size() > 0)
                    {
                        if (east != null && east.length() > 0 && north != null &&
                                north.length() > 0 && conNo != null && conNo.length() > 0 &&
                                samNo != null && samNo.length() > 0 && type != null &&
                                type.length() > 0)
                        {
                            AddSamplePhotoTask task = new AddSamplePhotoTask(
                                    ActivityCamera1.this, east, north,
                                    AppConstants.sampleSelectedImg, conNo, samNo, type);
                            task.execute();
                        }
                    }
                }
                else
                {
                    if (AppConstants.selectedImg != null && AppConstants.selectedImg.size() > 0)
                    {
                        if (imagePath != null && imagePath.length() > 0)
                        {
                            AppConstants.selectedImg.add(imagePath);
                        }
                    }
                    else
                    {
                        AppConstants.selectedImg = new ArrayList<>();
                        if (imagePath != null && imagePath.length() > 0)
                        {
                            AppConstants.selectedImg.add(imagePath);
                        }
                    }
                    if (imagePath != null && imagePath.length() > 0)
                    {
                        finish();
                        Intent i = new Intent(ActivityCamera1.this, Activity3D.class);
                        i.putExtra("y", "yes");
                        i.putExtra("imagePath", imagePath);
                        i.putExtra("north", north);
                        i.putExtra("east", east);
                        startActivity(i);
                    }
                }
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Activity switched to memory
     * @param outState - state in memory
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // save file URL in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", fileURI);
    }

    /**
     * Switched into from memory
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file URL
        fileURI = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Read file
     * @param selectedImage - file location
     * @return Returns the image
     * @throws FileNotFoundException if the file does not exist
     */
    private Bitmap decodeURI(Uri selectedImage) throws FileNotFoundException
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage),
                null, o);
        final int REQUIRED_SIZE = 100;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true)
        {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
            {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage),
                null, o2);
    }

    /**
     * Creating file uri to store image/video
     * @param type - file type
     * @return Returns the URI
     */
    public Uri getOutputMediaFileURI(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     * @param type - file type
     * @return Returns the file
     */
    private static File getOutputMediaFile(int type)
    {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME +
                        " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSSS",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" +
                    timeStamp + ".jpg");
            Log.e("ImagePath", mediaFile.getPath());
            imagePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        }
        else
        {
            return null;
        }
        return mediaFile;
    }
}