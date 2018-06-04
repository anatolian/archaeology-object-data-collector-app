// Photo fragment
// @author: msenol
package com.archaeology.ui;
import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.archaeology.services.PicassoWrapper;
import com.archaeology.R;
import com.archaeology.services.AsyncHTTPCallbackWrapper;
import com.archaeology.services.AsyncHerokuHTTPWrapper;
import com.archaeology.util.CheatSheet;
import static com.archaeology.util.StateStatic.MARKED_AS_ADDED;
import static com.archaeology.util.StateStatic.MARKED_AS_TO_DOWNLOAD;
import static com.archaeology.util.StateStatic.SYNCED;
import static com.archaeology.util.StateStatic.globalWebServerURL;
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
    private static final String PHOTO_DICT = "pd";
    View inflatedView;
    LinkedHashMap<Uri, String> dictOfPhotoSyncStatus;
    ArrayList<AppCompatImageView> loadedPhotos;
    final PicassoWrapper PICASSO_SINGLETON = new PicassoWrapper();
    final CustomPicassoCallback PICASSO_CALLBACK = new CustomPicassoCallback() {
        /**
         * Connection succeeded
         */
        @Override
        public void onSuccess()
        {
            photoLoadedSemaphore--;
            if (photoLoadedSemaphore == 0)
            {
                // changed from getActivity()
                PhotoLoadDeleteInterface containerActivity = (PhotoLoadDeleteInterface) getActivity();
                if (containerActivity != null)
                {
                    containerActivity.setAllPhotosLoaded();
                }
            }
        }

        /**
         * Connection failed
         */
        @Override
        public void onError()
        {
            photoLoadedSemaphore++;
        }
    };
    public int selectedPhotoCount = 0;
    public int photoLoadedSemaphore = 0;
    RequestQueue queue;
    public interface PhotoLoadDeleteInterface
    {
        /**
         * All photos loaded
         */
        void setAllPhotosLoaded();
    }
    public class PhotoOnClickListener implements View.OnClickListener
    {
        /**
         * User pressed photo
         * @param v - photo view
         */
        @Override
        public void onClick(View v)
        {
            ImageView tempImageView = (ImageView) v;
            if (tempImageView.isSelected())
            {
                tempImageView.setSelected(false);
                selectedPhotoCount--;
            }
            else
            {
                selectedPhotoCount++;
                tempImageView.setSelected(true);
            }
        }
    }

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
        this.dictOfPhotoSyncStatus = new LinkedHashMap<>();
        this.loadedPhotos = new ArrayList<>();
        this.inflatedView = inflater.inflate(R.layout.photo_fragment, container, false);
        // changed from getActivity
        this.queue = Volley.newRequestQueue(getActivity());
        return inflatedView;
    }

    /**
     * Attach the fragment
     * @param activity - activity to attach to
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    /**
     * Activity created
     * @param savedInstanceState - state from memory
     */
    @SuppressWarnings("unchecked")
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
        {
            dictOfPhotoSyncStatus = (LinkedHashMap<Uri, String>) savedInstanceState.getSerializable(PHOTO_DICT);
            syncPhotos();
        }
    }

    /**
     * Save the instance state
     * @param outState - memory object of state
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PHOTO_DICT, dictOfPhotoSyncStatus);
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
     * @param SYNC_STATUS - status of sync
     */
    public void addPhoto(Uri fileURI, final String SYNC_STATUS)
    {
        dictOfPhotoSyncStatus.put(fileURI, SYNC_STATUS);
        clearPhotosFromLayout();
        syncPhotos();
    }

    /**
     * Create fragment
     */
    public void prepareFragmentForNewPhotosFromNewItem()
    {
        CheatSheet.clearThePhotosDirectory();
        this.dictOfPhotoSyncStatus = new LinkedHashMap<>();
        this.loadedPhotos = new ArrayList<>();
        this.queue = Volley.newRequestQueue(getActivity());
        clearPhotosFromLayout();
        syncPhotos();
    }

    /**
     * Sync the photos
     */
    private void syncPhotos()
    {
        for (final Map.Entry<Uri, String> DICT_ENTRY: dictOfPhotoSyncStatus.entrySet())
        {
            if (DICT_ENTRY.getValue().equals(MARKED_AS_TO_DOWNLOAD))
            {
                PICASSO_SINGLETON.fetchAndInsertImage((LinearLayout) inflatedView, DICT_ENTRY.getKey(),
                        getActivity(), new PhotoOnClickListener(), PICASSO_CALLBACK);
            }
            else if (DICT_ENTRY.getValue().equals(MARKED_AS_ADDED))
            {
                PICASSO_SINGLETON.fetchAndInsertImage((LinearLayout) inflatedView, DICT_ENTRY.getKey(),
                        getActivity(), new PhotoOnClickListener(), PICASSO_CALLBACK);
                final Activity PARENT_ACTIVITY = getActivity();
                if (PARENT_ACTIVITY instanceof ObjectDetailActivity)
                {
                    String hemisphere = ((ObjectDetailActivity) PARENT_ACTIVITY).hemisphere;
                    int zone = ((ObjectDetailActivity) PARENT_ACTIVITY).zone;
                    int easting = ((ObjectDetailActivity) PARENT_ACTIVITY).easting;
                    int northing = ((ObjectDetailActivity) PARENT_ACTIVITY).northing;
                    int findNumber = ((ObjectDetailActivity) PARENT_ACTIVITY).findNumber;
                    AsyncHerokuHTTPWrapper.makeImageUpload(globalWebServerURL + "/upload_file",
                            DICT_ENTRY.getKey(), hemisphere, "" + zone, "" + easting,
                            "" + northing, "" + findNumber, new AsyncHTTPCallbackWrapper() {
                        /**
                         * Connection succeeded
                         * @param response - HTTP response
                         */
                        @Override
                        public void onSuccessCallback(String response)
                        {
                            super.onSuccessCallback(response);
                            dictOfPhotoSyncStatus.put(DICT_ENTRY.getKey(), SYNCED);
                            if (response.contains("https://") && !response.contains("form method"))
                            {
                                Toast.makeText(PARENT_ACTIVITY, "Image Uploaded To Server", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(PARENT_ACTIVITY, "Upload failed", Toast.LENGTH_SHORT).show();
                            }
                            ((ObjectDetailActivity) PARENT_ACTIVITY).clearCurrentPhotosOnLayoutAndFetchPhotosAsync();
                        }
                    });
                }
            }
        }
    }
}