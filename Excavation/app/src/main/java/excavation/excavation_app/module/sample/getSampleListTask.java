// Get sample list
package excavation.excavation_app.module.sample;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class GetSampleListTask extends BaseTask
{
    private Context con;
    List<SimpleData> list;
    private ProgressDialog progressDialog = null;
    private String m, cloth, gType, contNo, sno;
    private String ipAddress = "", ff;
    private ListView lv;
    private ProgressBar progressBar2;
    /**
     * Constructor
     * @param activity - calling activity
     * @param md - md
     * @param material - material type
     * @param type - item type
     * @param conNo - context number
     * @param samNo - sample
     * @param conn - context
     * @param sam - sample
     * @param progressBar2 - progress bar
     */
    public GetSampleListTask(Context activity, String md, String material, String type,
                             String conNo, String samNo, String conn, String sam,
                             ProgressBar progressBar2)
    {
        con = activity;
        m = md;
        cloth = material;
        gType = type;
        contNo = conNo;
        sno = samNo;
        contNo = conn;
        sno = sam;
        this.progressBar2 = progressBar2;
    }
    /**
     * Constructor
     * @param activitySample - calling activity
     * @param clothMaterial - cloth type
     * @param md - md
     * @param spnMaterial2 - material
     * @param type2 - object type
     * @param conNo - context
     * @param samNo - sample
     * @param act - act
     * @param progressBar2 - status
     */
    public GetSampleListTask(Context activitySample, ListView clothMaterial, String md,
                             String spnMaterial2, String type2, String conNo, String samNo,
                             String act, ProgressBar progressBar2)
    {
        con = activitySample;
        lv = clothMaterial;
        m = md;
        this.progressBar2 = progressBar2;
        cloth = spnMaterial2;
        gType = type2;
        contNo = conNo;
        sno = samNo;
        ff = act;
    }

    /**
     * Get sample
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
        if (m.equalsIgnoreCase("m"))
        {
            list = factory.getSampleList("material", m, ipAddress);
        }
        else if (m.equalsIgnoreCase("cn"))
        {
            list = factory.getSampleList("context", m, ipAddress);
        }
        else if (m.equalsIgnoreCase("s"))
        {
            list = factory.getSampleList("sample", m, ipAddress);
        }
        else
        {
            list = factory.getSampleList("photograph", m, ipAddress);
        }
        return null;
    }

    /**
     * Run after thread
     * @param result - Nothing
     */
    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
        progressBar2.setVisibility(View.GONE);
        if (m.equalsIgnoreCase("m"))
        {
            if (!(ff != null && ff.length() > 0))
            {
                SimpleData d = new SimpleData();
                d.id = "Cloth Material";
                list.add(0, d);
            }

        }
        else if (m.equalsIgnoreCase("cn"))
        {
            if (lv == null)
            {
                SimpleData d = new SimpleData();
                d.id = "Context Number";
                list.add(0, d);
            }
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
            if (lv != null)
            {
                // here changes done
                SimpleTextAdapter adp = new SimpleTextAdapter(con, list, "1", ff);
                lv.setAdapter(adp);
            }
        }
        if (cloth != null && cloth.length() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (cloth.equals(list.get(i).id))
                {
                    break;
                }
            }
        }
        if (gType != null && gType.length() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (gType.equals(list.get(i).id))
                {
                    break;
                }
            }
        }
        if (contNo != null && contNo.length() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (contNo.equals(list.get(i).id))
                {
                    break;
                }
            }
            // here changes done spnMaterial.setSelection(j);
        }
        if (sno != null && sno.length() > 0)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (sno.equals(list.get(i).id))
                {
                    break;
                }
            }
        }
    }

    /**
     * Thread stopped
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