// Camera Interface Screen
// Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.archaeology.ui;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.support.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.util.CheatSheet;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.archaeology.util.HistoryHelper;
import com.archaeology.R;
import eu.livotov.labs.android.camview.CameraLiveView;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.LiveDataProcessingCallback;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.StateStatic.ALL_SAMPLE_NUMBER;
import static com.archaeology.util.StateStatic.AREA_EASTING;
import static com.archaeology.util.StateStatic.AREA_NORTHING;
import static com.archaeology.util.StateStatic.CONTEXT_NUMBER;
import static com.archaeology.util.StateStatic.SAMPLE_NUMBER;
import static com.archaeology.util.StateStatic.getGlobalWebServerURL;
import static com.archaeology.util.StateStatic.setGlobalBucketURL;
public class CameraUIActivity extends AppCompatActivity
{
    HistoryHelper myDatabase;
    // anton's stuff for OCR
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";
    public static final String LANG = "eng";
    // camera view and scanner view stuff:
    private float x1;
    int flag = -1;
    private static final String TAG = "CameraUIActivity.java";
    private FloatingActionButton shutter;
    private CameraLiveView cam = null;
    private RelativeLayout parent;
    private ScannerLiveView scan = null;
    private FloatingActionButton fab;
    RequestQueue queue;
    /**
     * Activity is launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ui);
        String URL = getGlobalWebServerURL() + "/get_property/?key=bucket_url";
        queue = Volley.newRequestQueue(this);
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                setGlobalBucketURL(response);
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),
                        "Bucket URL could not be found. Go to Settings to change it.",
                        Toast.LENGTH_LONG).show();
            }
        });
        myDatabase = new HistoryHelper(this);
        // OCR-only stuff
        String[] paths = new String[]{DATA_PATH, DATA_PATH + "tessdata/"};
        for (String path: paths)
        {
            File dir = new File(path);
            if (!dir.exists())
            {
                if (!dir.mkdirs())
                {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                }
                else
                {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }
        if (!(new File(DATA_PATH + "tessdata/" + LANG + ".traineddata")).exists())
        {
            try
            {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + LANG + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + LANG + ".traineddata");
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copied " + LANG + " trained data");
            }
            catch (IOException e)
            {
                Log.e(TAG, "Was unable to copy " + LANG + " trained data " + e.toString());
            }
        }
        // setting up CameraView
        parent = (RelativeLayout) findViewById(R.id.parent);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        shutter = (FloatingActionButton) findViewById(R.id.shutter);
        fab.setImageResource(R.drawable.qr);
        shutter.setImageResource(R.drawable.camera_icon);
        createCamera();
    }

    /**
     * User pressed screen
     * @param event - touch event
     * @return Returns whether the event was handled
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > 150)
                {
                    // Left to Right swipe action
                    if (x2 < x1)
                    {
                        Intent intent = new Intent(this, FavoriteActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    }
                    // Right to left swipe action
                    else
                    {
                        Intent intent = new Intent(this, HistoryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_right,
                                R.anim.anim_slide_out_right);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Open the camera
     */
    private void createCamera()
    {
        if (scan != null)
        {
            scan.stopScanner();
        }
        parent.removeAllViews();
        cam = new CameraLiveView(this);
        cam.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        cam.setBackgroundColor(0x233069);
        parent.addView(cam);
        cam.startCamera();
        shutter.setVisibility(View.VISIBLE);
    }

