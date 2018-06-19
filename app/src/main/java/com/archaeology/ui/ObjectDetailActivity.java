// Abstract Activity for viewing object details
// @author: Christopher Besser
package com.archaeology.ui;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.archaeology.R;
import com.archaeology.util.CheatSheet;
import com.archaeology.util.StateStatic;

import static com.archaeology.util.CheatSheet.getOutputMediaFile;
import static com.archaeology.util.CheatSheet.goToSettings;
import static com.archaeology.util.StateStatic.REQUEST_IMAGE_CAPTURE;
import static com.archaeology.util.StateStatic.cameraIPAddress;
import static com.archaeology.util.StateStatic.cameraMACAddress;
import static com.archaeology.util.StateStatic.getTimeStamp;
public abstract class ObjectDetailActivity extends AppCompatActivity
{
    protected Uri fileURI;
    protected int requestID = 55;
    protected IntentFilter mIntentFilter;
    protected RequestQueue queue;
    protected WifiP2pManager mManager;
    protected WifiP2pManager.Channel mChannel;
    /**
     * Activity is launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (StateStatic.cameraIPAddress != null)
        {
            queue = Volley.newRequestQueue(this);
            // setting up intent filter
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(this, getMainLooper(), null);
        }
        mIntentFilter = new IntentFilter();
        queue = Volley.newRequestQueue(this);
        cameraIPAddress = CheatSheet.findIPFromMAC(cameraMACAddress);
        if (cameraIPAddress != null)
        {
            ((TextView) findViewById(R.id.connectToCameraText)).setText(getString(R.string.ip_connection,
                    cameraIPAddress));
        }
    }

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
            LinearLayout container = (LinearLayout) photoFragment.inflatedView;
            // photo URIs are added to hashmap in PhotoFragment class
            photoFragment.addPhoto(imageURI, getApplicationContext());
            for (int i = 0; i < photoFragment.files.size(); i++)
            {
                AppCompatImageView img = (AppCompatImageView) container.getChildAt(i);
                Uri uri = photoFragment.files.get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete this picture?").setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    /**
                     * User confirmed delete
                     * @param dialog - alert dialog
                     * @param id - item id
                     */
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        photoFragment.removePhoto(uri, getApplicationContext());
                        dialog.cancel();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    /**
                     * User clicked cancel
                     * @param dialog - alert dialog
                     * @param id - button id
                     */
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                img.setOnLongClickListener(new View.OnLongClickListener() {
                    /**
                     * User held click on image
                     * @param view - image
                     * @return Returns whether the long click was handled
                     */
                    @Override
                    public boolean onLongClick(View view)
                    {
                        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vb.vibrate(30);
                        alert.show();
                        return true;
                    }
                });
            }
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