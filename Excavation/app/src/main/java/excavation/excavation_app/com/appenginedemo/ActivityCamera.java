// Camera for updating one image
package excavation.excavation_app.com.appenginedemo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import excavation.excavation_app.module.common.application.ApplicationHandler;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.context.AddSinglePhotoTask;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.appenginedemo.R;
public class ActivityCamera extends ActivityBase
{
    LayoutInflater inflater;
    RelativeLayout rLayout;
    ImageView cameraImage;
    String imagePath, photoId;
    String north, east, img, nxt, act3D, pic;
    BaseTask task;
    ArrayList<String> ctxNo;
    private static int CAMERA_CAPTURE = 999;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        rLayout = (RelativeLayout) inflater.inflate(R.layout.activity_camera,null);
        wrapper.addView(rLayout);
        cameraImage = (ImageView) findViewById(R.id.camera_image);
        if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
                || getIntent().hasExtra("imagePath") || getIntent().hasExtra("ctx"))
        {
            north = getIntent().getExtras().getString("north");
            east = getIntent().getExtras().getString("east");
            img = getIntent().getExtras().getString("imagePath");
            ctxNo= getIntent().getExtras().getStringArrayList("ctx");
        }
        if (getIntent().hasExtra("3d"))
        {
            act3D = getIntent().getExtras().getString("3d");
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
            photoId = getIntent().getExtras().getString("photo_id");
        }
        try
        {
            // we will handle the returned data in onActivityResult
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_CAPTURE);
        }
        catch (ActivityNotFoundException anfe)
        {
            // display an error message
            Toast toast = Toast.makeText(ActivityCamera.this,
                    "Whoops - your device doesn't support capturing images!",
                    Toast.LENGTH_SHORT);
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                    null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            ApplicationHandler appHand = ApplicationHandler.getInstance();
            cameraImage.setImageBitmap(appHand.decodeFile(new File(imagePath)));
            cursor.close();
        }
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            cameraImage.setImageBitmap(thumbnail);
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
            task = new AddSinglePhotoTask(ActivityCamera.this, north, east, imagePath,
                    AppConstants.tempContextNo, photoId);
            task.execute();
        }
    }
}