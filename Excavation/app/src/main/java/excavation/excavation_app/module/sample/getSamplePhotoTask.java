// Get photo
// @author: anatolian
package excavation.excavation_app.module.sample;
import java.util.List;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class getSamplePhotoTask extends BaseTask
{
    List<SimpleData> list;
    ProgressDialog progressDialog = null;
    SimpleImagePhotoAdapter adp;
    String m, north, east, cloth, gtype, contno, sno;
    String type, mode = "list", type1;
    ImagePropertyBean data1;
    String ip_address = "";
    int count;
    /**
     * Constructor
     * @param activity_Sample - calling activity
     * @param count - item count
     * @param gridViewList - grid view
     * @param spnnorth - northing
     * @param spnEast - easting
     * @param spncon - context
     * @param spnSAm - sample
     * @param type - specimen type
     */
    public getSamplePhotoTask(Context activity_Sample, int count, GridView gridViewList,
                               String spnnorth, String spnEast, String spncon, String spnSAm, String type)
    {
        north = spnnorth;
        east = spnEast;
        contno = spncon;
        sno = spnSAm;
        type1 = type;
        this.count=count;
    }

    /**
     * Get the data
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
        SimpleListFactory factory = SimpleListFactory.getInstance();
        if (count == 5 || count == 3 && AppConstants.spnConNopos > 1 && AppConstants.spnSamNopos > 0)
        {
            list = factory.getPhotoSampleList(north, east, contno, sno, type1, ip_address,
                    data1.base_image_path, data1.sample_subpath);
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
        if (list != null && list.size() > 0)
        {
            adp = new SimpleImagePhotoAdapter(null, list);
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