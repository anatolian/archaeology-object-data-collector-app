// Activity property
// @author: anatolian
package excavation.excavation_app.com.appenginedemo;
import java.util.ArrayList;
import java.util.List;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import excavation.excavation_app.module.image.property.ImagePropertyTask;
import android.app.Activity;
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
    EditText txt3dsubpath, txtBase_img_path, txtContextsubpath, txtLabelareadivider;
    EditText txtContextdivider, txtLabelfont, txtLabelfontsize, txtPlacement, txtSamplelabeldivider;
    EditText txtSamplesubpath, txtContextsubpath3d1;
    Button btnUpdate;
    ImagePropertyBean data;
    Spinner spnLabelPlacement;
    String arrayPlacement[] = {"top-left", "top-center", "top-right", "left-top", "left-center",
            "left-bottom", "right-top", "right-center", "right-bottom", "bottom-left",
            "bottom-center", "bottom-right"};
    DBHelper db;
    ArrayAdapter<String> adapter;
    String placement = null;
    boolean flag;
    String _3dsubpath, base_image_path, context_subpath, area_divider, context_divider, font_size;
    String sample_divider, sample_subpath, context_subpath3d1;
    /**
     * Activity launched
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        inflater = getLayoutInflater();
        relLayout = (RelativeLayout) inflater.inflate(R.layout.activity_image_property, null);
        wrapper.addView(relLayout);
        TextViewSample.setBackgroundColor(getResources().getColor(R.color.butterflyblue));
        TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
        TextViewContext.setBackgroundColor(getResources().getColor(R.color.black));
        txt3dsubpath = (EditText) findViewById(R.id.txt3d_Subpath);
        txtBase_img_path = (EditText) findViewById(R.id.txtBase_image_path);
        txtContextsubpath = (EditText) findViewById(R.id.txtContext_subpath);
        txtLabelareadivider = (EditText) findViewById(R.id.txtLabeldivider);
        txtContextdivider = (EditText) findViewById(R.id.txtContexdivider);
        txtLabelfont = (EditText) findViewById(R.id.txtLabelfont);
        txtLabelfontsize = (EditText) findViewById(R.id.txtLabelfontsize);
        txtSamplelabeldivider = (EditText) findViewById(R.id.txtLabelsampledivider);
        txtSamplesubpath = (EditText) findViewById(R.id.txtSamplesubpath);
        spnLabelPlacement = (Spinner) findViewById(R.id.spnLabelPlacement);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        adapter = new ArrayAdapter<String>(ActivityImageProperty.this, android.R.layout.simple_spinner_item,
                arrayPlacement);
        spnLabelPlacement.setAdapter(adapter);
        spnLabelPlacement.setSelection(AppConstants.spnPlacement);
        db = DBHelper.getInstance(ActivityImageProperty.this);
        db.open();
        data = db.getImageProperty();
        db.close();
        int temp = 0;
        if (data != null && data.sample_label_placement != null && data.sample_label_placement.length() > 0)
        {
            for (int i = 0; i < arrayPlacement.length; i++)
            {
                if (arrayPlacement[i].equalsIgnoreCase(data.sample_label_placement))
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
                txt3dsubpath.setText(data.context_subpath_3d);
                txtBase_img_path.setText(data.base_image_path);
                txtContextsubpath.setText(data.context_subpath);
                txtLabelareadivider.setText(data.sample_label_area_divider);
                txtContextdivider.setText(data.sample_label_context_divider);
                txtLabelfont.setText(data.sample_label_font);
                txtLabelfontsize.setText(data.sample_label_font_size);
                txtSamplelabeldivider.setText(data.sample_label_sample_divider);
                txtSamplesubpath.setText(data.sample_subpath);
            }
            else
            {
                Toast.makeText(ActivityImageProperty.this, "Data is empty", Toast.LENGTH_SHORT).show();
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
                _3dsubpath = txt3dsubpath.getText().toString();
                base_image_path = txtBase_img_path.getText().toString();
                context_subpath = txtContextsubpath.getText().toString();
                area_divider = txtLabelareadivider.getText().toString();
                context_divider = txtContextdivider.getText().toString();
                font_size = txtLabelfontsize.getText().toString();
                sample_divider = txtSamplelabeldivider.getText().toString();
                sample_subpath = txtSamplesubpath.getText().toString();
                if (_3dsubpath != null && _3dsubpath.length() > 0)
                {
                    if (base_image_path != null && base_image_path.length() > 0)
                    {
                        if (context_subpath != null && context_subpath.length() > 0)
                        {
                            if (area_divider != null && area_divider.length() > 0)
                            {
                                if (context_divider != null && context_divider.length() > 0)
                                {
                                    if (font_size != null && font_size.length() > 0)
                                    {
                                        if (placement != null && placement.length() > 0)
                                        {
                                            if (sample_divider != null && sample_divider.length() > 0)
                                            {
                                                if (sample_subpath != null && sample_subpath.length() > 0)
                                                {
                                                    if (txtLabelfont != null && txtLabelfont.length() > 0)
                                                    {
                                                        db = DBHelper.getInstance(ActivityImageProperty.this);
                                                        db.open();
                                                        flag = db.updateImageProperty(_3dsubpath,
                                                                base_image_path, context_subpath, area_divider,
                                                                context_divider, font_size, placement,
                                                                sample_divider, sample_subpath);
                                                        db.close();
                                                        if (flag)
                                                        {
                                                            Toast.makeText(ActivityImageProperty.this,
                                                                    "Data Updated successfully",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ActivityImageProperty.this,
                                                                    Activity_Sample.class);
                                                            startActivity(intent);
                                                            ActivityImageProperty.this.finish();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(ActivityImageProperty.this,
                                                                    "Data doesn't Updated successfully",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(ActivityImageProperty.this,
                                                                "Please enter sample font",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                else
                                                {
                                                    Toast.makeText(ActivityImageProperty.this,
                                                            "Please enter sample_subpath",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                            {
                                                Toast.makeText(ActivityImageProperty.this,
                                                        "Please enter sample label sample divider",
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
                        Toast.makeText(ActivityImageProperty.this, "Please enter base image path",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(ActivityImageProperty.this, "Please enter 3d subpath",
                            Toast.LENGTH_SHORT).show();
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