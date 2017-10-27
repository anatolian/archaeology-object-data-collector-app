// Sample activity
// @author: anatolian
package excavation.excavation_app.com.appenginedemo;
import java.io.File;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.dialog.ImageDialog;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.context.getAreaTask;
import excavation.excavation_app.module.sample.getSampleListTask;
import excavation.excavation_app.module.sample.getSampleListTask1;
import excavation.excavation_app.module.sample.getSamplePhotoTask;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.appenginedemo.R;
import excavation.excavation_app.com.utils.imageloader.ImageLoader;
public class Activity_Sample extends ActivityBase
{
    LayoutInflater inflaterMain;
    RelativeLayout screenMain;
    ListView clothmaterial;
    TextView clothmaterial1;
    Spinner areaEasting, areaNorting, spnType, spnContextNo, spnSampleNo;
    TextView textViewtakephoto, textViewuploadphoto, samplephoto, textviewuploadphoto;
    ImageView imgphoto;
    BaseTask task;
    String spnEast = "", spnnorth = "", imagePath = "", yes = "", north = "", east = "";
    String spnmaterial = "", spncon = "", spnSAm = "", type = "", sno = "", ctxno = "", gtype = "";
    String material = "", val = "";
    ImageLoader imgld;
    ProgressBar pbar, progressBar2;
    GridView GridViewList;
    private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;
    SharedPreferences preference;
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
        samplephoto = (TextView) findViewById(R.id.samplephoto);
        areaEasting = (Spinner) findViewById(R.id.areaEasting);
        areaNorting = (Spinner) findViewById(R.id.areaNorting);
        clothmaterial = (ListView) findViewById(R.id.clothmaterial);
        clothmaterial1 = (TextView) findViewById(R.id.clothmaterial1);
        spnType = (Spinner) findViewById(R.id.Type);
        spnContextNo = (Spinner) findViewById(R.id.contextNo);
        spnSampleNo = (Spinner) findViewById(R.id.sampleNo);
        textViewtakephoto = (TextView) findViewById(R.id.textviewtakephoto);
        textviewuploadphoto = (TextView) findViewById(R.id.textviewuploadphoto);
        GridViewList = (GridView) findViewById(R.id.GridViewList);
        pbar = (ProgressBar) findViewById(R.id.progressBar1);
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
            ctxno = getIntent().getExtras().getString("Context_no");
            material = getIntent().getExtras().getString("material");
            gtype = getIntent().getExtras().getString("type");
        }
        if (!(spnnorth != null && spnnorth.length() > 0 || spnEast != null
                && spnEast.length() > 0 || type != null && type.length() > 0
                || spnmaterial != null && spnmaterial.length() > 0
                || spncon != null && spncon.length() > 0 || spnSAm != null
                && spnSAm.length() > 0))
        {
            spnnorth = north;
            spnEast = east;
            type = gtype;
            spnmaterial = material;
            spncon = ctxno;
            spnSAm = sno;
        }
        task = new getSampleListTask(Activity_Sample.this, clothmaterial, "m", spnmaterial, "", "",
                "", "act", progressBar2);
        task.execute();
        task = new getSampleListTask(Activity_Sample.this, 1, spnType, "t", "", type, "", "", "",
                "", "", "", progressBar2);
        task.execute();
        task = new getAreaTask(Activity_Sample.this, 2, areaNorting, areaEasting,
                "e", "", spnEast, "", progressBar2, 0);
        task.execute();
        System.out.println("appcons pos IS===>" + AppConstants.spnNorthpos);
        flag = false;
        areaNorting.setEnabled(false);
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
                AppConstants.spnEastpos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnEast = s.id;
                    task = new getSamplePhotoTask(Activity_Sample.this, 1, GridViewList, spnnorth,
                            spnEast, spncon, spnSAm, type);
                    task.execute();
                    areaNorting.setEnabled(true);
                }
                task = new getAreaTask(Activity_Sample.this, 2, areaNorting,
                        areaEasting, "n", spnnorth, "", spnEast, progressBar2,0);
                task.execute();
                clothmaterial1.setText("");
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
        areaNorting.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                AppConstants.spnNorthpos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnnorth = s.id;
                    task = new getSamplePhotoTask(Activity_Sample.this, 2, GridViewList, spnnorth,
                            spnEast, spncon, spnSAm, type);
                    task.execute();
                    spnContextNo.setEnabled(true);
                }
                task = new getSampleListTask(Activity_Sample.this, 2, spnContextNo, "cn", "", "",
                        spncon, "", spnEast, spnnorth, "", "", progressBar2);
                task.execute();
                clothmaterial1.setText("");
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
        GridViewList.setOnItemClickListener(new OnItemClickListener() {
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
                ImageDialog d = new ImageDialog(Activity_Sample.this, d1.img);
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
                AppConstants.spnTypepos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    type = s.id;
                    task = new getSamplePhotoTask(Activity_Sample.this, 3, GridViewList, spnnorth,
                            spnEast, spncon, spnSAm, type);
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
        clothmaterial.setOnItemSelectedListener(new OnItemSelectedListener() {
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
                    spnmaterial = s.id;
                    samplephoto.setText(getString(R.string.frmt, spnmaterial));
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
                AppConstants.spnConNopos = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spncon = s.id;
                    task = new getSamplePhotoTask(Activity_Sample.this, 4, GridViewList, spnnorth,
                            spnEast, spncon, spnSAm, type);
                    task.execute();
                    spnSampleNo.setEnabled(true);
                }
                task = new getSampleListTask(Activity_Sample.this, 3, spnSampleNo, "s", "", "", "",
                        spnSAm, spnEast, spnnorth, spncon, "", progressBar2);
                task.execute();
                clothmaterial1.setText("");
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
                AppConstants.spnSamNopos = arg2;
                if (arg2 > 0)
                {
                    flag = true;
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnSAm = s.id;
                    task = new getSamplePhotoTask(Activity_Sample.this, 5, GridViewList, spnnorth,
                            spnEast, spncon, spnSAm, type);
                    task.execute();
                    task = new getSampleListTask1(Activity_Sample.this, spnnorth, spncon, spnEast,
                            "material", "list", spnSAm, progressBar2, clothmaterial1, clothmaterial);
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
        textViewtakephoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked text
             * @param v - text
             */
            @Override
            public void onClick(View v)
            {
                if (flag && AppConstants.spnSamNopos > 0)
                {
                    if (spnnorth != null && spnnorth.length() > 0 && spnEast != null && spnEast.length() > 0
                            && spnSAm != null && spnSAm.length() > 0 && spncon != null && spncon.length() > 0
                            && type != null && type.length() > 0)
                    {
                        Intent i = new Intent(Activity_Sample.this, ActivityCamera1.class);
                        i.putExtra("north", spnnorth);
                        i.putExtra("east", spnEast);
                        i.putExtra("imagePath", imagePath);
                        i.putExtra("samp_no", spnSAm);
                        i.putExtra("Context_no", spncon);
                        i.putExtra("sam", "Sam");
                        i.putExtra("material", spnmaterial);
                        i.putExtra("type", type);
                        startActivity(i);
                    }
                }
                else
                {
                    Toast.makeText(Activity_Sample.this,"Please Fill All Required Field",
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