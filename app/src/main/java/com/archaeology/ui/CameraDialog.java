// Camera Dialog
// @author: msenol
package com.archaeology.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.archaeology.util.CheatSheet;
import com.archaeology.models.ImageResponseWrapper;
import com.archaeology.models.JSONObjectResponseWrapper;
import com.archaeology.R;
import com.archaeology.services.VolleyWrapper;
import com.archaeology.models.AfterImageSavedMethodWrapper;
import static com.archaeology.util.StateStatic.LOG_TAG_WIFI_DIRECT;
import static com.archaeology.util.StateStatic.cameraIPAddress;
public class CameraDialog
{
    // interface that will be used by camera dialogs
    interface ApproveDialogCallback
    {
        /**
         * User pressed save
         */
        void onSaveButtonClicked();

        /**
         * User pressed cancel
         */
        void onCancelButtonClicked();
    }

//    /**
//     * Create a camera alert
//     * @param AN_ACTIVITY - calling activity
//     * @return Returns the alert window
//     */
//    public static AlertDialog createCameraDialog(final Activity AN_ACTIVITY)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(AN_ACTIVITY);
//        // Get the layout inflater
//        LayoutInflater inflater = AN_ACTIVITY.getLayoutInflater();
//        // Inflate and set the layout for the dialog. Pass null as the parent view because its
//        // going in the dialog layout
//        builder.setView(inflater.inflate(R.layout.remote_camera_layout, null));
//        return builder.create();
//    }

    /**
     * Creating approval dialog to view and approve photos
     * @param AN_ACTIVITY - calling activity
     * @param CALLBACK - function needing photo permissions
     */
    public static AlertDialog createPhotoApprovalDialog(final Activity AN_ACTIVITY,
                                                        final ApproveDialogCallback CALLBACK)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AN_ACTIVITY);
        LayoutInflater inflater = AN_ACTIVITY.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.approve_photo_dialog,null))
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
            /**
             * User clicked save
             * @param dialog - the alert window
             * @param which - the selected picture
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CALLBACK.onSaveButtonClicked();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - alert window
             * @param which - selected item
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CALLBACK.onCancelButtonClicked();
            }
        });
        return builder.create();
    }

//    /**
//     * Building URL to connect phone with Sony camera
//     * @param IP - camera IP address
//     * @return Returns the URL of local connection
//     */
//    private static String buildAPIURLFromIP(String IP)
//    {
//        return "http://" + IP + ":8080/sony/camera";
//    }

