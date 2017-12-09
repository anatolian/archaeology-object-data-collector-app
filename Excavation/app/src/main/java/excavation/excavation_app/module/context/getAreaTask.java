// Get area
package excavation.excavation_app.module.context;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class GetAreaTask extends BaseTask
{
    private Context con;
    private Spinner spnEast, spnNorth;
    private List<SimpleData> list;
    private ProgressDialog progressDialog = null;
    private String m, north, east, eastId;
    ProgressBar progressBar2;
    private String ipAddress = "";
    private int count;
    /**
     * Constructor
     * @param mainActivity - calling activity
     * @param count - items
     * @param northing - area northing
     * @param areaEasting - area easting
     * @param md - md
     * @param north - northing
     * @param east - easting
     * @param id - area id
     * @param progressBar2 - progress
     */
    public GetAreaTask(Context mainActivity, int count, Spinner northing, Spinner areaEasting,
                       String md, String north, String east, String id, ProgressBar progressBar2)
    {
        con = mainActivity;
        spnEast = areaEasting;
        spnNorth = northing;
        m = md;
        this.progressBar2 = progressBar2;
        this.north = north;
        this.east = east;
        this.count = count;
        eastId = id;
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
        progressBar2.setVisibility(View.VISIBLE);
        DBHelper db = DBHelper.getInstance(con);
        db.open();
        ipAddress = db.getIPAddress();
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
        if (m.equalsIgnoreCase("n"))
        {
            list = factory.getNorthArea(eastId, ipAddress);
        }
        else
        {
            // Log.e("area_easting list","area_easting");
            list = factory.getEastArea(ipAddress);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        progressBar2.setVisibility(View.GONE);
        if (m.equalsIgnoreCase("n")) {
            SimpleData d = new SimpleData();
            d.id = "Area Northing";
            list.add(0, d);
            spnNorth.setEnabled(true);
            SimpleTextAdapter adp = new SimpleTextAdapter(con, list, "", "");
            spnNorth.setAdapter(adp);
            if (count == 1)
            {
                spnNorth.setSelection(AppConstants.activityMainSpnNorth);
            }
            else if (count == 2)
            {
                spnNorth.setSelection(AppConstants.spnNorthPos);
            }
            else if (count == 3)
            {
                spnNorth.setSelection(AppConstants.activity3DSpnNorth);
            }
        }
        else
        {
            SimpleData d = new SimpleData();
            d.id = "Area Easting";
            list.add(0, d);
            spnEast.setEnabled(true);
            SimpleTextAdapter adp = new SimpleTextAdapter(con, list, "", "");
            spnEast.setAdapter(adp);
            if (count == 1)
            {
                spnEast.setSelection(AppConstants.activityMainSpnEast);
            }
            else if (count == 2)
            {
                spnEast.setSelection(AppConstants.spnEastPos);
            }
            else if (count == 3)
            {
                spnEast.setSelection(AppConstants.activity3DSpnEast);
            }
        }
        if (north != null && north.length() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).id.equals(north))
                {
                    break;
                }
            }
            // here changes done spnMaterial.setSelection(j);
            spnNorth.setSelection(0);
        }
        if (east != null && east.length() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).id.equals(east))
                {
                    break;
                }
            }
            // here changes done spnMaterial.setSelection(j);
            spnEast.setSelection(0);
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