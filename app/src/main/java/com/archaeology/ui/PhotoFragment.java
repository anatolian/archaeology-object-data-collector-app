// Photo fragment
// @author: msenol
package com.archaeology.ui;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import java.io.File;
import java.util.LinkedList;
import com.archaeology.R;
import static com.archaeology.util.CheatSheet.getDirectory;
public class PhotoFragment extends Fragment
{
    public abstract class CustomPicassoCallback implements Callback
    {
        AppCompatImageView actualImageView;
        /**
         * Set the image view
         * @param actualImageView - new image view
         */
        public void setActualImageView(AppCompatImageView actualImageView)
        {
            this.actualImageView = actualImageView;
        }
    }
    private static final String PHOTOS = "photos";
    View inflatedView;
    LinkedList<Uri> files;
    RequestQueue queue;
    /**
     * Constructor
     */
    public PhotoFragment()
    {
    }

    /**
     * Create the view
     * @param inflater - layout maker
     * @param container - layout container
     * @param savedInstanceState - app state from memory
     * @return Returns the view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.files = new LinkedList<>();
        this.inflatedView = inflater.inflate(R.layout.photo_fragment, container, false);
        // changed from getActivity
        this.queue = Volley.newRequestQueue(getActivity());
        return inflatedView;
    }

    /**
     * Save the instance state
     * @param outState - memory object of state
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PHOTOS, files);
    }

    /**
     * Clear layout
     */
    public void clearPhotosFromLayout()
    {
        LinearLayout photoLayout = (LinearLayout) inflatedView;
        while (photoLayout.getChildCount() > 0)
        {
            AppCompatImageView img = (AppCompatImageView) photoLayout.getChildAt(0);
            ((BitmapDrawable) img.getDrawable()).getBitmap().recycle();
            photoLayout.removeViewAt(0);
        }
    }

    /**
     * Add photo
     * @param fileURI - photo location
     * @param context - calling context
     */
    public void addPhoto(Uri fileURI, Context context)
    {
        files.addFirst(fileURI);
        clearPhotosFromLayout();
        syncPhotos(context);
    }

    /**
     * Remove an image from the container
     * @param fileURI - image to remove
     * @param context - calling context
     */
    public void removePhoto(Uri fileURI, Context context)
    {
        files.remove(fileURI);
        String path = fileURI.getPath();
        new File(path).delete();
        String structure = path.substring(path.lastIndexOf("/") + 1);
        structure = structure.replaceAll("_", "/");
        structure = structure.substring(0, structure.lastIndexOf("/")) + "/photos/lab"
                + structure.substring(structure.lastIndexOf("/"));
        new File(Environment.getExternalStorageDirectory() + getDirectory() + structure).delete();
        structure = structure.substring(0, structure.lastIndexOf("/"));
        // Delete empty directories up to the top directory
        while (true)
        {
            File dir = new File(Environment.getExternalStorageDirectory() + getDirectory() + structure);
            File[] children = dir.listFiles();
            if (children == null || children.length == 0)
            {
                if (!dir.delete())
                {
                    Log.v("Deleting files ", "Error deleting file");
                    break;
                }
                else if (structure.lastIndexOf("/") <= 0)
                {
                    break;
                }
                structure = structure.substring(0, structure.lastIndexOf("/"));
            }
            else
            {
                break;
            }
        }
        clearPhotosFromLayout();
        syncPhotos(context);
    }

    /**
     * Create fragment
     * @param context - calling context
     */
    public void prepareFragmentForNewPhotosFromNewItem(Context context)
    {
        this.files = new LinkedList<>();
        this.queue = Volley.newRequestQueue(getActivity());
        clearPhotosFromLayout();
        syncPhotos(context);
    }

    /**
     * Sync the photos
     * @param context - calling context
     */
    private void syncPhotos(Context context)
    {
        for (Uri file: files)
        {
            AppCompatImageView elem = new AppCompatImageView(context);
            elem.setImageURI(file);
            ((LinearLayout) inflatedView).addView(elem);
        }
    }
}