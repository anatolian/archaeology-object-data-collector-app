package excavation.excavation_app.module.context;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class getAreaTask extends BaseTask
{
    List<SimpleData> list;
    ProgressDialog progressDialog = null;
    SimpleTextAdapter adp;
    String m, north, east, eastid;
    String ip_address = "";
    int flag = 0;
    int count;
    /**
     * Constructor
     * @param mainActivity - calling activity
     * @param count - item count
     * @param Northing - northing
     * @param areaEasting - easting
     * @param md - md
     * @param north - northing2
     * @param east - easting2
     * @param id - item id
     * @param progressBar2 - status
     * @param flag - mode
     */
    public getAreaTask(Context mainActivity, int count, Spinner Northing, Spinner areaEasting,
                       String md, String north, String east, String id, ProgressBar progressBar2, int flag)
    {
        m = md;
        this.north = north;
        this.east = east;
        this.flag = flag;
        this.count = count;
        eastid = id;
    }

    /**
     * Return null
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
            list = factory.getNorthArea("area_northing", eastid, ip_address);
        }
        else
        {
            list = factory.getEastArea("area_easting", ip_address);
        }
        return null;
    }

    /**
     * Run after thread finished
     * @param result - nothing
     */
    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        if (m.equalsIgnoreCase("n"))
        {
            SimpleData d = new SimpleData();
            d.id = "Area Northing";
            list.add(0, d);
            adp = new SimpleTextAdapter(null, list, "", "");
        }
        else
        {
            SimpleData d = new SimpleData();
            d.id = "Area Easting";
            list.add(0, d);
            adp = new SimpleTextAdapter(null, list, "", "");
        }
        if (north != null && north.length() > 0)
        {
            int j = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).id.equals(north))
                {
                    j = i;
                    break;
                }
            }
        }
        if (east != null && east.length() > 0)
        {
            int j = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).id.equals(east))
                {
                    j = i;
                    break;
                }
            }
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