    /**
     * Open a scanner
     */
    private void createScanner()
    {
        if (cam != null)
        {
            cam.stopCamera();
        }
        parent.removeAllViews();
        scan = new ScannerLiveView(this);
        scan.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        scan.setBackgroundColor(0x233069);
        parent.addView(scan);
        scan.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            /**
             * Scanner opened
             * @param scanner - file scanner
             */
            @Override
            public void onScannerStarted(ScannerLiveView scanner)
            {
            }

            /**
             * Scanner stopped
             * @param scanner - file scanner
             */
            @Override
            public void onScannerStopped(ScannerLiveView scanner)
            {
            }

            /**
             * Scanner failed
             * @param err - error
             */
            @Override
            public void onScannerError(Throwable err)
            {
            }

            /**
             * Scanned QR code
             * @param data - located data
             */
            @Override
            public void onCodeScanned(String data)
            {
                Log.v("QRCode Scanned", data);
                goToObjectDetail(data);
            }
        });
        scan.startScanner();
        shutter.setVisibility(View.GONE);
    }

    /**
     * Take a picture
     * @param v - camera view
     */
    public void takePic(View v)
    {
        cam.getController().requestLiveData(new LiveDataProcessingCallback()
        {
            /**
             * Process a frame
             * @param data - frame
             * @param width - frame width
             * @param height - frame height
             * @return Returns null
             */
            @Override
            public Object onProcessCameraFrame(byte[] data, int width, int height)
            {
                YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
                // have to write camera frame to file to see if it is rotated
                File f = new File(Environment.getExternalStorageDirectory() + "/Archaeology/");
                ExifInterface ei = null;
                if (!f.exists())
                {
                    f.mkdirs();
                }
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Archaeology/temp.jpeg");
                try
                {
                    OutputStream out = new FileOutputStream(file);
                    baos.writeTo(out);
                    out.close();
                    ei = new ExifInterface(Environment.getExternalStorageDirectory()
                            + "/Archaeology/temp.jpeg");
                    file.delete();
                    f.delete();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                byte[] jData = baos.toByteArray();
                // Convert to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(jData, 0, jData.length);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                {
                    bitmap = rotateImage(bitmap, 90);
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                {
                    bitmap = rotateImage(bitmap, 180);
                }
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                {
                    bitmap = rotateImage(bitmap, 270);
                }
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                handleImage(bitmap);
                return null;
            }

            /**
             * Process processed frame
             * @param data - frame
             */
            @Override
            public void onReceiveProcessedCameraFrame(Object data)
            {
            }
        });
    }

    /**
     * Go to ObjectDetailActivity from scanned QR code
     * @param CODE - QR code data
     */
    private void goToObjectDetail(final String CODE)
    {
        Log.v("QRCode scanned", CODE);
        final String[] KEYS = CODE.split("\\.");
        // TODO: Abstract to arbitrary data formats, not just E.N.C.S and E.N.S
        if (KEYS.length < 4)
        {
            Toast.makeText(getApplicationContext(), "Invalid QR Code " + CODE, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.v("QRCode scanned", String.valueOf((int) CODE.charAt(2)));
        Log.v("QRCode scanned", String.valueOf((int) '.'));
        Log.v("QRCode scanned", KEYS[0]);
        final Intent DETAIL_INTENT = new Intent(this, ObjectDetailActivity.class);
        DETAIL_INTENT.putExtra(AREA_EASTING, KEYS[2]);
        DETAIL_INTENT.putExtra(AREA_NORTHING, KEYS[3]);
        DETAIL_INTENT.putExtra(CONTEXT_NUMBER, KEYS[4]);
        DETAIL_INTENT.putExtra(SAMPLE_NUMBER, KEYS[5]);
        final Intent CERAMIC_INTENT = new Intent(this, CeramicInputActivity.class);
        final List<String> SAMPLE_NUMBERS = new ArrayList<>();
        // TODO: Send sample numbers to object detail
        String URL = getGlobalWebServerURL() + "/get_sample_numbers/?easting=" + KEYS[2]
                + "&northing=" + KEYS[3] + "&context=" + KEYS[4];
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper(this) {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                // convert to regular array from html link list
                ArrayList<String> eastings = CheatSheet.convertLinkListToArray(response);
                // If the easting, northing, context, or sample are not found, this will be false
                // (nothing returned at all if any of the prior 3 are not found)
                if (eastings.contains(KEYS[5]))
                {
                    SAMPLE_NUMBERS.addAll(eastings);
                    DETAIL_INTENT.putExtra(ALL_SAMPLE_NUMBER, SAMPLE_NUMBERS.toArray(new String[SAMPLE_NUMBERS.size()]));
                    startActivity(DETAIL_INTENT);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Sample " + CODE + " not found", Toast.LENGTH_LONG).show();
                    startActivity(CERAMIC_INTENT);
                }
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "Error contacting server", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Picture taken
     * @param bitmap - image
     */
    private void handleImage(Bitmap bitmap)
    {
        Log.v(TAG, "Before baseAPI");
        Bitmap toSend = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 10,
                bitmap.getHeight() / 10, false);
        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.setDebug(true);
        baseAPI.init(DATA_PATH, LANG);
        baseAPI.setImage(toSend);
        String recognizedText = baseAPI.getUTF8Text();
        baseAPI.end();
        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.
        Log.v(TAG, "OCRED TEXT: " + recognizedText);
        if (LANG.equalsIgnoreCase("eng"))
        {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", "");
        }
        recognizedText = recognizedText.trim();
        cam.stopCamera();
        cam.startCamera();
        Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
        intent.putExtra("search", recognizedText);
        intent.putExtra("preview", toSend);
        startActivity(intent);
    }

    /**
     * Rotate an image
     * @param source - image
     * @param angle - angle to rotate
     * @return Returns the rotated image
     */
    public static Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix,true);
    }

    /**
     * Switch between QR reader and OCR
     * @param view - camera view
     */
    public void crossFade(View view)
    {
        if (flag < 0)
        {
            createScanner();
            fab.setImageResource(R.drawable.ocr);
            flag *= -1;
        }
        else
        {
            createCamera();
            fab.setImageResource(R.drawable.qr);
            flag *= -1;
        }
    }

    /**
     * User pressed back
     */
    @Override
    public void onBackPressed()
    {
    }
}