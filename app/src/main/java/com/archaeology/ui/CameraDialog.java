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

    /**
     * Create a camera alert
     * @param anActivity - calling activity
     * @return Returns the alert window
     */
    public static AlertDialog createCameraDialog(final Activity anActivity)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(anActivity);
        // Get the layout inflater
        LayoutInflater inflater = anActivity.getLayoutInflater();
        // Inflate and set the layout for the dialog. Pass null as the parent view because its
        // going in the dialog layout
        builder.setView(inflater.inflate(R.layout.remote_camera_layout, null));
        return builder.create();
    }

    /**
     * Creating approval dialog to view and approve photos
     * @param anActivity - calling activity
     * @param callback - function needing photo permissions
     */
    public static AlertDialog createPhotoApprovalDialog(final Activity anActivity,
                                                        final ApproveDialogCallback callback)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(anActivity);
        LayoutInflater inflater = anActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.approve_photo_dialog,
                null)).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            /**
             * User clicked save
             * @param dialog - the alert window
             * @param which - the selected picture
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                callback.onSaveButtonClicked();
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
                callback.onCancelButtonClicked();
            }
        });
        return builder.create();
    }

    /**
     * Building URL to connect phone with Sony camera
     * @param IP - camera IP address
     * @return Returns the URL of local connection
     */
    private static String buildAPIURLFromIP(String IP)
    {
        return "http://" + IP + ":8080/sony/camera";
    }

    /**
     * This is going to be called from ObjectDetailActivity. It should allow you to see what the
     * camera is seeing. requests are stored in a RequestQueue that is passed in as an argument
     * @param anActivity - calling activity
     * @param queue - waiting processes
     * @param id - camera id
     * @param liveViewSurface - camera live view
     */
    public static void startLiveView(final Activity anActivity, final RequestQueue queue,
                                     final int id, final SimpleStreamSurfaceView liveViewSurface)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIStartLiveViewRequest(URL, queue, id,
                    new JSONObjectResponseWrapper(anActivity) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    try
                    {
                        final String liveViewURL
                                = response.getJSONArray("result").getString(0);
                        anActivity.runOnUiThread(new Runnable() {
                            /**
                             * Run the thread
                             */
                            @Override
                            public void run()
                            {
                                // SimpleStreamSurfaceLiveView used to allow for live view from
                                // camera
                                liveViewSurface.start(liveViewURL,
                                        new SimpleStreamSurfaceView.StreamErrorListener() {
                                    /**
                                     * Camera did not launch
                                     * @param reason - failure
                                     */
                                    @Override
                                    public void onError(StreamErrorReason reason)
                                    {
                                        // break connection with camera
                                        stopLiveView(anActivity, queue, id, liveViewSurface);
                                    }
                                });
                            }
                        });
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Stops live view of camera upon request
     * @param anActivity - calling activity
     * @param queue - process queue
     * @param id - request id
     * @param liveViewSurface - camera live view
     */
    public static void stopLiveView(final Activity anActivity, RequestQueue queue, int id,
                                    final SimpleStreamSurfaceView liveViewSurface)
    {
        final String url = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIStopLiveViewRequest(url, queue, id,
                    new JSONObjectResponseWrapper(anActivity) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                    Toast.makeText(anActivity, "Live View stopped", Toast.LENGTH_SHORT).show();
                    liveViewSurface.stop();
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Take a photo
     * @param anActivity - calling activity
     * @param queue - process queue
     * @param id - process id
     * @param filename - destination file
     * @param lambdaWrapper - callback function?
     * @param liveViewSurface - camera live view
     */
    public static void takePhoto(final Activity anActivity, final RequestQueue queue, final int id,
                                 final String filename,
                                 final AfterImageSavedMethodWrapper lambdaWrapper,
                                 final SimpleStreamSurfaceView liveViewSurface)
    {
        // creating a fileURI so that image can be saved
        final Uri saveFileURI = CheatSheet.getOutputMediaFileURI(filename);
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPISetJPEGQualityToFine(URL, queue, id + 3,
                    new JSONObjectResponseWrapper(anActivity) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    try
                    {
                        VolleyWrapper.makeVolleySonyAPISetImageSizeToOriginal(URL, queue,
                                id + 5, new JSONObjectResponseWrapper(anActivity) {
                            /**
                             * Response received
                             * @param response - camera response
                             */
                            @Override
                            public void responseMethod(JSONObject response)
                            {
                                Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                                try
                                {
                                    // make request to take photo
                                    VolleyWrapper.makeVolleySonyAPITakePhotoRequest(URL, queue, id,
                                            new JSONObjectResponseWrapper(anActivity) {
                                        /**
                                         * Response received
                                         * @param response - camera response
                                         */
                                        @Override
                                        public void responseMethod(JSONObject response)
                                        {
                                            Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                                            try
                                            {
                                                // building image URL to save photo
                                                String imageURL =
                                                        response.getJSONArray("result")
                                                                .getString(0);
                                                imageURL = imageURL.substring(2,
                                                        imageURL.length() - 2);
                                                imageURL = imageURL.replace("\\",
                                                        "");
                                                Log.v(LOG_TAG_WIFI_DIRECT,
                                                        "imageURL: " + imageURL);
                                                // once you have stored the image data into a url
                                                // you can stop the live view
                                                stopLiveView(anActivity, queue, id + 23,
                                                        liveViewSurface);
                                                final ProgressDialog loadingDialog =
                                                        new ProgressDialog(anActivity);
                                                loadingDialog.setMessage("Downloading Photo From" +
                                                        " Camera");
                                                loadingDialog.setProgressStyle(
                                                        ProgressDialog.STYLE_SPINNER);
                                                loadingDialog.setIndeterminate(true);
                                                loadingDialog.show();
                                                // getting image to store as thumbnail
                                                VolleyWrapper.makeVolleyImageRequest(imageURL,
                                                        queue, new ImageResponseWrapper() {
                                                    /**
                                                     * Response received
                                                     * @param bitmap - image taken
                                                     */
                                                    @Override
                                                    public void responseMethod(Bitmap bitmap)
                                                    {
                                                        FileOutputStream tmpStream = null;
                                                        Log.v(LOG_TAG_WIFI_DIRECT,
                                                                "Image loaded");
                                                        try
                                                        {
                                                            File tmpFile =
                                                                    new File(saveFileURI
                                                                            .getPath());
                                                            // writing data from file to output
                                                            // stream to be stored into a bitmap
                                                            tmpStream = new FileOutputStream(
                                                                    tmpFile);
                                                            bitmap.compress(
                                                                    Bitmap.CompressFormat.JPEG,
                                                                    100, tmpStream);
                                                            Uri thumbnailURI =
                                                                    CheatSheet.getThumbnail(
                                                                            filename
                                                                                    + ".jpg");
                                                            loadingDialog.dismiss();
                                                            // has not been defined yet
                                                            lambdaWrapper.doStuffWithSavedImage(
                                                                    thumbnailURI);
                                                        }
                                                        catch (FileNotFoundException e)
                                                        {
                                                            e.printStackTrace();
                                                            loadingDialog.dismiss();
                                                        }
                                                        finally
                                                        {
                                                            try
                                                            {
                                                                if (tmpStream != null)
                                                                {
                                                                    tmpStream.close();
                                                                }
                                                            }
                                                            catch (IOException e)
                                                            {
                                                                e.printStackTrace();
                                                                loadingDialog.dismiss();
                                                            }
                                                        }
                                                        // after thumbnail has been loaded you can
                                                        // start the live view
                                                        startLiveView(anActivity, queue,
                                                                id + 22, liveViewSurface);
                                                    }

                                                    /**
                                                     * Connection failed
                                                     * @param error - failure
                                                     */
                                                    @Override
                                                    public void errorMethod(VolleyError error)
                                                    {
                                                        error.printStackTrace();
                                                        loadingDialog.dismiss();
                                                        startLiveView(anActivity, queue,
                                                                id + 27, liveViewSurface);
                                                    }
                                                });
                                            }
                                            catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }

                                        /**
                                         * Connection failed
                                         * @param error - failure
                                         */
                                        @Override
                                        public void errorMethod(VolleyError error)
                                        {
                                            error.printStackTrace();
                                            Log.v(LOG_TAG_WIFI_DIRECT, error.toString());
                                        }
                                    });
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            /**
                             * Connection failed
                             * @param error - failure
                             */
                            @Override
                            public void errorMethod(VolleyError error)
                            {
                                error.printStackTrace();
                            }
                        });
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * methods to zoom in and zoom out during live camera view
     * @param anActivity - calling activity
     * @param queue - process queue
     * @param id - process id
     */
    public static void zoomIn(final Activity anActivity, RequestQueue queue, int id)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("in", queue, URL, id,
                    new JSONObjectResponseWrapper(anActivity) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Zoom out
     * @param anActivity - calling activity
     * @param queue - process queue
     * @param id - process id
     */
    public static void zoomOut(final Activity anActivity, RequestQueue queue, int id)
    {
        final String URL = buildAPIURLFromIP(cameraIPAddress);
        try
        {
            VolleyWrapper.makeVolleySonyAPIActZoomRequest("out", queue, URL, id,
                    new JSONObjectResponseWrapper(anActivity) {
                /**
                 * Response received
                 * @param response - camera response
                 */
                @Override
                public void responseMethod(JSONObject response)
                {
                    Log.v(LOG_TAG_WIFI_DIRECT, response.toString());
                }

                /**
                 * Connection failed
                 * @param error - failure
                 */
                @Override
                public void errorMethod(VolleyError error)
                {
                    error.printStackTrace();
                }
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}