// Add photo thread
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.util.ArrayList;
import excavation.excavation_app.com.appenginedemo.Activity_Sample;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
public class AddSamplePhotoTask extends BaseTask
{
    private SimpleData data = null;
    ProgressDialog progressDialog = null;
    String album_name, user_id, cover_image, photos, albumId, mode = null, east, north;
    ArrayList<String> selectedItems;
    ArrayList<String> allselectedItems = new ArrayList<String>();
    int cnt = 0;
    int i = 0;
    String batch_id, tp, samno, conno, im;
    ImagePropertyBean data1;
    private Handler handler = new Handler();

    /**
     * Constructor
     * @param con - calling context
     * @param spnEast - easting
     * @param spnnorth - northing
     * @param selectedImg - image
     * @param Con - context
     * @param sam - sample
     * @param type - type of object
     */
    public AddSamplePhotoTask(Activity con, String spnEast, String spnnorth, ArrayList<String> selectedImg,
                              String Con, String sam, String type)
    {
        east = spnEast;
        north = spnnorth;
        selectedItems = selectedImg;
        conno = Con;
        samno = sam;
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
        String ip_address = "";
        DBHelper db = DBHelper.getInstance(null);
        db.open();
        ip_address = db.getIpAddress();
        data1 = db.getImageProperty();
        db.close();
        int j = selectedItems.size() - 1;
        data = factory.AddSamplePhotosData(east, north, conno, samno, tp, selectedItems.get(j), ip_address,
                data1.context_subpath_3d, data1.base_image_path, data1.context_subpath,
                data1.sample_label_area_divider, data1.sample_label_context_divider, data1.sample_label_font,
                data1.sample_label_font_size, data1.sample_label_placement, data1.sample_label_sample_divider,
                data1.sample_subpath, data1.context_subpath3d1);
        return null;
    }

    /**
     * Thread ended
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
            Toast.makeText(null, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(null, Activity_Sample.class);
        }
        else
        {
            Toast.makeText(null, data.resultMsg, Toast.LENGTH_SHORT).show();
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