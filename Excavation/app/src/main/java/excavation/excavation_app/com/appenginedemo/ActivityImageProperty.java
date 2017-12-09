// Image property
package excavation.excavation_app.com.appenginedemo;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.appenginedemo.R;
public class ActivityImageProperty extends ActivityBase
{
    LayoutInflater inflater;
    RelativeLayout relLayout;
    EditText txt3DSubPath, txtBaseImgPath, txtContextSubPath, txtLabelAreaDivider;
    EditText txtContextDivider, txtLabelFont, txtLabelFontSize, txtSampleLabelDivider;
    EditText txtSampleSubPath;
    Button btnUpdate;
    ImagePropertyBean data;
    Spinner spnLabelPlacement;
    String arrayPlacement[] = {"top-left", "top-center", "top-right", "left-top", "left-center",
            "left-bottom", "right-top", "right-center", "right-bottom", "bottom-left",
            "bottom-center", "bottom-right"};
    DBHelper db;
    ArrayAdapter<String> adapter;
    boolean flag;
    String threeDSubPath, baseImagePath, contextSubPath, areaDivider, contextDivider, fontSize;
    String sampleDivider, sampleSubPath, placement;
    /**
     * Activity launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        relLayout = (RelativeLayout) inflater.inflate(R.layout.activity_image_property, null);
        wrapper.addView(relLayout);
        TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
        TextView3D.setBackgroundColor(getResources().getColor(R.color.black));
        TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
        txt3DSubPath = (EditText) findViewById(R.id.txt3d_Subpath);
        txtBaseImgPath = (EditText) findViewById(R.id.txtBase_image_path);
        txtContextSubPath = (EditText) findViewById(R.id.txtContext_subpath);
        txtLabelAreaDivider = (EditText) findViewById(R.id.txtLabeldivider);
        txtContextDivider = (EditText) findViewById(R.id.txtContextdivider);
        txtLabelFont = (EditText) findViewById(R.id.txtLabelfont);
        txtLabelFontSize = (EditText) findViewById(R.id.txtLabelfontsize);
        txtSampleLabelDivider = (EditText) findViewById(R.id.txtLabelsampledivider);
        txtSampleSubPath = (EditText) findViewById(R.id.txtSamplesubpath);
        spnLabelPlacement = (Spinner) findViewById(R.id.spnLabelPlacement);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        adapter = new ArrayAdapter<>(ActivityImageProperty.this,
                android.R.layout.simple_spinner_item, arrayPlacement);
        spnLabelPlacement.setAdapter(adapter);
        spnLabelPlacement.setSelection(AppConstants.spnPlacement);
        db = DBHelper.getInstance(ActivityImageProperty.this);
        db.open();
        data = db.getImageProperty();
        db.close();
        int temp = 0;
        if (data != null && data.sampleLabelPlacement != null
                && data.sampleLabelPlacement.length() > 0)
        {
            for (int i = 0; i < arrayPlacement.length; i++)
            {
                if (arrayPlacement[i].equalsIgnoreCase(data.sampleLabelPlacement))
                {
                    temp = i;
                }
            }
        }
        spnLabelPlacement.setSelection(temp);
        try
        {
            if (data != null)
            {
                txt3DSubPath.setText(data.contextSubPath3D);
                txtBaseImgPath.setText(data.baseImagePath);
                txtContextSubPath.setText(data.contextSubPath);
                txtLabelAreaDivider.setText(data.sampleLabelAreaDivider);
                txtContextDivider.setText(data.sampleLabelContextDivider);
                txtLabelFont.setText(data.sampleLabelFont);
                txtLabelFontSize.setText(data.sampleLabelFontSize);
                txtSampleLabelDivider.setText(data.sampleLabelSampleDivider);
                txtSampleSubPath.setText(data.sampleSubPath);
            }
            else
            {
                Toast.makeText(ActivityImageProperty.this, "Data is empty",
                        Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        spnLabelPlacement.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * Item selected
             * @param parent - container view
             * @param view - item selected
             * @param position - item position
             * @param id - item id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                AppConstants.spnPlacement = position;
                placement = (String) parent.getItemAtPosition(position);
            }

            /**
             * Nothing selected
             * @param parent - container
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        btnUpdate.setOnClickListener(new OnClickListener() {
            /**
             * User pressed update
             * @param v - button
             */
            @Override
            public void onClick(View v)
            {
                threeDSubPath = txt3DSubPath.getText().toString();
                baseImagePath = txtBaseImgPath.getText().toString();
                contextSubPath = txtContextSubPath.getText().toString();
                areaDivider = txtLabelAreaDivider.getText().toString();
                contextDivider = txtContextDivider.getText().toString();
                fontSize = txtLabelFontSize.getText().toString();
                sampleDivider = txtSampleLabelDivider.getText().toString();
                sampleSubPath = txtSampleSubPath.getText().toString();
                if (threeDSubPath != null && threeDSubPath.length() > 0)
                {
                    if (baseImagePath != null && baseImagePath.length() > 0)
                    {
                        if (contextSubPath != null && contextSubPath.length() > 0)
                        {
                            if (areaDivider != null && areaDivider.length() > 0)
                            {
                                if (contextDivider != null && contextDivider.length() > 0)
                                {
                                    if (fontSize != null && fontSize.length() > 0)
                                    {
                                        if (placement != null && placement.length() > 0)
                                        {
                                            if (sampleDivider != null &&
                                                    sampleDivider.length() > 0)
                                            {
                                                if (sampleSubPath != null
                                                        && sampleSubPath.length() > 0)
                                                {
                                                    if (txtLabelFont != null
                                                            && txtLabelFont.length() > 0)
                                                    {
                                                        db = DBHelper.getInstance(
                                                                ActivityImageProperty.this);
                                                        db.open();
                                                        flag = db.updateImageProperty(
                                                                threeDSubPath, baseImagePath,
                                                                contextSubPath, areaDivider,
                                                                contextDivider, fontSize,
                                                                placement, sampleDivider,
                                                                sampleSubPath);
                                                        db.close();
                                                        if (flag)
                                                        {
                                                            Toast.makeText(
                                                                    ActivityImageProperty
                                                                            .this,
                                                                    "Data updated " +
                                                                            "successfully",
                                                                    Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(
                                                                    ActivityImageProperty.this,
                                                                    ActivitySample.class));
                                                            ActivityImageProperty.this.finish();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(
                                                                    ActivityImageProperty
                                                                            .this,
                                                                    "Data update failed",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(
                                                                ActivityImageProperty.this,
                                                                "Please enter sample font",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                else
                                                {
                                                    Toast.makeText(
                                                            ActivityImageProperty.this,
                                                            "Please enter sample subpath",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                            {
                                                Toast.makeText(ActivityImageProperty.this,
                                                        "Please enter sample label sample "
                                                                + "divider",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(ActivityImageProperty.this,
                                                    "Please select sample label placement",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(ActivityImageProperty.this,
                                                "Please enter sample label font size",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(ActivityImageProperty.this,
                                            "Please enter sample label context divider",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(ActivityImageProperty.this,
                                        "Please enter sample label area divider",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ActivityImageProperty.this,
                                    "Please enter context subpath", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ActivityImageProperty.this,
                                "Please enter base image path", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(ActivityImageProperty.this,
                            "Please enter 3D subpath", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Fill action overflow
     * @param menu - the menu
     * @return Returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }
}