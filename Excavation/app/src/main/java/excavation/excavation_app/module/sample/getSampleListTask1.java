// Get sample list
package excavation.excavation_app.module.sample;
import java.util.List;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class GetSampleListTask1 extends BaseTask
{
    private Context con;
    private String spNorth, conn, spEast, listingType, mode, sampleNo, ipAddress;
    private ProgressBar progressBar2;
    private TextView clothMaterial1;
    private ListView clothMaterial;
    List<SimpleData> data;
    /**
     * Constructor
     * @param activity - calling activity
     * @param spNorth - northing
     * @param conn - context
     * @param spEast - easting
     * @param listingType - listing type
     * @param mode - search mode
     * @param sampleNo - sample
     * @param progressBar2 - status
     * @param clothMaterial1 - material 1
     * @param clothMaterial - material 2
     */
    public GetSampleListTask1(Context activity, String spNorth, String conn, String spEast,
                              String listingType, String mode, String sampleNo,
                              ProgressBar progressBar2, TextView clothMaterial1,
                              ListView clothMaterial)
    {
        con = activity;
        this.spNorth = spNorth;
        this.conn = conn;
        this.spEast = spEast;
        this.listingType = listingType;
        this.mode = mode;
        this.sampleNo = sampleNo;
        this.progressBar2 = progressBar2;
        this.clothMaterial1 = clothMaterial1;
        this.clothMaterial = clothMaterial;
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
        progressBar2.setVisibility(View.VISIBLE);
        DBHelper db = DBHelper.getInstance(con);
        db.open();
        ipAddress = db.getIpAddress();
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
        data = factory.getMaterial(spNorth, conn, spEast, listingType, mode, sampleNo, ipAddress);
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
        progressBar2.setVisibility(View.GONE);
        if (data != null && data.size() > 0)
        {
            if (data.get(1).material != null && data.get(1).material.length() > 0)
            {
                clothMaterial1.setVisibility(View.VISIBLE);
                clothMaterial.setVisibility(View.GONE);
                clothMaterial1.setText(data.get(1).material);
            }
        }
    }

    /**
     * Thread cancelled
     * @param result - Nothing
     */
    @Override
    protected void onCancelled(Void result)
    {
        release();
        super.onCancelled(result);
    }

    /**
     * Release thread
     */
    @Override
    public void release()
    {
    }
}