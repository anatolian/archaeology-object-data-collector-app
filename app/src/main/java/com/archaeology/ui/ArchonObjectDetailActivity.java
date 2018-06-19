// Object detail screen for the Floridians
// @author: Christopher Besser
package com.archaeology.ui;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.archaeology.R;
import com.archaeology.util.CheatSheet;
import java.io.File;
import java.io.IOException;
import static com.archaeology.util.CheatSheet.rotateImageIfRequired;
import static com.archaeology.util.StateStatic.REQUEST_IMAGE_CAPTURE;
import static com.archaeology.util.StateStatic.REQUEST_REMOTE_IMAGE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.cameraMACAddress;
import static com.archaeology.util.StateStatic.colorCorrectionEnabled;
import static com.archaeology.util.StateStatic.selectedCameraName;
import static com.archaeology.util.StateStatic.selectedCameraPosition;
public class ArchonObjectDetailActivity extends ObjectDetailActivity
{
    public int archon, find;
    private EditText mArchonField, mFindField;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archon_object_detail);
        mArchonField = findViewById(R.id.archonField);
        mFindField = findViewById(R.id.findNumberField);
    }

    /**
     * Handle intent result
     * @param requestCode - activity request code
     * @param resultCode - activity result code
     * @param data - activity returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK)
        {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.approve_photo_dialog,null));
        // set up camera dialog
        AlertDialog approveDialog = builder.create();
        approveDialog.show();
        // view photo you are trying to approve
        MagnifyingGlass approvePhotoImage = approveDialog.findViewById(R.id.approvePhotoImage);
        Uri thumbnailURI;
        Bitmap bmp;
        String captureFile;
        // Local camera request
        if (requestCode == REQUEST_IMAGE_CAPTURE)
        {
            if (fileURI == null)
            {
                return;
            }
            captureFile = fileURI.toString();
            String originalFileName = captureFile.substring(captureFile.lastIndexOf('/') + 1);
            fileURI = Uri.parse(Environment.getExternalStorageDirectory() + "/FloridaArchaeology/" + originalFileName);
            // creating URI to save photo to once taken
            thumbnailURI = CheatSheet.getThumbnail(originalFileName);
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), thumbnailURI);
                bmp = rotateImageIfRequired(bitmap, getApplicationContext(), thumbnailURI);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        // Remote camera request
        else
        {
            // Returned URI from RemoteCameraActivity is a thumbnail
            thumbnailURI = data.getData();
            fileURI = Uri.parse(Environment.getExternalStorageDirectory() + "/FloridaArchaeology/temp.jpg");
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), thumbnailURI);
                bmp = rotateImageIfRequired(bitmap, getApplicationContext(), thumbnailURI);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        approvePhotoImage.setImageBitmap(bmp);
        approvePhotoImage.correctedAlready = true;
        Button OKButton = approveDialog.findViewById(R.id.saveButton);
        TextView label = approveDialog.findViewById(R.id.correctionLabel);
        label.setVisibility(View.INVISIBLE);
        approveDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            /**
             * User dismissed dialog
             * @param f - interface
             */
            public void onDismiss(DialogInterface f)
            {
                new File(fileURI.getPath()).delete();
                new File(thumbnailURI.getPath()).delete();
            }
        });
        OKButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User pressed save
             * @param view - the save button
             */
            @Override
            public void onClick(View view)
            {
                String dirPath = Environment.getExternalStorageDirectory() + "/FloridaArchaeology/"
                        + archon + "/" + find + "/";
                String thumbDirPath = Environment.getExternalStorageDirectory() + "/FloridaThumbnails/";
                File folder = new File(dirPath);
                File thumbFolder = new File(thumbDirPath);
                if (!folder.exists())
                {
                    folder.mkdirs();
                }
                if (!thumbFolder.exists())
                {
                    thumbFolder.mkdirs();
                }
                String thumbName = archon + "_" + find;
                int photoNum = 1;
                for (File child: thumbFolder.listFiles())
                {
                    String name = child.getName();
                    if (!name.startsWith(thumbName))
                    {
                        continue;
                    }
                    try
                    {
                        int num = Integer.parseInt(name.substring(name.lastIndexOf("_") + 1, name.indexOf(".")));
                        if (num >= photoNum)
                        {
                            photoNum = num + 1;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        // do nothing
                    }
                }
                String path = dirPath + photoNum + ".JPG";
                String thumbPath = thumbDirPath + thumbName + "_" + photoNum + ".JPG";
                File thumbFile = new File(thumbPath);
                new File(fileURI.getPath()).renameTo(new File(path));
                new File(thumbnailURI.getPath()).renameTo(thumbFile);
                loadPhotoIntoPhotoFragment(Uri.fromFile(thumbFile));
                approveDialog.dismiss();
            }
        });
        Button cancelButton = approveDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User cancelled image upload
             * @param view - the cancel button
             */
            @Override
            public void onClick(View view)
            {
                new File(fileURI.getPath()).delete();
                new File(thumbnailURI.getPath()).delete();
                approveDialog.dismiss();
            }
        });
        if (!colorCorrectionEnabled)
        {
            OKButton.performClick();
        }
    }

    /**
     * Get images from response and store so that they can be viewed later
     */
    @Override
    public void asyncPopulatePhotos()
    {
        File folder = new File(Environment.getExternalStorageDirectory() + "/FloridaThumbnails/");
        if (!folder.exists())
        {
            return;
        }
        String prefix = archon + "_" + find;
        for (File child: folder.listFiles())
        {
            String name = child.getName();
            if (name.startsWith(prefix))
            {
                loadPhotoIntoPhotoFragment(Uri.fromFile(child));
            }
        }
    }

    /**
     * Go to remote controlling camera
     */
    @Override
    public void goToRemoteCameraActivity()
    {
        TextView loading = findViewById(R.id.connectingToCamera);
        loading.setVisibility(View.VISIBLE);
        if (cameraIPAddress == null)
        {
            Toast.makeText(getApplicationContext(), "Not connected to camera", Toast.LENGTH_LONG).show();
            loading.setVisibility(View.INVISIBLE);
            return;
        }
        RemoteSonyCameraActivity activity = RemoteSonyCameraActivityFactory.getRemoteSonyCameraActivity(selectedCameraName);
        Intent wifiActivity = new Intent(this, activity.getClass());
        startActivityForResult(wifiActivity, REQUEST_REMOTE_IMAGE);
    }

    /**
     * Called from add photo button. shows remoteCameraDialog, which is used to open camera view
     * and take picture
     * @param view - add photo button
     */
    public void addPhotoAction(View view)
    {
        try
        {
            archon = Integer.parseInt(mArchonField.getText().toString());
            find = Integer.parseInt(mFindField.getText().toString());
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(getApplicationContext(), "Archon and Find must be integers", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCameraPosition == 0)
        {
            startLocalCameraIntent();
        }
        else
        {
            // Just connect to found IP
            cameraIPAddress = CheatSheet.findIPFromMAC(cameraMACAddress);
            goToRemoteCameraActivity();
        }
    }
}
