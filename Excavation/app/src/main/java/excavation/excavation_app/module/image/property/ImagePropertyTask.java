// Image property thread
// author: anatolian
package excavation.excavation_app.module.image.property;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class ImagePropertyTask extends BaseTask
{
    private Context context;
    private ProgressDialog pDialog;
    private String ipAddress;
    private ImagePropertyBean data;
    private DBHelper db;
    private boolean flag1 = false;
    /**
     * Constructor
     * @param context - calling context
     */
    public ImagePropertyTask(Context context)
    {
        this.context = context;
        db = DBHelper.getInstance(context);
        db.open();
        ipAddress = db.getIpAddress();
        data = db.getImageProperty();
        db.close();
    }

    /**
     * Get data
     * @param pos - position
     * @return Returns nothing
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
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Loading Image Property");
        pDialog.setMessage("Loading....");
        pDialog.setIndeterminate(false);
        pDialog.show();
    }

    /**
     * Run Thread
     * @param params - thread parameters
     * @return Returns nothing
     */
    @Override
    protected Void doInBackground(String... params)
    {
        SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
        if (!flag1)
        {
            data = factory.getImageProperty(ipAddress);
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
            db = DBHelper.getInstance(context);
            db.open();
            flag = db.addImageProperty(data);
            db.close();
            if (flag)
            {
                Log.d("Table_image_property", "Data inserted successfully..");
            }
            else
            {
                Log.d("Table_image_property", "Data insertion failed");
            }
        }
        else
        {
            Log.e("Table Image Property","Data is empty...");
        }
    }

    /**
     * Release thread
     */
    @Override
    public void release()
    {
        System.gc();
    }
}