// Take a picture
// @author: anatolian
package surveyphotography.archaeology.org.survey;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
public class TakePhotographActivity extends Activity
{
    private Uri fileURI;
    private String photoSavePath = "";
    public static int PHOTO_CODE = 100;
    /**
     * Activity launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String suYearTxt = getIntent().getStringExtra(MainActivity.SU_YEAR);
        String suSeqNumTxt = getIntent().getStringExtra(MainActivity.SU_SEQ_NUM);
        String fieldPhotoNumberTxt = getIntent().getStringExtra(MainActivity.FIELD_PHOTO_NUMBER);
        String fieldOrBag = getIntent().getStringExtra(MainActivity.FIELD_OR_BAG);
        photoSavePath = getIntent().getStringExtra((MainActivity.PHOTO_SAVE_PATH));
        getIntent().putExtra("result", false);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        if (suYearTxt == null || suYearTxt.length() == 0)
        {
            textView.setText(getString(R.string.wrong_year));
            setContentView(textView);
            return;
        }
        if (Integer.parseInt(suYearTxt) < 2005 || Integer.parseInt(suYearTxt) > 2050)
        {
            textView.setText(getString(R.string.wrong_year));
            setContentView(textView);
            return;
        }
        if (suSeqNumTxt == null || suSeqNumTxt.length() == 0)
        {
            textView.setText(getString(R.string.wrong_su));
            setContentView(textView);
            return;
        }
        if (Integer.parseInt(suSeqNumTxt) < 1 || Integer.parseInt(suSeqNumTxt) > 9999)
        {
            textView.setText(getString(R.string.wrong_su));
            setContentView(textView);
            return;
        }
        if (fieldPhotoNumberTxt == null || fieldPhotoNumberTxt.length() == 0)
        {
            textView.setText(getString(R.string.wrong_field_photo_number));
            setContentView(textView);
            return;
        }
        if (Integer.parseInt(fieldPhotoNumberTxt) < 1 || Integer.parseInt(fieldPhotoNumberTxt) > 50)
        {
            textView.setText(getString(R.string.wrong_field_photo_number));
            setContentView(textView);
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a file to save the video
        fileURI = getOutputMediaFileURI(fieldOrBag);
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        // start the image capture Intent
        startActivityForResult(intent, PHOTO_CODE);
    }

    /**
     * User selected action
     * @param item - action selected
     * @return Returns whether the action was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            // This ID represents the Home or Up button. In the case of this activity, the Up
            // button is shown. Use NavUtils to allow users to navigate up one level in the
            // application structure. For more details, see the Navigation pattern on Android
            // Design: http://developer.android.com/design/patterns/navigation.html#up-vs-back
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Activity finished
     * @param requestCode - result request code
     * @param resultCode - result code
     * @param data - result
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PHOTO_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" + fileURI.getPath(),
                        Toast.LENGTH_LONG).show();
                this.setResult(RESULT_OK);
            }
            else if (resultCode == RESULT_CANCELED)
            {
                this.setResult(RESULT_CANCELED);
                // User cancelled the image capture
            }
            else
            {
                // Image capture failed, advise user
                this.setResult(RESULT_CANCELED);
            }
        }
        finish();
    }

    /**
     * Create a file Uri for saving an image or video
     * @param fieldOrBag - name of file
     * @return Returns the URI
     */
    private Uri getOutputMediaFileURI(String fieldOrBag)
    {
        File path = getOutputMediaFile(fieldOrBag);
        if (path == null)
        {
            Toast.makeText(this, "Specified directory not writable. Using default " +
                            "directory.",
                    Toast.LENGTH_LONG).show();
            photoSavePath = MainActivity.DEFAULT_SAVE_PATH;
            path = getOutputMediaFile(fieldOrBag);
        }
        return Uri.fromFile(path);
    }

    /**
     * Create a File for saving an image or video
     * @param fieldOrBag - of file
     * @return Returns the file
     */
    private File getOutputMediaFile(String fieldOrBag)
    {
        String suYear = getIntent().getStringExtra(MainActivity.SU_YEAR);
        String suSeqNum = getIntent().getStringExtra(MainActivity.SU_SEQ_NUM);
        String fieldPhotoNumber = getIntent().getStringExtra(MainActivity.FIELD_PHOTO_NUMBER);
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                .getPath() + photoSavePath + suYear + "/" + suSeqNum + "/fld");
        File thumbMediaStorageDir = new File(Environment.getExternalStorageDirectory()
                .getPath() + photoSavePath + "tmb/" + suYear + "/" + suSeqNum + "/fld");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        if (!thumbMediaStorageDir.exists())
        {
            if (!thumbMediaStorageDir.mkdirs())
            {
                Log.d("MyCameraApp", "failed to create thumb directory");
                return null;
            }
        }
        // Create a media file name
        if (fieldOrBag.equals(MainActivity.FIELD))
        {
            return new File(mediaStorageDir.getPath() + File.separator + "1_pic_"
                    + fieldPhotoNumber + ".jpg");
        }
        return new File(mediaStorageDir.getPath() + File.separator + "1_bag_1.jpg");
    }

    /**
     * Dummy function to make AndroidStudio stop complaining
     * @param v - useless
     */
    public void takeFieldPhoto(View v)
    {
    }

    /**
     * Dummy function to make AndroidStudio stop complaining
     * @param v - useless
     */
    public void takeBagPhoto(View v)
    {
    }
}