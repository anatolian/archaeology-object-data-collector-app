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
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class GetSamplePhotoTask extends BaseTask
{
    private Context con;
    private GridView gList;
    List<SimpleData> list;
    private ProgressDialog progressDialog = null;
    private String north, east, sno, type1, ipAddress = "", contNo;
    private ImagePropertyBean data1;
    private int count;
    /**
     * Constructor
     * @param activitySample - calling activity
     * @param count - item count
     * @param gridViewList - grid view
     * @param spnNorth - northing
     * @param spnEast - easting
     * @param spnCon - context
     * @param spnSam - sample
     * @param type - specimen type
     */
    public GetSamplePhotoTask(Context activitySample, int count, GridView gridViewList,
                              String spnNorth, String spnEast, String spnCon, String spnSam,
                              String type)
    {
        con = activitySample;
        gList = gridViewList;
        north = spnNorth;
        east = spnEast;
        sno = spnSam;
        type1 = type;
        contNo = spnCon;
        this.count = count;
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
        SimpleListFactory factory = SimpleListFactory.getInstance();
        if (count == 5 || count == 3 && AppConstants.spnConNoPos > 1 && AppConstants.spnSamNoPos > 0)
        {
            list = factory.getPhotoSampleList(north, east, contNo, sno, type1, ipAddress,
                    data1.baseImagePath, data1.sampleSubpath);
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
            gList.setVisibility(View.VISIBLE);
            SimpleImagePhotoAdapter adp = new SimpleImagePhotoAdapter(con, list);
            gList.setAdapter(adp);
        }
        else
        {
            gList.setVisibility(View.GONE);
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