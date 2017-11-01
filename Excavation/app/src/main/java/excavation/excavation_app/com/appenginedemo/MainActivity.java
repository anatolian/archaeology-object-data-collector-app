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
import excavation.excavation_app.module.context.GetAreaTask;
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
public class MainActivity extends ActivityBase
{
    LayoutInflater inflaterMain;
    RelativeLayout screenMain;
    LinearLayout LinearLayout1;
    Spinner areaEasting, areaNorthing, spnContext;
    TextView listDisp;
    ImageView imgPhoto;
    BaseTask task;
    Context context;
    static String spnEast = null, spnNorth = null, imagePath = null, yes = null, north = null;
    static String east = null, ctxNo = null, next, photoId = null;
    ArrayList<String> ctx;
    ListView listViewContext;
    TextView textViewReplacePhoto, textViewNextPhoto;
    ProgressBar progressBar2;
    int a = 0;
    boolean replace = false, photoStart = false;
    private static int CAMERA_CAPTURE = 999;
    ProgressBar progressBar1;
    // Activity request codes
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    // file url to store image/video
    private Uri fileUri;
    ImagePropertyTask task1;
    static ImagePropertyBean data1;
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
        header.setText(getString(R.string.context_photo_no_space));
        header.setBackgroundColor(getResources().getColor(R.color.cream));
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        areaEasting = (Spinner) findViewById(R.id.areaEasting);
        areaNorthing = (Spinner) findViewById(R.id.areaNorting);
        spnContext = (Spinner) findViewById(R.id.spnContext);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        areaEasting.setEnabled(false);
        areaNorthing.setEnabled(false);
        AppConstants.tempContextNo = null;
        textViewReplacePhoto = (TextView) findViewById(R.id.textViewReplacephotphoto);
        textViewNextPhoto = (TextView) findViewById(R.id.textViewnextphoto);
        listDisp = (TextView) findViewById(R.id.textViewdisp);
        LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        imgPhoto = (ImageView) findViewById(R.id.imageView1);
        listViewContext = (ListView) findViewById(R.id.listViewContext);
        task1 = new ImagePropertyTask(MainActivity.this);
        task1.execute();
        ApplicationHandler appHand = ApplicationHandler.getInstance();
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
        if (!(spnNorth != null && spnNorth.length() > 0 || spnEast != null
                && spnEast.length() > 0 || AppConstants.tempContextNo != null
                && AppConstants.tempContextNo.size() > 0))
        {
            spnNorth = north;
            spnEast = east;
        }
        listDisp.setText(ctxNo);
        task = new GetAreaTask(MainActivity.this, 1, areaNorthing, areaEasting,
                "e", "", spnEast, "", progressBar2);
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
            imgPhoto.setImageBitmap(appHand.decodeFile(new File(imagePath)));
        }
        TextView3d.setOnClickListener(new OnClickListener() {
            /**
             * User clicked text view
             * @param v - text view
             */
            @Override
            public void onClick(View v)
            {
                if (photoStart)
                {
                    if (imagePath != null && imagePath.length() > 0)
                    {
                        if (AppConstants.tempContextNo != null && AppConstants.tempContextNo.size() > 0)
                        {
                            TextView3d.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                            TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                            TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
                            Intent i = new Intent(MainActivity.this, Activity3d.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,
                                    "Please select at least one context number...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    TextView3d.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                    TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                    TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
                    Intent i = new Intent(MainActivity.this, Activity3d.class);
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
                if (photoStart)
                {
                    if (imagePath != null && imagePath.length() > 0)
                    {
                        if (AppConstants.tempContextNo != null
                                && AppConstants.tempContextNo.size() > 0)
                        {
                            TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                            TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
                            TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                            Intent i = new Intent(MainActivity.this, ActivitySample.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,
                                    "Please select at least one context number...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
                    TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
                    TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
                    Intent i = new Intent(MainActivity.this, ActivitySample.class);
                    startActivity(i);
                }
            }
        });
        imgPhoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked image
             * @param v - image
             */
            @Override
            public void onClick(View v)
            {
                if (spnNorth != null && spnNorth.length() > 0 && spnEast != null
                        && spnEast.length() > 0)
                {
                    captureImage();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Select area",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        textViewReplacePhoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked replace photo
             * @param v - text view
             */
            @Override
            public void onClick(View v)
            {
                if (spnNorth != null && spnNorth.length() > 0 && spnEast != null
                        && spnEast.length() > 0)
                {
                    replace = true;
                    captureImage();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No Photo to Replace",
                            Toast.LENGTH_SHORT).show();
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
                AppConstants.activityMainSpnEast = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    if (spnEast != null && spnEast.length() > 0)
                    {
                        if (!spnEast.equalsIgnoreCase("s.id"))
                        {
                            AppConstants.tempContextNo = null;
                            photoId = null;
                            imagePath = null;
                            replace = false;
                            listViewContext.setAdapter(null);
                            listViewContext.setVisibility(View.GONE);
                            spnContext.setVisibility(View.GONE);
                            a = 0;
                            textViewNextPhoto.setEnabled(false);
                            textViewReplacePhoto.setEnabled(false);
                            textViewNextPhoto.setClickable(false);
                            textViewReplacePhoto.setClickable(false);
                            imgPhoto.setImageResource(R.drawable.camera);
                        }
                    }
                    spnEast = s.id;
                    areaNorthing.setEnabled(true);
                    areaNorthing.setAdapter(null);
                    task = new GetAreaTask(MainActivity.this, 1, areaNorthing,
                            areaEasting, "n", spnNorth, "", spnEast, progressBar2);
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
                AppConstants.activityMainSpnNorth = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    if (spnNorth != null && spnNorth.length() > 0)
                    {
                        if (!spnNorth.equalsIgnoreCase("s.id"))
                        {
                            AppConstants.tempContextNo = null;
                            photoId = null;
                            replace = false;
                            imagePath = null;
                            listViewContext.setAdapter(null);
                            listViewContext.setVisibility(View.GONE);
                            spnContext.setVisibility(View.GONE);
                            a = 0;
                            textViewNextPhoto.setEnabled(false);
                            textViewReplacePhoto.setEnabled(false);
                            textViewNextPhoto.setClickable(false);
                            textViewReplacePhoto.setClickable(false);
                            imgPhoto.setImageResource(R.drawable.camera);
                        }
                    }
                    spnNorth = s.id;
                }
                else
                {
                    spnNorth = "";
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
                    ctxNo = s.id;
                    if (!(AppConstants.tempContextNo != null
                            && AppConstants.tempContextNo.size() > 0))
                    {
                        AppConstants.tempContextNo = new ArrayList<>();
                    }
                    AppConstants.tempContextNo.add(ctxNo);
                    textViewNextPhoto.setEnabled(true);
                    textViewReplacePhoto.setEnabled(true);
                    textViewNextPhoto.setClickable(true);
                    textViewReplacePhoto.setClickable(true);
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
        textViewNextPhoto.setOnClickListener(new OnClickListener() {
            /**
             * User pressed next
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                if (AppConstants.tempContextNo != null && AppConstants.tempContextNo.size() > 0)
                {
                    if (spnNorth != null && spnNorth.length() > 0 && spnEast != null
                            && spnEast.length() > 0)
                    {
                        if (imagePath != null && imagePath.length() > 0)
                        {
                            AlertDialog.Builder alertDialogBuilder
                                    = new AlertDialog.Builder(MainActivity.this);
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
                            alertDialogBuilder.setView(myLayout).setCancelable(false)
                                    .setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                /**
                                 * User pressed ok
                                 * @param dialog - alert window
                                 * @param id - button id
                                 */
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // if this button is clicked, close current activity
                                    context = getApplicationContext();
                                    task = new addSinglePhotoTaskMain();
                                    task.execute();
                                    Intent i = new Intent(MainActivity.this,
                                            MainActivity.class);
                                    i.putExtra("north", spnNorth);
                                    i.putExtra("east", spnEast);
                                    i.putExtra("imagePath", imagePath);
                                    i.putExtra("ctx", AppConstants.tempContextNo);
                                    startActivity(i);
                                }
                            }).setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
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
                            Intent i = new Intent(MainActivity.this,
                                    ActivityCamera.class);
                            i.putExtra("north", spnNorth);
                            i.putExtra("east", spnEast);
                            i.putExtra("ctx", AppConstants.tempContextNo);
                            i.putExtra("imagePath", imagePath);
                            startActivity(i);
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Select area",
                                Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this,
                            "Please Select at least one Context number...",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Take picture
     */
    public void captureImage()
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
                imgPhoto.setImageBitmap(thumbnail);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            // changes done here
            if (replace)
            {
                System.out.println("spneast" + spnEast + "spnnorth" + spnNorth);
            }
            else
            {
                task = new GetContextList(MainActivity.this);
                task.execute();
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
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage),
                null, o);
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
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage),
                null, o2);
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
        File mediaStorageDir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME
                        + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
            Log.e("ImagePath", mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
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
        SimpleData list;
        ProgressDialog progressDialog = null;
        String ipAddress = "";
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
            ipAddress = db.getIpAddress();
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
                list = factory.addSingleImg(spnNorth, spnEast, imagePath, null, ipAddress,
                        "", data1.baseImagePath, data1.contextSubpath);
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
                if (list.imagePath != null && list.imagePath.length() > 0)
                {
                    System.out.println("inside single photo task post method");
                }
                // changes done here
            }
            else
            {
                Toast.makeText(null, list.resultMsg + "", Toast.LENGTH_SHORT).show();
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

    class GetContextList extends BaseTask
    {
        Context con;
        List<SimpleData> list;
        ProgressDialog progressDialog = null;
        String ipAddress = "";
        public GetContextList(Context con)
        {
            this.con = con;
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
         * Start thread
         */
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            DBHelper db = DBHelper.getInstance(MainActivity.this);
            db.open();
            ipAddress = db.getIpAddress();
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
            list = factory.getContextList(ipAddress, spnEast, spnNorth, photoId);
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
            if (list != null && list.size() > 0)
            {
                spnContext.setVisibility(View.VISIBLE);
                SimpleStringAdapter asdad = new SimpleStringAdapter(con, list);
                spnContext.setAdapter(asdad);
                if (a == 0)
                {
                    spnContext.performClick();
                }
                a = a + 1;
            }
            else
            {
                Toast.makeText(MainActivity.this,
                        "There is no Context numbers for these areas",
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
         * Thread released
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
        if (AppConstants.tempContextNo != null && AppConstants.tempContextNo.size() > 0)
        {
            listViewContext.setVisibility(View.VISIBLE);
            SimpleContextSelectedAdapter adyt =
                    new SimpleContextSelectedAdapter(MainActivity.this,
                    AppConstants.tempContextNo, spnEast, spnNorth, photoId, listViewContext,
                            spnContext);
            listViewContext.setAdapter(adyt);
        }
        else
        {
            listViewContext.setVisibility(View.GONE);
        }
    }
}