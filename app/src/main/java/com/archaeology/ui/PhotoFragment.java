// Photo fragment
// @author: msenol
package com.archaeology.ui;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import java.util.ArrayList;
import java.util.LinkedList;
import com.archaeology.R;
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
    ArrayList<AppCompatImageView> loadedPhotos;
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
        this.loadedPhotos = new ArrayList<>();
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
    private void clearPhotosFromLayout()
    {
        LinearLayout photoLayout = (LinearLayout) inflatedView;
        photoLayout.removeAllViews();
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
     * Create fragment
     * @param context - calling context
     */
    public void prepareFragmentForNewPhotosFromNewItem(Context context)
    {
        this.files = new LinkedList<>();
        this.loadedPhotos = new ArrayList<>();
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
            ImageView elem = new ImageView(context);
            elem.setImageURI(file);
            ((LinearLayout) inflatedView).addView(elem);
        }
    }
}