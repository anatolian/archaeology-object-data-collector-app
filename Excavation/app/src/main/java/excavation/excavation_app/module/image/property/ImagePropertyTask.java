// Image property thread
// author: anatolian
package excavation.excavation_app.module.image.property;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import excavation.excavation_app.com.appenginedemo.ActivityImageProperty;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class ImagePropertyTask extends BaseTask
{
    ProgressDialog pDialog;
    String ip_address;
    ImagePropertyBean data;
    DBHelper db;
    boolean flag1 = false;
    /**
     * Constructor
     * @param context - calling context
     */
    public ImagePropertyTask(Context context)
    {
        db = DBHelper.getInstance(context);
        db.open();
        ip_address = db.getIpAddress();
        data = db.getImageProperty();
        db.close();
    }

    /**
     * Get data
     * @param pos - position
     * @return Returns null
     */
    @Override
    public <T extends ResponseData> T getData(int pos)
    {
        return null;
    }

    /**
     * Run before thread
     */
    @Override
    protected void onPreExecute()
    {
    }

    /**
     * Run the thread
     * @param params - thread parameters
     * @return Returns nothing
     */
    @Override
    protected Void doInBackground(String... params)
    {
        SimpleObjectFactory factory=SimpleObjectFactory.getInstance();
        if (!flag1)
        {
            data = factory.getImageProperty(ip_address);
        }
        else
        {
            flag1 = true;
        }
        return super.doInBackground(params);
    }

    /**
     * Thread finished
     * @param result - nothing
     */
    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
        if (data != null && data.result == RESPONSE_RESULT.success && !flag1)
        {
            boolean flag;
            db = DBHelper.getInstance(null);
            db.open();
            flag = db.addImageProperty(data);
            db.close();
            if (flag)
            {
                Log.d("Table_image_property", "Data inserted successfully..");
            }
            else
            {
                Log.d("Table_image_property", "Data doesn't inserted successfully");
            }
        }
        else
        {
            Log.e("Table Image Property","Data is empty...");
        }
    }

    /**
     * Release the thread
     */
    @Override
    public void release()
    {
        System.gc();
    }
}