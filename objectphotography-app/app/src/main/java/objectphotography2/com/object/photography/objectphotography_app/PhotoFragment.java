// Photo fragment
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MARKED_AS_ADDED;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MARKED_AS_REMOVED;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.MARKED_AS_TO_DOWNLOAD;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.SYNCED;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalWebServerURL;
public class PhotoFragment extends Fragment
{
    abstract class CustomPicassoCallback implements Callback
    {
        TaggedImageView actualImageView;
        /**
         * Get the image view
         * @return Returns the image view
         */
        public TaggedImageView getActualImageView()
        {
            return actualImageView;
        }

        /**
         * Set the image view
         * @param actualImageView - new image view
         */
        public void setActualImageView(TaggedImageView actualImageView)
        {
            this.actualImageView = actualImageView;
        }
    }
    private static final String PHOTO_DICT = "pd";
    View inflatedView;
    LinkedHashMap<Uri, String> dictOfPhotoSyncStatus;
    ArrayList<TaggedImageView> loadedPhotos;
    final PicassoWrapper picassoSingleton = new PicassoWrapper();
    final CustomPicassoCallback picassoCallback = new CustomPicassoCallback() {
        /**
         * Connection succeeded
         */
        @Override
        public void onSuccess()
        {
            photoLoadedSemaphore--;
            getActualImageView().setSyncStatus(SYNCED);
            Log.v(LOG_TAG, "photoLoadedSemaphore: " + photoLoadedSemaphore);
            if (photoLoadedSemaphore == 0)
            {
                // changed from getActivity()
                PhotoLoadDeleteInterface containerActivity
                        = (PhotoLoadDeleteInterface) getActivity();
                Log.v(LOG_TAG, "Container: " + containerActivity);
                if (containerActivity != null)
                {
                    containerActivity.setAllPhotosLoaded(true);
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
            Log.v(LOG_TAG, "photo callback error");
            Log.v(LOG_TAG, "photoLoadedSempahore: " + photoLoadedSemaphore);
        }
    };
    public int selectedPhotoCount = 0;
    public int photoLoadedSemaphore = 0;
    RequestQueue queue;
    public interface PhotoLoadDeleteInterface
    {
        /**
         * All photos loaded
         * @param isLoaded - are all photos loaded?
         */
        void setAllPhotosLoaded(boolean isLoaded);

        /**
         * Whether to delete photo
         * @param deletePhotoStatus - status of photo
         */
        void toggleDeletePhotoStatus(boolean deletePhotoStatus);
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
            // changed from getActivity()
            PhotoLoadDeleteInterface containerActivity = (PhotoLoadDeleteInterface) getActivity();
            containerActivity.toggleDeletePhotoStatus(selectedPhotoCount > 0);
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
            dictOfPhotoSyncStatus
                    = (LinkedHashMap<Uri, String>) savedInstanceState.getSerializable(PHOTO_DICT);
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
        Log.v(LOG_TAG, "SAVE FRAGMENT");
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
     * @param fileUri - photo location
     * @param syncStatus - status of sync
     */
    public void addPhoto(Uri fileUri, final String syncStatus)
    {
        dictOfPhotoSyncStatus.put(fileUri, syncStatus);
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
        for (final Map.Entry<Uri, String> dictEntry : dictOfPhotoSyncStatus.entrySet())
        {
            if (dictEntry.getValue().equals(MARKED_AS_TO_DOWNLOAD))
            {
                Log.v(LOG_TAG, "Downloading remote image: " + dictEntry.getKey());
                picassoSingleton.fetchAndInsertImage((LinearLayout) inflatedView, dictEntry.getKey(),
                        getActivity(), dictEntry.getValue(), new PhotoOnClickListener(),
                        picassoCallback);
            }
            else if(dictEntry.getValue().equals(MARKED_AS_ADDED))
            {
                picassoSingleton.fetchAndInsertImage((LinearLayout) inflatedView, dictEntry.getKey(),
                        getActivity(), dictEntry.getValue(), new PhotoOnClickListener(),
                        picassoCallback);
                final Activity parentActivity = getActivity();
                if (parentActivity instanceof ObjectDetail2Activity)
                {
                    final int areaEasting = ((ObjectDetail2Activity) parentActivity).areaEasting;
                    final int areaNorthing = ((ObjectDetail2Activity) parentActivity).areaNorthing;
                    final int contextNumber = ((ObjectDetail2Activity) parentActivity).contextNumber;
                    final int sampleNumber = ((ObjectDetail2Activity) parentActivity).sampleNumber;
                    String url = getGlobalWebServerURL() + "/upload_image_2.php?area_easting="
                            + areaEasting + "&area_northing=" + areaNorthing + "&context_number="
                            + contextNumber + "&sample_number=" + sampleNumber;
                    Log.v(LOG_TAG, "Image to be uploaded" + dictEntry.getKey());
                    AsyncHttpWrapper.makeImageUpload(url, dictEntry.getKey(), getActivity(),
                            new AsyncHttpCallbackWrapper() {
                        /**
                         * Connection succeeded
                         * @param response - HTTP response
                         */
                        @Override
                        public void onSuccessCallback(String response)
                        {
                            super.onSuccessCallback(response);
                            dictOfPhotoSyncStatus.put(dictEntry.getKey(), SYNCED);
                            Log.v(LOG_TAG, "Image New Url After Upload" + response);
                            Toast.makeText(parentActivity, "Image Uploaded To Server",
                                    Toast.LENGTH_SHORT).show();
                            ((ObjectDetail2Activity) parentActivity)
                                    .clearCurrentPhotosOnLayoutAndFetchPhotosAsync();
                        }
                    });
                }
            }
        }
    }

    /**
     * Mark photos for deletion
     */
    public void markPhotosAsDeleted()
    {
        new AlertDialog.Builder(getActivity()).setTitle("Do you want to delete photo(s)")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            /**
             * User pressed delete
             * @param dialog - alert window
             * @param which selection
             */
            public void onClick(DialogInterface dialog, int which)
            {
                for (TaggedImageView aPhoto: loadedPhotos)
                {
                    if (aPhoto.isSelected())
                    {
                        aPhoto.setSyncStatus(MARKED_AS_REMOVED);
                        aPhoto.setSelected(false);
                        Log.v(LOG_TAG, "Before delete: " + dictOfPhotoSyncStatus);
                        dictOfPhotoSyncStatus.put(aPhoto.getInformativeImageURI(),
                                MARKED_AS_REMOVED);
                        Log.v(LOG_TAG, "After delete: " + dictOfPhotoSyncStatus);
                        selectedPhotoCount--;
                        PhotoLoadDeleteInterface containerActivity
                                = (PhotoLoadDeleteInterface) getActivity();
                        containerActivity.toggleDeletePhotoStatus(selectedPhotoCount > 0);
                        aPhoto.invalidate();
                    }
                }
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            /**
             * User pressed cancel
             * @param dialog - alert window
             * @param which - selection
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // do nothing;
            }
        }).show();
    }
}