// Add a photo
package excavation.excavation_app.module.context;
import java.util.ArrayList;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import excavation.excavation_app.com.appenginedemo.MainActivity;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class AddSinglePhotoTask extends BaseTask
{
    private Context con;
    private SimpleData list;
    private ProgressDialog progressDialog = null;
    private String north1, east1, img, photoId;
    private String ipAddress = "";
    private ArrayList<String> ctxNo = null;
    private ImagePropertyBean data1;
    /**
     * Constructor
     * @param activityCamera - calling activity
     * @param north - northing
     * @param east - easting
     * @param imagePath - image location
     * @param tempContextNo - context
     * @param photoId - image id
     */
    public AddSinglePhotoTask(Context activityCamera, String north, String east, String imagePath,
                              ArrayList<String> tempContextNo, String photoId)
    {
        con = activityCamera;
        north1 = north;
        east1 = east;
        img = imagePath;
        ctxNo = tempContextNo;
        this.photoId = photoId;
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
        progressDialog = new ProgressDialog(con);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        DBHelper db = DBHelper.getInstance(con);
        db.open();
        ipAddress = db.getIpAddress();
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
        if (photoId != null && photoId.length() > 0)
        {
            list = factory.addSingleImg(north1, east1, img, ctxNo.get(0), ipAddress, photoId,
                    data1.baseImagePath, data1.contextSubpath);
        }
        else if (ctxNo != null && ctxNo.size() > 0)
        {

            list = factory.addSingleImg(north1, east1, img, ctxNo.get(0), ipAddress,"",
                    data1.baseImagePath, data1.contextSubpath);
        }
        else
        {
            list = factory.addSingleImg(north1, east1, img,"", ipAddress,"",
                    data1.baseImagePath, data1.contextSubpath);
        }
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
        if (list.result == RESPONSE_RESULT.success)
        {
            AppConstants.tempContextNo = null;
            Toast.makeText(con,"Uploaded Successfully", Toast.LENGTH_LONG).show();
            Intent i = new Intent(con, MainActivity.class);
            i.putExtra("pic", "camval");
            i.putExtra("north", north1);
            i.putExtra("east", east1);
            i.putExtra("ctx", ctxNo);
            con.startActivity(i);
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