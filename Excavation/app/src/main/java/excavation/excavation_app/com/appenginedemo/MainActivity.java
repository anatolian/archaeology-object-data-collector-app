package excavation.excavation_app.com.appenginedemo;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import excavation.excavation_app.module.common.adapter.SimpleContextSelectedAdapter;
import excavation.excavation_app.module.common.adapter.SimpleStringAdapter;
import excavation.excavation_app.module.common.application.ApplicationHandler;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.context.getAreaTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import excavation.excavation_app.module.image.property.ImagePropertyTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appenginedemo.R;

import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.com.utils.imageloader.ImageLoader;
public class MainActivity extends ActivityBase
{
    LayoutInflater inflaterMain;
    RelativeLayout screenMain;
    LinearLayout LinearLayout1;
    Spinner areaEasting, areaNorthing, spnContext;
    TextView listdisp;
    ImageView imgphoto;
    BaseTask task;
    static String spnEast = null, spnnorth = null, imagePath = null, yes = null, north = null;
    static String east = null, ctx_no = null, next;
    ArrayList<String> ctx;
    public ImageLoader imgld;
    ListView listViewContext;
    TextView textViewReplacephotphoto, textViewnextphoto;
    String p1;
    ProgressBar progressBar2;
    String photo_id = null;
    int flag_context = 0;
    int a = 0;
    boolean replace = false, photostart = false;
    private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;
    ProgressBar progressBar1;
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    // file url to store image/video
    private Uri fileUri;
    ImagePropertyTask task1;
    static ImagePropertyBean data1;
    Spinner Spneast;
    DBHelper db;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflaterMain = getLayoutInflater();
        screenMain = (RelativeLayout) inflaterMain.inflate(R.layout.activity_main, null);
        wrapper.addView(screenMain);
        AppConstants.up = 1;
        header.setText("Context Photo");
        header.setBackgroundColor(getResources().getColor(R.color.cream));
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        areaEasting = (Spinner) findViewById(R.id.areaEasting);
        areaNorthing = (Spinner) findViewById(R.id.areaNorting);
        spnContext = (Spinner) findViewById(R.id.spnContext);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        areaEasting.setEnabled(false);
        areaNorthing.setEnabled(false);
        AppConstants.temp_Context_No = null;
        textViewReplacephotphoto = (TextView) findViewById(R.id.textViewReplacephotphoto);
        textViewnextphoto = (TextView) findViewById(R.id.textViewnextphoto);
        listdisp = (TextView) findViewById(R.id.textViewdisp);
        LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        imgphoto = (ImageView) findViewById(R.id.imageView1);
        imgld = new ImageLoader(MainActivity.this);
        listViewContext = (ListView) findViewById(R.id.listViewContext);
        task1 = new ImagePropertyTask(MainActivity.this);
        task1.execute();
        ApplicationHandler apphand = ApplicationHandler.getInstance();
        if (getIntent().hasExtra("north"))
        {
            north = getIntent().getExtras().getString("north");
        }
        if (getIntent().hasExtra("east"))
        {
            east = getIntent().getExtras().getString("east");
        }
        if (getIntent().hasExtra("ctx"))
        {
            ctx = getIntent().getExtras().getStringArrayList("ctx");
        }
        areaNorthing.setEnabled(false);
        if (!(spnnorth != null && spnnorth.length() > 0 || spnEast != null
                && spnEast.length() > 0 || AppConstants.temp_Context_No != null
                && AppConstants.temp_Context_No.size() > 0))
        {
            spnnorth = north;
            spnEast = east;
        }
        listdisp.setText(ctx_no);
        task = new getAreaTask(MainActivity.this, 1, areaNorthing, areaEasting, "e", "", spnEast,
                "", progressBar2, 0);
        task.execute();
        if (getIntent().hasExtra("imagePath") && getIntent().hasExtra("y"))
        {
            imagePath = getIntent().getExtras().getString("imagePath");
            yes = getIntent().getExtras().getString("y");
        }
        if (getIntent().hasExtra("next"))
        {
            next = getIntent().getExtras().getString("next");
        }
        if (yes != null && yes.length() > 0)
        {
            imgphoto.setImageBitmap(apphand.decodeFile(new File(imagePath)));
        }
        TextView3d.setOnClickListener(new OnClickListener() {
            /**
             * User clicked text view
             * @param v - text view
             */
            @Override
            public void onClick(View v)
            {
                if (photostart)
                {
                    if (imagePath != null && imagePath.length() > 0)
                    {
                        if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
                        {
                            TextView3d.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                            TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                            TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
                            Intent i = new Intent(MainActivity.this, Activity_3d.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please select at least one context number...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    TextView3d.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                    TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                    TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
                    Intent i = new Intent(MainActivity.this, Activity_3d.class);
                    startActivity(i);
                }
            }
        });
        TextViewSample.setOnClickListener(new OnClickListener() {
            /**
             * User pressed sample
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                if (photostart)
                {
                    if (imagePath != null && imagePath.length() > 0)
                    {
                        if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
                        {
                            TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                            TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
                            TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                            Intent i = new Intent(MainActivity.this, Activity_Sample.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please select at least one context number...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                    TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
                    TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                    Intent i = new Intent(MainActivity.this, Activity_Sample.class);
                    startActivity(i);
                }
            }
        });
        imgphoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked image
             * @param v - image
             */
            @Override
            public void onClick(View v)
            {
                if (spnnorth != null && spnnorth.length() > 0 && spnEast != null && spnEast.length() > 0)
                {
                    capture_image();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Select area", Toast.LENGTH_LONG).show();
                }
            }
        });
        textViewReplacephotphoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked replace photo
             * @param v - text view
             */
            @Override
            public void onClick(View v)
            {
                if (spnnorth != null && spnnorth.length() > 0 && spnEast != null && spnEast.length() > 0)
                {
                    replace = true;
                    capture_image();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No Photo to Replace", Toast.LENGTH_SHORT).show();
                }
            }
        });
        areaEasting.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * User pressed easting
             * @param arg0 - container
             * @param arg1 - selected item
             * @param arg2 - item position
             * @param arg3 - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.activity_mainSpnEast = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    if (spnEast != null && spnEast.length() > 0)
                    {
                        if (!spnEast.equalsIgnoreCase("s.id"))
                        {
                            AppConstants.temp_Context_No = null;
                            photo_id = null;
                            imagePath = null;
                            replace = false;
                            listViewContext.setAdapter(null);
                            listViewContext.setVisibility(View.GONE);
                            spnContext.setVisibility(View.GONE);
                            a = 0;
                            textViewnextphoto.setEnabled(false);
                            textViewReplacephotphoto.setEnabled(false);
                            textViewnextphoto.setClickable(false);
                            textViewReplacephotphoto.setClickable(false);
                            imgphoto.setImageResource(R.drawable.camera);
                        }
                    }
                    spnEast = s.id;
                    areaNorthing.setEnabled(true);
                    areaNorthing.setAdapter(null);
                    task = new getAreaTask(MainActivity.this, 1, areaNorthing, areaEasting, "n",
                            spnnorth, "", spnEast, progressBar2, 1);
                    task.execute();
                }
                else
                {
                    spnEast = "";
                    areaNorthing.setEnabled(false);
                }
            }

            /**
             * User selected nothing
             * @param arg0 - container view
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        areaNorthing.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * User selected northing
             * @param arg0 - container view
             * @param arg1 - selected item
             * @param arg2 - item location
             * @param arg3 - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.activity_mainSpnNorth = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    if (spnnorth != null && spnnorth.length() > 0)
                    {
                        if (!spnnorth.equalsIgnoreCase("s.id"))
                        {
                            AppConstants.temp_Context_No = null;
                            photo_id = null;
                            replace = false;
                            imagePath = null;
                            listViewContext.setAdapter(null);
                            listViewContext.setVisibility(View.GONE);
                            spnContext.setVisibility(View.GONE);
                            a = 0;
                            textViewnextphoto.setEnabled(false);
                            textViewReplacephotphoto.setEnabled(false);
                            textViewnextphoto.setClickable(false);
                            textViewReplacephotphoto.setClickable(false);
                            imgphoto.setImageResource(R.drawable.camera);
                        }
                    }
                    spnnorth = s.id;
                }
                else
                {
                    spnnorth = "";
                }
            }

            /**
             * Nothing selected
             * @param arg0 - container
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        spnContext.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * User selected context
             * @param arg0 - container
             * @param arg1 - item selected
             * @param arg2 - item position
             * @param arg3 - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    ctx_no = s.id;
                    if (!(AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0))
                    {
                        AppConstants.temp_Context_No = new ArrayList<String>();
                    }
                    AppConstants.temp_Context_No.add(ctx_no);
                    textViewnextphoto.setEnabled(true);
                    textViewReplacephotphoto.setEnabled(true);
                    textViewnextphoto.setClickable(true);
                    textViewReplacephotphoto.setClickable(true);
                }
            }

            /**
             * Nothing selected
             * @param arg0 - container
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        textViewnextphoto.setOnClickListener(new OnClickListener() {
            /**
             * User pressed next
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
                {
                    if (spnnorth != null && spnnorth.length() > 0 && spnEast != null && spnEast.length() > 0)
                    {
                        if (imagePath != null && imagePath.length() > 0)
                        {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            LinearLayout myLayout = new LinearLayout(MainActivity.this);
                            myLayout.setOrientation(LinearLayout.VERTICAL);
                            final TextView t1 = new TextView(MainActivity.this);
                            t1.setTextSize(15.0f);
                            t1.setTextColor(Color.WHITE);
                            t1.setPadding(10, 5, 10, 5);
                            t1.setText(getString(R.string.done_photo));
                            myLayout.addView(t1);
                            alertDialogBuilder.setTitle("Alert");
                            // set dialog message
                            alertDialogBuilder.setView(myLayout).setCancelable(false).setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                /**
                                 * User pressed ok
                                 * @param dialog - alert window
                                 * @param id - button id
                                 */
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // if this button is clicked, close current activity
                                    task = new addSinglePhotoTaskMain();
                                    task.execute();
                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    i.putExtra("north", spnnorth);
                                    i.putExtra("east", spnEast);
                                    i.putExtra("imagePath", imagePath);
                                    i.putExtra("ctx", AppConstants.temp_Context_No);
                                    startActivity(i);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                /**
                                 * User pressed cancel
                                 * @param dialog - alert window
                                 * @param id - button id
                                 */
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // if this button is clicked, just close the dialog box and do
                                    // nothing
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        }
                        else
                        {
                            Intent i = new Intent(MainActivity.this, ActivityCamera.class);
                            i.putExtra("north", spnnorth);
                            i.putExtra("east", spnEast);
                            i.putExtra("ctx", AppConstants.temp_Context_No);
                            i.putExtra("imagePath", imagePath);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Select area", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please Select at least one Context number...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Take picture
     */
    public void capture_image()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE);
    }

    /**
     * App switched out
     * @param outState - app state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri", fileUri);
    }

    /**
     * App switched into context
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Activity finished
     * @param requestCode - result request code
     * @param resultCode - result code
     * @param data - Returned data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK)
        {
            try
            {
                Bitmap thumbnail = decodeUri(fileUri);
                imgphoto.setImageBitmap(thumbnail);
            }
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // changes done here
            if (replace)
            {
                System.out.println("spneast" + spnEast + "spnnorth" + spnnorth);
            }
            else
            {
                task = new GetContextList(MainActivity.this); task.execute();
			}
        }
    }

	/**
	 * Display image from a path to ImageView
     * @param selectedImage - image selected
     * @return Returns image
     * @throws FileNotFoundException if the file cannot be found
	 */
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
    {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
        final int REQUIRED_SIZE = 100;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
        {
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    /**
     * Preview image
     */
    private void previewCapturedImage()
    {
        try
        {
            // hide video preview
            imgphoto.setVisibility(View.VISIBLE);
            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 1;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imgphoto.setImageBitmap(bitmap);
            bitmap.recycle();
            System.gc();
            if (replace)
            {
                task = new ReplacePhotoMain();
                task.execute();
            }
            else
            {
                task = new addSinglePhotoTaskMain();
                task.execute();
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creating file uri to store image/video
     * @param type - file type
     * @return Returns file URI
     */
    public Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     * @param type - file type
     * @return Returns the file
     */
    private static File getOutputMediaFile(int type)
    {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            Log.e("ImagePAth", mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            imagePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        }
        else
        {
            return null;
        }
        return mediaFile;
    }

    static class addSinglePhotoTaskMain extends BaseTask
    {
        // Context con;
        // Spinner Spneast;
        SimpleData list;
        ProgressDialog progressDialog = null;
        // SimpleTextAdapter adp;
        // String north1,east1,img,photo_id;
        String ip_address = "", camval;
        /**
         * Constructor
         */
        public addSinglePhotoTaskMain()
        {
        }

        /**
         * Get data
         * @param pos - position
         * @return Returns data
         */
        @SuppressWarnings("unchecked")
        public SimpleData getData(int pos)
        {
            return null;
        }

        /**
         * Run before thread starts
         */
        @Override
        protected void onPreExecute()
        {
            DBHelper db = DBHelper.getInstance(null);
            db.open();
            ip_address = db.getIpAddress();
            data1 = db.getImageProperty();
            db.close();
        }

        /**
         * Run thread
         * @param params - thread parameters
         * @return Returns nothing
         */
        @Override
        protected Void doInBackground(String... params)
        {
            SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
            if (imagePath != null && imagePath.length() > 0)
            {
                list = factory.addSingleimg(spnnorth, spnEast, imagePath, null, ip_address, "",
                        data1.base_image_path, data1.context_subpath);
            }
            return null;
        }

        /**
         * Thread finished
         * @param result - nothing
         */
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            if (list.result == RESPONSE_RESULT.success)
            {
                if (list.image_path != null && list.image_path.length() > 0)
                {
                    System.out.println("inside single photo task post method");
                }
                // changes done here
            }
            else
            {
                Toast.makeText(null, list.resultMsg + "", Toast.LENGTH_SHORT).show();
            }
            if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
            {
                SimpleContextSelectedAdapter adyt =
                        new SimpleContextSelectedAdapter(null, AppConstants.temp_Context_No,
                        spnEast, spnnorth, null, null, null);
            }
        }

        /**
         * Thread is cancelled
         * @param result - nothing
         */
        @Override
        protected void onCancelled(Void result)
        {
            release();
            super.onCancelled(result);
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }

        /**
         * Release the thread
         */
        @Override
        public void release()
        {
        }
    }

    static class ReplacePhotoMain extends BaseTask
    {
        SimpleData data;
        ProgressDialog progressDialog = null;
        String ip_address = "", camval;
		/**
		 * Replace photo
		 */
        public ReplacePhotoMain()
        {
        }

        /**
         * Get data
         * @param pos - position
         * @return Returns null
         */
        @SuppressWarnings("unchecked")
        public SimpleData getData(int pos)
        {
            return null;
        }

        /**
         * Run before thread
         */
        @Override
        protected void onPreExecute()
        {
            DBHelper db = DBHelper.getInstance(null);
            db.open();
            ip_address = db.getIpAddress();
            db.close();
        }

        /**
         * Run thread in background
         * @param params - thread parameters
         * @return Returns nothing
         */
        @Override
        protected Void doInBackground(String... params)
        {
            SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
            try
            {
                data = factory.ReplacePhoto(ip_address, "delete", spnEast, spnnorth, ctx_no,
                        null, imagePath);
                System.out.println("ip address" + ip_address);
                System.out.println("ip spnEast" + spnEast);
                System.out.println("ip spnnorth" + spnnorth);
                System.out.println("ip ctx_no" + ctx_no);
                System.out.println("ip imagePath" + imagePath);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return null;
        }

        /**
         * Run after the thread finishes
         * @param result - Nothing
         */
        @Override
        protected void onPostExecute(Void result)
        {
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            if (data.result == RESPONSE_RESULT.success)
            {
                Toast.makeText(null, "Photo replaced..", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(null, data.resultMsg, Toast.LENGTH_SHORT).show();
            }
            if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
            {
                SimpleContextSelectedAdapter adyt =
                        new SimpleContextSelectedAdapter(null, AppConstants.temp_Context_No,
                        spnEast, spnnorth, null, null, null);
            }
        }

        /**
         * Thread cancelled
         * @param result - nothing
         */
        @Override
        protected void onCancelled(Void result)
        {
            release();
            super.onCancelled(result);
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }

        /**
         * Release thread
         */
        @Override
        public void release()
        {
        }
    }

    static class GetContextList extends BaseTask
    {
        List<SimpleData> list;
        ProgressDialog progressDialog = null;
        String ip_address = "", camval;
        /**
         * Get contexts
         * @param con - context
         */
        public GetContextList(Context con)
        {
        }

        /**
         * Get data
         * @param pos - position
         * @return Returns null
         */
        @SuppressWarnings("unchecked")
        public SimpleData getData(int pos)
        {
            return null;
        }

        /**
         * Run before thread
         */
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(null);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            DBHelper db = DBHelper.getInstance(null);
            db.open();
            ip_address = db.getIpAddress();
            db.close();
        }

        /**
         * Run thread
         * @param params - thread parameters
         * @return Returns nothing
         */
        @Override
        protected Void doInBackground(String... params)
        {
            SimpleListFactory factory = SimpleListFactory.getInstance();
            list = factory.getContextList(ip_address, spnEast, spnnorth, null);
            return null;
        }

        /**
         * Run after thread
         * @param result - nothing
         */
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
            if (list != null && list.size() > 0)
            {
                SimpleStringAdapter asdad = new SimpleStringAdapter(null, list, spnEast, spnnorth, imagePath);
            }
            else
            {
                Toast.makeText(null, "There is no Context numbers for these areas",
                        Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Thread cancelled
         * @param result - nothing
         */
        @Override
        protected void onCancelled(Void result)
        {
            release();
            super.onCancelled(result);
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }

        /**
         * Release thread
         */
        @Override
        public void release()
        {
        }
    }

    /**
     * Restart activity
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
        {
            listViewContext.setVisibility(View.VISIBLE);
            SimpleContextSelectedAdapter adyt =
                    new SimpleContextSelectedAdapter(MainActivity.this, AppConstants.temp_Context_No, spnEast,
                    spnnorth, photo_id, listViewContext, spnContext);
            listViewContext.setAdapter(adyt);
        }
        else
        {
            listViewContext.setVisibility(View.GONE);
        }
    }
}