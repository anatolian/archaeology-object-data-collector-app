// Add a photo
// @author: anatolian
package excavation.excavation_app.module.context;
import java.util.ArrayList;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.Toast;
import excavation.excavation_app.com.appenginedemo.MainActivity;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class addSinglePhotoTask extends BaseTask
{
    //Context con;
    //Spinner Spneast;
    SimpleData list;
    ProgressDialog progressDialog = null;
    SimpleTextAdapter adp;
    String north1, east1, img, photo_id;
    String ip_address = "", camval;
    ArrayList<String> ctx_no = null;
    ImagePropertyBean data1;

    /**
     * Constructor
     * @param activityCamera - camera activity
     * @param north - northing
     * @param east - easting
     * @param imagePath - location of image
     * @param temp_Context_No - context number
     * @param photoid - photo id
     * @param camval1 - camera value
     */
    public addSinglePhotoTask(Context activityCamera, String north, String east, String imagePath,
                              ArrayList<String> temp_Context_No, String photoid, String camval1)
    {
        //con = activityCamera;
        north1 = north;
        east1 = east;
        img = imagePath;
        ctx_no = temp_Context_No;
        photo_id = photoid;
        camval = camval1;
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
        if(photo_id != null && photo_id.length() > 0)
        {
            list = factory.addSingleimg(north1, east1, img, ctx_no.get(0), ip_address, photo_id,
                    data1.base_image_path, data1.context_subpath);
        }
        else if (ctx_no != null && ctx_no.size() > 0)
        {
            list = factory.addSingleimg(north1, east1, img, ctx_no.get(0), ip_address, "",
                    data1.base_image_path, data1.context_subpath);
        }
        else
        {
            list = factory.addSingleimg(north1, east1, img, "", ip_address, "",
                    data1.base_image_path, data1.context_subpath);
        }
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
        if (list.result == RESPONSE_RESULT.success)
        {
            AppConstants.temp_Context_No = null;
            Intent i = new Intent(null, MainActivity.class);
            i.putExtra("pic", "camval");
            i.putExtra("north", north1);
            i.putExtra("east", east1);
            i.putExtra("ctx", ctx_no);
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