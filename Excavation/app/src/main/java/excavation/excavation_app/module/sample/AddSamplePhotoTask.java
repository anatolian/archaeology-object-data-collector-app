// Add photo thread
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.util.ArrayList;
import excavation.excavation_app.com.appenginedemo.ActivitySample;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;
public class AddSamplePhotoTask extends BaseTask
{
    private Activity context;
    private SimpleData data = null;
    private ProgressDialog progressDialog = null;
    private String east, north;
    private ArrayList<String> selectedItems;
    private String tp, samNo,conNo;
    /**
     * Constructor
     * @param con context
     * @param spnEast - easting
     * @param spnNorth - northing
     * @param selectedImg - image
     * @param sam - sample
     * @param type - type of object
     */
    public AddSamplePhotoTask(Activity con, String spnEast, String spnNorth,
                              ArrayList<String> selectedImg, String context, String sam,
                              String type)
    {
        this.context = con;
        east = spnEast;
        north = spnNorth;
        selectedItems = selectedImg;
        conNo = context;
        samNo = sam;
        tp = type;
    }

    /**
     * Get data
     * @param pos - position
     * @return Returns data
     */
    @SuppressWarnings("unchecked")
    public SimpleData getData(int pos)
    {
        return data;
    }

    /**
     * Run before thread
     */
    @Override
    protected void onPreExecute()
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Uploading..");
        progressDialog.show();
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
        DBHelper db = DBHelper.getInstance(context);
        db.open();
        String ipAddress = db.getIpAddress();
        ImagePropertyBean data1 = db.getImageProperty();
        db.close();
        int j = selectedItems.size() - 1;
        data = factory.AddSamplePhotosData(east, north, conNo, samNo, tp, selectedItems.get(j),
                ipAddress, data1.contextSubpath3d, data1.baseImagePath, data1.contextSubpath,
                data1.sampleLabelAreaDivider, data1.sampleLabelContextDivider,
                data1.sampleLabelFont, data1.sampleLabelFontSize, data1.sampleLabelPlacement,
                data1.sampleLabelSampleDivider, data1.sampleSubpath);
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
        if (data.result == RESPONSE_RESULT.success)
        {
            context.finish();
            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, ActivitySample.class);
            context.startActivity(i);
        }
        else
        {
            Toast.makeText(context, data.resultMsg, Toast.LENGTH_SHORT).show();
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