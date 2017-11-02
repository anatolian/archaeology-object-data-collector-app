// Sample activity
package excavation.excavation_app.com.appenginedemo;
import java.io.File;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.dialog.ImageDialog;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.context.GetAreaTask;
import excavation.excavation_app.module.sample.GetSampleListTask;
import excavation.excavation_app.module.sample.GetSampleListTask1;
import excavation.excavation_app.module.sample.GetSamplePhotoTask;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.appenginedemo.R;
public class ActivitySample extends ActivityBase
{
    LayoutInflater inflaterMain;
    RelativeLayout screenMain;
    ListView clothMaterial;
    TextView clothMaterial1;
    Spinner areaEasting, areaNorthing, spnType, spnContextNo, spnSampleNo;
    TextView textViewTakePhoto, samplePhoto, textViewUploadPhoto;
    BaseTask task;
    String spnEast = "", spnNorth = "", imagePath = "", north = "", east = "";
    String spnMaterial = "", spnCon = "", spnSam = "", type = "", sno = "", ctxNo = "", gtype = "";
    String material = "", val = "";
    ProgressBar pBar, progressBar2;
    GridView gridViewList;
    boolean flag;
    /**
     * Launch activity
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflaterMain = getLayoutInflater();
        screenMain = (RelativeLayout) inflaterMain.inflate(R.layout.activity_sample1,null);
        wrapper.addView(screenMain);
        header.setText(getString(R.string.sample));
        header.setBackgroundColor(getResources().getColor(R.color.Lavender));
        AppConstants.up = 1;
        TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
        TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
        TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
        samplePhoto = (TextView) findViewById(R.id.samplephoto);
        areaEasting = (Spinner) findViewById(R.id.areaEasting);
        areaNorthing = (Spinner) findViewById(R.id.areaNorting);
        clothMaterial = (ListView) findViewById(R.id.clothmaterial);
        clothMaterial1 = (TextView) findViewById(R.id.clothmaterial1);
        spnType = (Spinner) findViewById(R.id.Type);
        spnContextNo = (Spinner) findViewById(R.id.contextNo);
        spnSampleNo = (Spinner) findViewById(R.id.sampleNo);
        textViewTakePhoto = (TextView) findViewById(R.id.textviewtakephoto);
        textViewUploadPhoto = (TextView) findViewById(R.id.textviewuploadphoto);
        gridViewList = (GridView) findViewById(R.id.GridViewList);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        if (getIntent().hasExtra("db"))
        {
            val = getIntent().getExtras().getString("db");
        }
        if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
                || getIntent().hasExtra("samp_no") || getIntent().hasExtra("Context_no")
                || getIntent().hasExtra("material") || getIntent().hasExtra("type"))
        {
            north = getIntent().getExtras().getString("north");
            east = getIntent().getExtras().getString("east");
            sno = getIntent().getExtras().getString("samp_no");
            ctxNo = getIntent().getExtras().getString("Context_no");
            material = getIntent().getExtras().getString("material");
            gtype = getIntent().getExtras().getString("type");
        }
        if (!(spnNorth != null && spnNorth.length() > 0 || spnEast != null && spnEast.length() > 0
                || type != null && type.length() > 0 || spnMaterial != null
                && spnMaterial.length() > 0 || spnCon != null && spnCon.length() > 0
                || spnSam != null && spnSam.length() > 0))
        {
            spnNorth = north;
            spnEast = east;
            type = gtype;
            spnMaterial = material;
            spnCon = ctxNo;
            spnSam = sno;
        }
        task = new GetSampleListTask(ActivitySample.this, clothMaterial, "m",
                spnMaterial, "", "","", "act", progressBar2);
        task.execute();
        task = new GetSampleListTask(ActivitySample.this, "t", spnMaterial, type,
                "", "", "", "", progressBar2);
        task.execute();
        task = new GetAreaTask(ActivitySample.this, 2, areaNorthing, areaEasting,
                "e", "", spnEast, "", progressBar2);
        task.execute();
        System.out.println("appcons pos IS===>" + AppConstants.spnNorthPos);
        flag = false;
        areaNorthing.setEnabled(false);
        areaEasting.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * User selected value
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.spnEastPos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnEast = s.id;
                    task = new GetSamplePhotoTask(ActivitySample.this, 1,
                            gridViewList, spnNorth, spnEast, spnCon, spnSam, type);
                    task.execute();
                    areaNorthing.setEnabled(true);
                }
                task = new GetAreaTask(ActivitySample.this, 2, areaNorthing,
                        areaEasting, "n", spnNorth, "", spnEast, progressBar2);
                task.execute();
                clothMaterial1.setText("");
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
        spnContextNo.setEnabled(false);
        areaNorthing.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * Item selected
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.spnNorthPos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnNorth = s.id;
                    task = new GetSamplePhotoTask(ActivitySample.this, 2,
                            gridViewList, spnNorth, spnEast, spnCon, spnSam, type);
                    task.execute();
                    spnContextNo.setEnabled(true);
                }
                task = new GetSampleListTask(ActivitySample.this,"cn", "",
                        "", spnCon, "", "", "", progressBar2);
                task.execute();
                clothMaterial1.setText("");
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
        gridViewList.setOnItemClickListener(new OnItemClickListener() {
            /**
             * User clicked grid
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                SimpleData d1 = (SimpleData) arg0.getItemAtPosition(arg2);
                ImageDialog d = new ImageDialog(ActivitySample.this, d1.img);
                d.show();
            }
        });
        spnType.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * Item selected
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.spnTypePos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    type = s.id;
                    task = new GetSamplePhotoTask(ActivitySample.this, 3,
                            gridViewList, spnNorth, spnEast, spnCon, spnSam, type);
                    task.execute();
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
        clothMaterial.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * User selected item
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnMaterial = s.id;
                    samplePhoto.setText(getString(R.string.frmt, spnMaterial));
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
        spnSampleNo.setEnabled(false);
        spnContextNo.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * Item selected
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.spnConNoPos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnCon = s.id;
                    task = new GetSamplePhotoTask(ActivitySample.this, 4,
                            gridViewList, spnNorth, spnEast, spnCon, spnSam, type);
                    task.execute();
                    spnSampleNo.setEnabled(true);
                }
                task = new GetSampleListTask(ActivitySample.this, "s", "",
                        "", "", spnSam, spnCon, "", progressBar2);
                task.execute();
                clothMaterial1.setText("");
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
        spnSampleNo.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * Item selected
             * @param arg0 - container
             * @param arg1 - item
             * @param arg2 - position
             * @param arg3 - id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.spnSamNoPos = arg2;
                if (arg2 > 0)
                {
                    flag = true;
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnSam = s.id;
                    task = new GetSamplePhotoTask(ActivitySample.this, 5,
                            gridViewList, spnNorth,
                            spnEast, spnCon, spnSam, type);
                    task.execute();
                    task = new GetSampleListTask1(ActivitySample.this, spnNorth, spnCon,
                            spnEast,"material", "list", spnSam, progressBar2,
                            clothMaterial1, clothMaterial);
                    task.execute();
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
        textViewTakePhoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked text
             * @param v - text
             */
            @Override
            public void onClick(View v)
            {
                if (flag && AppConstants.spnSamNoPos > 0)
                {
                    if (spnNorth != null && spnNorth.length() > 0 && spnEast != null
                            && spnEast.length() > 0 && spnSam != null && spnSam.length() > 0
                            && spnCon != null && spnCon.length() > 0 && type != null
                            && type.length() > 0)
                    {
                        Intent i = new Intent(ActivitySample.this,
                                ActivityCamera1.class);
                        i.putExtra("north", spnNorth);
                        i.putExtra("east", spnEast);
                        i.putExtra("imagePath", imagePath);
                        i.putExtra("samp_no", spnSam);
                        i.putExtra("Context_no", spnCon);
                        i.putExtra("sam", "Sam");
                        i.putExtra("material", spnMaterial);
                        i.putExtra("type", type);
                        startActivity(i);
                    }
                }
                else
                {
                    Toast.makeText(ActivitySample.this,"Please Fill All Required Fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Activity paused
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        trimCache(this);
    }

    /**
     * Activity deleted
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        trimCache(this);
    }

    /**
     * Delete cache items
     * @param context - calling context
     */
    public static void trimCache(Context context)
    {
        try
        {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory())
            {
                deleteDir(dir);
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * Delete directory
     * @param dir - directory
     * @return Returns if it was successful
     */
    public static boolean deleteDir(File dir)
    {
        if (dir != null && dir.isDirectory())
        {
            String[] children = dir.list();
            for (String s: children)
            {
                boolean success = deleteDir(new File(dir, s));
                if (!success)
                {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
}