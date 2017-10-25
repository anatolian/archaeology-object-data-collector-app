// Camera Interface Screen
// Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import eu.livotov.labs.android.camview.CameraLiveView;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.LiveDataProcessingCallback;
public class CameraUiActivity extends AppCompatActivity
{
    HistoryHelper myDatabase;
    // anton's stuff for OCR
    public static final String DATA_PATH =
            Environment.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";
    public static final String lang = "eng";
    // camera view and scanner view stuff:
    private float x1;
    int flag = -1;
    private static final String TAG = "QRCode.java";
    private FloatingActionButton shutter;
    private CameraLiveView cam = null;
    private RelativeLayout parent;
    private ScannerLiveView scan = null;
    private FloatingActionButton fab;
    /**
     * Activity is launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
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
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists())
        {
            try
            {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out =
                        new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata");
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copied " + lang + " traineddata");
            }
            catch (IOException e)
            {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
        // setting up CameraView
        parent = (RelativeLayout) findViewById(R.id.parent);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        shutter = (FloatingActionButton) findViewById(R.id.shutter);
        fab.setImageResource(R.mipmap.qr);
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
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
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
        scan.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener()
        {
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
             * Data found
             * @param data - located data
             */
            @Override
            public void onCodeScanned(String data)
            {
                // from manual search
                JSONObject json = null;
                String jsonstring = loadJSONFromAsset();
                try
                {
                    json = new JSONObject(jsonstring);
                    System.out.println(json.names().toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                JSONObject translatedsearch = null;
                String searchitem = "";
                String searchurl = "";
                String searchdescription = "";
                String searchprovenience = "";
                String searchmaterial = "";
                String searchcuratorial_section = "";
                try
                {
                    translatedsearch = json.getJSONObject(data);
                    System.out.println(translatedsearch);
                    searchurl = translatedsearch.getString("url");
                    searchitem = translatedsearch.getString("object_name");
                    searchdescription = translatedsearch.getString("description");
                    searchprovenience = translatedsearch.getString("provenience");
                    searchmaterial = translatedsearch.getString("material");
                    searchcuratorial_section = translatedsearch.getString("curatorial_section");
                }
                catch (JSONException e)
                {
                    searchurl = "https://www.penn.museum/collections/object/" + data;
                }
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("search", searchurl);
                intent.putExtra("searchnumber", data);
                intent.putExtra("searchname", searchitem);
                intent.putExtra("searchdescription", searchdescription);
                intent.putExtra("searchprovenience", searchprovenience);
                intent.putExtra("searchmaterial", searchmaterial);
                intent.putExtra("searchcuratorial_section", searchcuratorial_section);
                myDatabase.insertSearch(data, searchitem, searchurl, searchdescription,
                        searchprovenience, searchmaterial, searchcuratorial_section);
                startActivity(intent);
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
                File f = new File(Environment.getExternalStorageDirectory() + "/archaeology/");
                ExifInterface ei = null;
                if (!f.exists())
                {
                    f.mkdirs();
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/archaeology/temp.jpeg");
                try
                {
                    OutputStream out = new FileOutputStream(file);
                    baos.writeTo(out);
                    out.close();
                    ei = new ExifInterface(Environment.getExternalStorageDirectory() + "/archaeology/temp.jpeg");
                    file.delete();
                    f.delete();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                byte[] jdata = baos.toByteArray();
                // Convert to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Log.v("ROTATION", "" + orientation);
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
     * Picture taken
     * @param bitmap - image
     */
    private void handleImage(Bitmap bitmap)
    {
        Log.v(TAG, "Before baseApi");
        Bitmap toSend = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 10, bitmap.getHeight() / 10, false);
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(toSend);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();
        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.
        Log.v(TAG, "OCRED TEXT: " + recognizedText);
        if (lang.equalsIgnoreCase("eng"))
        {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
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
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Crossfade effect
     * @param view - camera view
     */
    public void crossfade(View view)
    {
        if (flag < 0)
        {
            createScanner();
            fab.setImageResource(R.mipmap.ocr);
            flag *= -1;
        }
        else
        {
            createCamera();
            fab.setImageResource(R.mipmap.qr);
            flag *= -1;
        }
    }

    /**
     * Manual entry
     * @param view - button
     */
    public void manual(View view)
    {
        Intent intent = new Intent(this, ManualActivity.class);
        startActivity(intent);
    }

    /**
     * Fetch a JSON from file
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String loadJSONFromAsset()
    {
        String json;
        try
        {
            InputStream is = openFileInput("newjson.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        String jsonfixed = "{ ";
        String[] jsonArr = json.split("(?=\\{)");
        for (int i = 0; i < jsonArr.length; i++)
        {
            Pattern pattern = Pattern.compile("\"object_number\": (.+)");
            Matcher matcher = pattern.matcher(jsonArr[i]);
            String id_number = "";
            if (matcher.find())
            {
                id_number = matcher.group(1);
                System.out.format("'%s'\n", id_number);
            }
            String fixed = id_number + " : {";
            jsonArr[i] = jsonArr[i].replaceAll(Pattern.quote("{"), fixed);
            jsonfixed += jsonArr[i];
        }
        jsonfixed = jsonfixed.replaceAll(Pattern.quote("}"), "\\},");
        jsonfixed = jsonfixed.substring(0, jsonfixed.length() - 1);
        jsonfixed += "}";
        System.out.println(jsonfixed);
        return jsonfixed;
    }

    /**
     * Push an update
     * @param view - button
     */
    public void updateDatabase(View view)
    {
        updateDatabase updater = new updateDatabaseMuseum();
        updater.doUpdate(this);
    }

    /**
     * User pressed back
     */
    @Override
    public void onBackPressed()
    {
    }
}

