// Get sample list
// @author: sample list
package excavation.excavation_app.module.sample;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class getSampleListTask extends BaseTask
{
    List<SimpleData> list;
    List<SimpleData> material;
    ProgressDialog progressDialog = null;
    SimpleTextAdapter adp;
    String m, north, east, cloth, gtype, contno, sno, co, sn;
    String type, mode = "list";
    String ip_address = "", ff, img;
    int count;
    /**
     * Constructor
     * @param activity - calling activity
     * @param count - item count
     * @param spnClothmaterial - cloth type
     * @param md - md
     * @param material - material type
     * @param type - item type
     * @param conNo - context number
     * @param SamNo - sample
     * @param speast - easting
     * @param spnorth - northing
     * @param conn - context
     * @param sam - sample
     * @param progressBar2 - progress bar
     */
    public getSampleListTask(Context activity, int count, Spinner spnClothmaterial, String md,
                             String material, String type, String conNo, String SamNo, String speast,
                             String spnorth, String conn, String sam, ProgressBar progressBar2)
    {
        this.count = count;
        m = md;
        cloth = material;
        gtype = type;
        contno = conNo;
        sno = SamNo;
        east = speast;
        north = spnorth;
        co = conn;
        sn = sam;
    }

    /**
     * Constructor
     * @param activity_Sample - calling activity
     * @param listViewContext - calling context
     * @param md - md
     * @param east - easting
     * @param north - northing
     * @param imagePath - image location
     * @param progressBar2 - progress bar
     */
    public getSampleListTask(Context activity_Sample, ListView listViewContext, String md, String east,
                             String north, String imagePath, ProgressBar progressBar2)
    {
        m = md;
        this.east = east;
        this.north = north;
        img = imagePath;
    }

    /**
     * Constructor
     * @param activity_Sample - calling activity
     * @param clothmaterial - cloth material
     * @param md - md
     * @param spnmaterial2 - material
     * @param type2 - type
     * @param conNo - context number
     * @param samNo - sample number
     * @param act - act
     * @param progressBar2 - status
     */
    public getSampleListTask(Context activity_Sample, ListView clothmaterial, String md,
                             String spnmaterial2, String type2, String conNo, String samNo,
                             String act, ProgressBar progressBar2)
    {
        m = md;
        cloth = spnmaterial2;
        gtype = type2;
        contno = conNo;
        sno = samNo;
        ff = act;
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
        if (m.equalsIgnoreCase("m"))
        {
            list = factory.getSampleList("material", "list", m, ip_address, "",
                    "", "");
        }
        else if (m.equalsIgnoreCase("cn"))
        {
            list = factory.getSampleList("context", "list", m, ip_address, east, north, "");
        }
        else if (m.equalsIgnoreCase("s"))
        {
            list = factory.getSampleList("sample", "list", m, ip_address, east, north, co);
        }
        else
        {
            list = factory.getSampleList("photograph", "list", m, ip_address, "", "", "");
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
        if (m.equalsIgnoreCase("m"))
        {
            if (!(ff != null && ff.length() > 0)) {
                SimpleData d = new SimpleData();
                d.id = "Cloth Material";
                list.add(0, d);
            }
        }
        else if (m.equalsIgnoreCase("cn"))
        {
            SimpleData d = new SimpleData();
            d.id = "Context Number";
            list.add(0, d);
        }
        else if (m.equalsIgnoreCase("s"))
        {
            SimpleData d = new SimpleData();
            d.id = "Sample Number";
            list.add(0, d);
        }
        else
        {
            SimpleData d = new SimpleData();
            d.id = "Type";
            list.add(0, d);
        }
        if (list != null && list.size() > 0)
        {
            //here changes done
            adp = new SimpleTextAdapter(null, list, "1", ff);
        }
        else
        {
            adp = new SimpleTextAdapter(null, list, "", "");
        }
        if (cloth != null && cloth.length() > 0)
        {
            int j = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (cloth.equals(list.get(i).id))
                {
                    j = i;
                    break;
                }
            }
        }
        if (gtype != null && gtype.length() > 0)
        {
            int j = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (gtype.equals(list.get(i).id))
                {
                    j = i;
                    break;
                }
            }
        }
        if (contno != null && contno.length() > 0)
        {
            int j = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (contno.equals(list.get(i).id))
                {
                    j = i;
                    break;
                }
            }
            // here changes done spnMaterial.setSelection(j);
        }
        if (sno != null && sno.length() > 0)
        {
            int j = 0;
            for (int i = 0; i < list.size(); i++)
            {
                if (sno.equals(list.get(i).id))
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