// Camera Interface Screen
// Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.archaeology.ui;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.archaeology.models.StringObjectResponseWrapper;
import com.archaeology.util.CheatSheet;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.archaeology.R;
import eu.livotov.labs.android.camview.CameraLiveView;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.LiveDataProcessingCallback;
import static com.archaeology.services.VolleyStringWrapper.makeVolleyStringObjectRequest;
import static com.archaeology.util.StateStatic.ALL_FIND_NUMBER;
import static com.archaeology.util.StateStatic.EASTING;
import static com.archaeology.util.StateStatic.NORTHING;
import static com.archaeology.util.StateStatic.FIND_NUMBER;
import static com.archaeology.util.StateStatic.globalBucketURL;
import static com.archaeology.util.StateStatic.globalWebServerURL;
public class CameraUIActivity extends AppCompatActivity
{
    // Assets for OCR
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
        String URL = globalWebServerURL + "/get_property/?key=bucket_url";
        queue = Volley.newRequestQueue(this);
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Response received
             * @param response - database response
             */
            @Override
            public void responseMethod(String response)
            {
                globalBucketURL = response;
            }

            /**
             * Connection failed
             * @param error - failure
             */
            @Override
            public void errorMethod(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "Bucket URL could not be found. Go to Settings to change it.",
                        Toast.LENGTH_LONG).show();
            }
        });
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
        parent = findViewById(R.id.parent);
        fab = findViewById(R.id.fab);
        shutter = findViewById(R.id.shutter);
        fab.setImageResource(R.drawable.qr);
        FloatingActionButton input = findViewById(R.id.input);
        input.setImageResource(R.drawable.check_form);
        shutter.setImageResource(R.drawable.camera_icon);
        createCamera();
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
        goToObjectDetail("");
//        cam.getController().requestLiveData(new LiveDataProcessingCallback()
//        {
//            /**
//             * Process a frame
//             * @param data - frame
//             * @param width - frame width
//             * @param height - frame height
//             * @return Returns null
//             */
//            @Override
//            public Object onProcessCameraFrame(byte[] data, int width, int height)
//            {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                handleImage(bitmap);
//                return null;
//            }
//
//            /**
//             * Process processed frame
//             * @param data - frame
//             */
//            @Override
//            public void onReceiveProcessedCameraFrame(Object data)
//            {
//            }
//        });
    }

    /**
     * Go to ObjectDetailActivity from scanned QR code
     * @param CODE - QR code data
     */
    private void goToObjectDetail(final String CODE)
    {
        final String[] KEYS = CODE.split("\\.");
        final Intent INPUT_INTENT = new Intent(this, CeramicInputActivity.class);
        if (KEYS.length != 5)
        {
            Toast.makeText(getApplicationContext(), "Invalid Code \"" + CODE + "\"", Toast.LENGTH_SHORT).show();
            startActivity(INPUT_INTENT);
            return;
        }
        final Intent DETAIL_INTENT = new Intent(this, ObjectDetailActivity.class);
        DETAIL_INTENT.putExtra(EASTING, KEYS[2]);
        DETAIL_INTENT.putExtra(NORTHING, KEYS[3]);
        DETAIL_INTENT.putExtra(FIND_NUMBER, KEYS[4]);
        final List<String> FIND_NUMBERS = new ArrayList<>();
        String URL = globalWebServerURL + "/get_finds/?easting=" + KEYS[2] + "&northing=" + KEYS[3];
        makeVolleyStringObjectRequest(URL, queue, new StringObjectResponseWrapper() {
            /**
             * Database response
             * @param response - response received
             */
            @Override
            public void responseMethod(String response)
            {
                // convert to regular array from html link list
                ArrayList<String> finds = CheatSheet.convertLinkListToArray(response);
                // If the easting, northing, or find are not found, this will be false
                // (nothing returned at all if any of the prior 3 are not found)
                if (finds.contains(KEYS[4]))
                {
                    FIND_NUMBERS.addAll(finds);
                    DETAIL_INTENT.putExtra(ALL_FIND_NUMBER, FIND_NUMBERS.toArray(new String[FIND_NUMBERS.size()]));
                    startActivity(DETAIL_INTENT);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Find " + CODE + " not found", Toast.LENGTH_LONG).show();
                    startActivity(INPUT_INTENT);
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
     * Go to manual input
     * @param view - floating action button
     */
    public void goToManual(View view)
    {
        startActivity(new Intent(this, CeramicInputActivity.class));
    }

    /**
     * Picture taken
     * @param bitmap - image
     */
    private void handleImage(Bitmap bitmap)
    {
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
        recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", "").trim();
        cam.stopCamera();
        cam.startCamera();
        goToObjectDetail(recognizedText);
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