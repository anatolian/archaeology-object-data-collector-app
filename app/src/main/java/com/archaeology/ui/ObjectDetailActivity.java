// Abstract Activity for viewing object details
// @author: Christopher Besser
package com.archaeology.ui;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.archaeology.R;
import com.archaeology.util.CheatSheet;
import static com.archaeology.util.CheatSheet.getOutputMediaFile;
import static com.archaeology.util.CheatSheet.goToSettings;
import static com.archaeology.util.StateStatic.REQUEST_IMAGE_CAPTURE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.cameraMACAddress;
import static com.archaeology.util.StateStatic.getTimeStamp;
import static com.archaeology.util.StateStatic.selectedCameraPosition;
public abstract class ObjectDetailActivity extends AppCompatActivity
{
    protected Uri fileURI;
    /**
     * Populate action overflow
     * @param menu - overflow actions
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_object_detail, menu);
        return true;
    }

    /**
     * Overflow action selected
     * @param item - selected action
     * @return Returns whether the action succeeded
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                goToSettings(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Add a photo
     * @param view - add photo button
     */
    public abstract void addPhotoAction(View view);

    /**
     * Starts the local camera
     */
    public void startLocalCameraIntent()
    {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String stamp = getTimeStamp();
        // create a file to save the image
        Context context = getApplicationContext();
        fileURI = FileProvider.getUriForFile(context, context.getPackageName()
                + ".my.package.name.provider", getOutputMediaFile(stamp));
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photoIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Fill photo fragment
     * @param imageURI - image location
     */
    public void loadPhotoIntoPhotoFragment(Uri imageURI)
    {
        // loading PhotoFragment class to add photo URIs
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        if (photoFragment != null)
        {
            // photo URIs are added to hashmap in PhotoFragment class
            photoFragment.addPhoto(imageURI, getApplicationContext());
        }
    }

    /**
     * Clear current photos and populate with photos from database
     */
    public void clearCurrentPhotosOnLayoutAndFetchPhotosAsync()
    {
        PhotoFragment photoFragment = (PhotoFragment) getFragmentManager().findFragmentById(R.id.fragment);
        photoFragment.prepareFragmentForNewPhotosFromNewItem(getApplicationContext());
        asyncPopulatePhotos();
    }

    /**
     * Go to the remote camera
     */
    public abstract void goToRemoteCameraActivity();

    /**
     * Populate image views
     */
    public abstract void asyncPopulatePhotos();

    /**
     * Handle activity result
     * @param requestCode - activity request code
     * @param resultCode - activity result code
     * @param data - activity returned data
     */
    @Override
    protected abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}