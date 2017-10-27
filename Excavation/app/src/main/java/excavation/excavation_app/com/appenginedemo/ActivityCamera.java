// Camera
// @author: anatolian
package excavation.excavation_app.com.appenginedemo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import excavation.excavation_app.module.common.application.ApplicationHandler;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.context.addSinglePhotoTask;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.appenginedemo.R;
public class ActivityCamera extends ActivityBase
{
    LayoutInflater inflater;
    RelativeLayout rlayout;
    ImageView camera_image;
    String imagePath, photo_id;
    String north, east, img, nxt, act3d,pic;
    BaseTask task;
    ArrayList<String> ctx_no;
    private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        rlayout = (RelativeLayout) inflater.inflate(R.layout.activity_camera,null);
        wrapper.addView(rlayout);
        camera_image = (ImageView) findViewById(R.id.cemera_image);
        if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
                || getIntent().hasExtra("imagePath") || getIntent().hasExtra("ctx"))
        {
            north = getIntent().getExtras().getString("north");
            east = getIntent().getExtras().getString("east");
            img = getIntent().getExtras().getString("imagePath");
            ctx_no= getIntent().getExtras().getStringArrayList("ctx");
        }
        if (getIntent().hasExtra("3d"))
        {
            act3d = getIntent().getExtras().getString("3d");
        }
        if (getIntent().hasExtra("pic"))
        {
            pic = getIntent().getExtras().getString("pic");
        }
        if (getIntent().hasExtra("next"))
        {
            nxt = getIntent().getExtras().getString("next");
        }
        if (getIntent().hasExtra("photo_id"))
        {
            photo_id = getIntent().getExtras().getString("photo_id");
        }
        try
        {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        }
        catch (ActivityNotFoundException anfe)
        {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(ActivityCamera.this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            ActivityCamera.this.finish();
        }
    }

    /**
     * Release activity
     */
    public void release()
    {
    }

    /**
     * Activity finished
     * @param requestCode - result request code
     * @param resultCode - Result code
     * @param data - returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                    null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            ApplicationHandler apphand = ApplicationHandler.getInstance();
            camera_image.setImageBitmap(apphand.decodeFile(new File(imagePath)));
            cursor.close();
        }
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            camera_image.setImageBitmap(thumbnail);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "image.jpg");
            try
            {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            imagePath = Environment.getExternalStorageDirectory() + File.separator + "image.jpg";
            task = new addSinglePhotoTask(ActivityCamera.this, north, east, imagePath,
                    AppConstants.temp_Context_No, photo_id, pic);
            task.execute();
        }
    }
}