//    /**
//     * This is going to be called from ObjectDetailActivity. It should allow you to see what the
//     * camera is seeing. requests are stored in a RequestQueue that is passed in as an argument
//     * @param AN_ACTIVITY - calling activity
//     * @param QUEUE - waiting processes
//     * @param ID - camera id
//     * @param LIVE_VIEW_SURFACE - camera live view
//     */
//    public static void startLiveView(final Activity AN_ACTIVITY, final RequestQueue QUEUE,
//                                     final int ID, final SimpleStreamSurfaceView LIVE_VIEW_SURFACE)
//    {
//        final String URL = buildAPIURLFromIP(cameraIPAddress);
//        try
//        {
//            VolleyWrapper.makeVolleySonyAPIStartLiveViewRequest(URL, QUEUE, ID,
//                    new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                /**
//                 * Response received
//                 * @param response - camera response
//                 */
//                @Override
//                public void responseMethod(JSONObject response)
//                {
//                    try
//                    {
//                        final String LIVE_VIEW_URL = response.getJSONArray("result").getString(0);
//                        AN_ACTIVITY.runOnUiThread(new Runnable() {
//                            /**
//                             * Run the thread
//                             */
//                            @Override
//                            public void run()
//                            {
//                                // SimpleStreamSurfaceLiveView used to allow for live view from camera
//                                LIVE_VIEW_SURFACE.start(LIVE_VIEW_URL, new SimpleStreamSurfaceView.StreamErrorListener() {
//                                    /**
//                                     * Camera did not launch
//                                     * @param reason - failure
//                                     */
//                                    @Override
//                                    public void onError(StreamErrorReason reason)
//                                    {
//                                        // break connection with camera
//                                        stopLiveView(AN_ACTIVITY, QUEUE, ID, LIVE_VIEW_SURFACE);
//                                    }
//                                });
//                            }
//                        });
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//
//                /**
//                 * Connection failed
//                 * @param error - failure
//                 */
//                @Override
//                public void errorMethod(VolleyError error)
//                {
//                    error.printStackTrace();
//                }
//            });
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Stops live view of camera upon request
//     * @param AN_ACTIVITY - calling activity
//     * @param queue - process queue
//     * @param id - request id
//     * @param LIVE_VIEW_SURFACE - camera live view
//     */
//    public static void stopLiveView(final Activity AN_ACTIVITY, RequestQueue queue, int id,
//                                    final SimpleStreamSurfaceView LIVE_VIEW_SURFACE)
//    {
//        final String URL = buildAPIURLFromIP(cameraIPAddress);
//        try
//        {
//            VolleyWrapper.makeVolleySonyAPIStopLiveViewRequest(URL, queue, id,
//                    new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                /**
//                 * Response received
//                 * @param response - camera response
//                 */
//                @Override
//                public void responseMethod(JSONObject response)
//                {
//                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
//                    Toast.makeText(AN_ACTIVITY, "Live View stopped", Toast.LENGTH_SHORT).show();
//                    LIVE_VIEW_SURFACE.stop();
//                }
//
//                /**
//                 * Connection failed
//                 * @param error - failure
//                 */
//                @Override
//                public void errorMethod(VolleyError error)
//                {
//                    error.printStackTrace();
//                }
//            });
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Take a photo
//     * @param AN_ACTIVITY - calling activity
//     * @param QUEUE - process queue
//     * @param ID - process id
//     * @param FILE_NAME - destination file
//     * @param LAMBDA_WRAPPER - callback function?
//     * @param LIVE_VIEW_SURFACE - camera live view
//     */
//    public static void takePhoto(final Activity AN_ACTIVITY, final RequestQueue QUEUE, final int ID,
//                                 final String FILE_NAME, final AfterImageSavedMethodWrapper LAMBDA_WRAPPER,
//                                 final SimpleStreamSurfaceView LIVE_VIEW_SURFACE)
//    {
//        // creating a fileURI so that image can be saved
//        final Uri SAVE_FILE_URI = CheatSheet.getOutputMediaFileURI(FILE_NAME);
//        final String URL = buildAPIURLFromIP(cameraIPAddress);
//        try
//        {
//            VolleyWrapper.makeVolleySonyAPISetJPEGQualityToFine(URL, QUEUE, ID + 3,
//                    new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                /**
//                 * Response received
//                 * @param response - camera response
//                 */
//                @Override
//                public void responseMethod(JSONObject response)
//                {
//                    try
//                    {
//                        VolleyWrapper.makeVolleySonyAPISetImageSizeToOriginal(URL, QUEUE,
//                                ID + 5, new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                            /**
//                             * Response received
//                             * @param response - camera response
//                             */
//                            @Override
//                            public void responseMethod(JSONObject response)
//                            {
//                                Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
//                                try
//                                {
//                                    // make request to take photo
//                                    VolleyWrapper.makeVolleySonyAPITakePhotoRequest(URL, QUEUE, ID,
//                                            new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                                        /**
//                                         * Response received
//                                         * @param response - camera response
//                                         */
//                                        @Override
//                                        public void responseMethod(JSONObject response)
//                                        {
//                                            Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
//                                            try
//                                            {
//                                                // building image URL to save photo
//                                                String imageURL = response.getJSONArray("result").getString(0);
//                                                imageURL = imageURL.substring(2, imageURL.length() - 2);
//                                                imageURL = imageURL.replace("\\","");
//                                                Log.v(LOG_TAG_WIFI_DIRECT,"imageURL: " + imageURL);
//                                                // once you have stored the image data into a url
//                                                // you can stop the live view
//                                                stopLiveView(AN_ACTIVITY, QUEUE, ID + 23, LIVE_VIEW_SURFACE);
//                                                final ProgressDialog LOADING_DIALOG = new ProgressDialog(AN_ACTIVITY);
//                                                LOADING_DIALOG.setMessage("Downloading Photo From" + " Camera");
//                                                LOADING_DIALOG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                                                LOADING_DIALOG.setIndeterminate(true);
//                                                LOADING_DIALOG.show();
//                                                // getting image to store as thumbnail
//                                                VolleyWrapper.makeVolleyImageRequest(imageURL, QUEUE,
//                                                        new ImageResponseWrapper() {
//                                                    /**
//                                                     * Response received
//                                                     * @param bitmap - image taken
//                                                     */
//                                                    @Override
//                                                    public void responseMethod(Bitmap bitmap)
//                                                    {
//                                                        FileOutputStream tmpStream = null;
//                                                        Log.v(LOG_TAG_WIFI_DIRECT,
//                                                                "Image loaded");
//                                                        try
//                                                        {
//                                                            File tmpFile = new File(SAVE_FILE_URI.getPath());
//                                                            // writing data from file to output
//                                                            // stream to be stored into a bitmap
//                                                            tmpStream = new FileOutputStream(tmpFile);
//                                                            bitmap.compress(Bitmap.CompressFormat.JPEG,
//                                                                    100, tmpStream);
//                                                            Uri thumbnailURI = CheatSheet.getThumbnail(
//                                                                    FILE_NAME + ".jpg");
//                                                            LOADING_DIALOG.dismiss();
//                                                            // has not been defined yet
//                                                            LAMBDA_WRAPPER.doStuffWithSavedImage(thumbnailURI);
//                                                        }
//                                                        catch (FileNotFoundException e)
//                                                        {
//                                                            e.printStackTrace();
//                                                            LOADING_DIALOG.dismiss();
//                                                        }
//                                                        finally
//                                                        {
//                                                            try
//                                                            {
//                                                                if (tmpStream != null)
//                                                                {
//                                                                    tmpStream.close();
//                                                                }
//                                                            }
//                                                            catch (IOException e)
//                                                            {
//                                                                e.printStackTrace();
//                                                                LOADING_DIALOG.dismiss();
//                                                            }
//                                                        }
//                                                        // after thumbnail has been loaded you can
//                                                        // start the live view
//                                                        startLiveView(AN_ACTIVITY, QUEUE,ID + 22, LIVE_VIEW_SURFACE);
//                                                    }
//
//                                                    /**
//                                                     * Connection failed
//                                                     * @param error - failure
//                                                     */
//                                                    @Override
//                                                    public void errorMethod(VolleyError error)
//                                                    {
//                                                        error.printStackTrace();
//                                                        LOADING_DIALOG.dismiss();
//                                                        startLiveView(AN_ACTIVITY, QUEUE,ID + 27, LIVE_VIEW_SURFACE);
//                                                    }
//                                                });
//                                            }
//                                            catch (JSONException e)
//                                            {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                        /**
//                                         * Connection failed
//                                         * @param error - failure
//                                         */
//                                        @Override
//                                        public void errorMethod(VolleyError error)
//                                        {
//                                            error.printStackTrace();
//                                            Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
//                                        }
//                                    });
//                                }
//                                catch (JSONException e)
//                                {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            /**
//                             * Connection failed
//                             * @param error - failure
//                             */
//                            @Override
//                            public void errorMethod(VolleyError error)
//                            {
//                                error.printStackTrace();
//                            }
//                        });
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//
//                /**
//                 * Connection failed
//                 * @param error - failure
//                 */
//                @Override
//                public void errorMethod(VolleyError error)
//                {
//                    error.printStackTrace();
//                }
//            });
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Methods to zoom in and zoom out during live camera view
//     * @param AN_ACTIVITY - calling activity
//     * @param queue - process queue
//     * @param id - process id
//     */
//    public static void zoomIn(final Activity AN_ACTIVITY, RequestQueue queue, int id)
//    {
//        final String URL = buildAPIURLFromIP(cameraIPAddress);
//        try
//        {
//            VolleyWrapper.makeVolleySonyAPIActZoomRequest("in", queue, URL, id,
//                    new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                /**
//                 * Response received
//                 * @param response - camera response
//                 */
//                @Override
//                public void responseMethod(JSONObject response)
//                {
//                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
//                }
//
//                /**
//                 * Connection failed
//                 * @param error - failure
//                 */
//                @Override
//                public void errorMethod(VolleyError error)
//                {
//                    error.printStackTrace();
//                }
//            });
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Zoom out
//     * @param AN_ACTIVITY - calling activity
//     * @param queue - process queue
//     * @param id - process id
//     */
//    public static void zoomOut(final Activity AN_ACTIVITY, RequestQueue queue, int id)
//    {
//        final String URL = buildAPIURLFromIP(cameraIPAddress);
//        try
//        {
//            VolleyWrapper.makeVolleySonyAPIActZoomRequest("out", queue, URL, id,
//                    new JSONObjectResponseWrapper(AN_ACTIVITY) {
//                /**
//                 * Response received
//                 * @param response - camera response
//                 */
//                @Override
//                public void responseMethod(JSONObject response)
//                {
//                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
//                }
//
//                /**
//                 * Connection failed
//                 * @param error - failure
//                 */
//                @Override
//                public void errorMethod(VolleyError error)
//                {
//                    error.printStackTrace();
//                }
//            });
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }
}