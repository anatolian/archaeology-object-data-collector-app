// 3D activity
package excavation.excavation_app.com.appenginedemo;
import excavation.excavation_app.module.bil3d.AddMultiPhotoTask;
import excavation.excavation_app.module.common.adapter.SimpleImageAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.context.GetAreaTask;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.appenginedemo.R;
public class Activity3D extends ActivityBase
{
    LayoutInflater inflaterMain;
    RelativeLayout screenMain;
    Spinner areaEasting, areaNorthing;
    TextView textViewTakePhoto, textViewUploadPhoto;
    BaseTask task;
    String spnEast, spnNorth, imagePath, yes, north, east;
    ProgressBar pBar, progressBar2;
    GridView GridViewList;
    /**
     * Activity launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflaterMain = getLayoutInflater();
        screenMain = (RelativeLayout) inflaterMain.inflate(R.layout.activity_3d, null);
        wrapper.addView(screenMain);
        header.setText(getString(R.string.threed_photo_spaceless));
        header.setBackgroundColor(getResources().getColor(R.color.Azure));
        AppConstants.up = 1;
        TextView3D.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
        TextView3D.setEnabled(false);
        TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        TextViewSample.setBackgroundColor(getResources().getColor(R.color.black));
        areaEasting = (Spinner) findViewById(R.id.areaEasting);
        areaNorthing = (Spinner) findViewById(R.id.areaNorting);
        textViewTakePhoto = (TextView) findViewById(R.id.textViewReplacephotphoto);
        textViewUploadPhoto = (TextView) findViewById(R.id.textViewnextphoto);
        GridViewList = (GridView) findViewById(R.id.GridViewList);
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        if (getIntent().hasExtra("imagePath") && getIntent().hasExtra("y"))
        {
            imagePath = getIntent().getExtras().getString("imagePath");
            yes = getIntent().getExtras().getString("y");
        }
        if (getIntent().hasExtra("north") || getIntent().hasExtra("east"))
        {
            north = getIntent().getExtras().getString("north");
            east = getIntent().getExtras().getString("east");
        }
        if (!(spnNorth != null && spnNorth.length() > 0 || spnEast != null
                && spnEast.length() > 0))
        {
            spnNorth = north;
            spnEast = east;
        }
        if (AppConstants.selectedImg != null && AppConstants.selectedImg.size() > 0)
        {
            AppConstants.up = 0;
            SimpleImageAdapter img = new SimpleImageAdapter(Activity3D.this,
                    AppConstants.selectedImg);
            GridViewList.setAdapter(img);
        }
        textViewTakePhoto.setOnClickListener(new OnClickListener() {
            /**
             * User pressed take photo text
             * @param v - text
             */
            @Override
            public void onClick(View v)
            {
                final Dialog d = new Dialog(Activity3D.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                d.setContentView(R.layout.dialog_profile_picture);
                final Button buttonFromLibrary = (Button) d.findViewById(R.id.buttonFromLibrary);
                final Button buttonTakePhoto = (Button) d.findViewById(R.id.buttonTakePhoto);
                final Button buttonCancel = (Button) d.findViewById(R.id.buttonCancel);
                buttonTakePhoto.setOnClickListener(new OnClickListener() {
                    /**
                     * User pressed take photo
                     * @param v - button
                     */
                    @Override
                    public void onClick(View v)
                    {
                        Intent i = new Intent(Activity3D.this, ActivityCamera1.class);
                        i.putExtra("north", spnNorth);
                        i.putExtra("east", spnEast);
                        i.putExtra("imagePath", imagePath);
                        i.putExtra("3d", "3d");
                        startActivity(i);
                    }
                });
                buttonFromLibrary.setOnClickListener(new OnClickListener() {
                    /**
                     * User pressed open library
                     * @param v - button
                     */
                    @Override
                    public void onClick(View v)
                    {
                        Intent i = new Intent(Activity3D.this,
                                MultiPhotoSelectActivity.class);
                        i.putExtra("north", spnNorth);
                        i.putExtra("east", spnEast);
                        startActivity(i);
                    }
                });
                buttonCancel.setOnClickListener(new OnClickListener() {
                    /**
                     * User pressed cancel
                     * @param v - button
                     */
                    @Override
                    public void onClick(View v)
                    {
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
        areaNorthing.setEnabled(false);
        areaEasting.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * User selected item
             * @param arg0 - container view
             * @param arg1 - item selected
             * @param arg2 - item position
             * @param arg3 - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                final SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                AppConstants.activity3DSpnEast = arg2;
                if (arg2 > 0)
                {
                    spnEast = s.id;
                    areaNorthing.setEnabled(true);
                }
                task = new GetAreaTask(Activity3D.this, 3, areaNorthing,
                        areaEasting,"n", spnNorth, spnEast, "", progressBar2);
                task.execute();
            }

            /**
             * User selected nothing
             * @param arg0 - container
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
             * @param arg2 - item position
             * @param arg3 - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                AppConstants.activity3DSpnNorth = arg2;
                if (arg2 > 0)
                {
                    SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
                    spnNorth = s.id;
                }
            }

            /**
             * Nothing selected
             * @param arg0 - container view
             */
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
        task = new GetAreaTask(Activity3D.this, 3, areaNorthing, areaEasting,
                "e", "", spnEast, "", progressBar2);
        task.execute();
        textViewUploadPhoto.setOnClickListener(new OnClickListener() {
            /**
             * User clicked upload photo text
             * @param v - text
             */
            @Override
            public void onClick(View v)
            {
                if (AppConstants.selectedImg != null && AppConstants.selectedImg.size() > 0)
                {
                    if (spnEast != null && spnEast.length() > 0 && spnNorth != null
                            && spnNorth.length() > 0 && AppConstants.activity3DSpnEast > 0
                            && AppConstants.activity3DSpnNorth > 0)
                    {
                        task = new AddMultiPhotoTask(Activity3D.this, spnEast, spnNorth,
                                AppConstants.selectedImg, pBar);
                        task.execute();
                    }
                    else
                    {
                        Toast.makeText(Activity3D.this, "Please select area",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    AppConstants.up = 1;
                    Toast.makeText(Activity3D.this, "Please select photos to upload",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}