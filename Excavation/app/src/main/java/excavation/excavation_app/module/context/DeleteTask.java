// Delete thread
package excavation.excavation_app.module.context;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleContextSelectedAdapter;
import excavation.excavation_app.module.common.adapter.SimpleStringAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.ResponseResult;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class DeleteTask extends BaseTask
{
    private Context con;
    private SimpleData list;
    private ProgressDialog progressDialog = null;
    private String east, north, photoNo, contextNum, mode;
    private ProgressBar bar;
    private String ipAddress = "";
    private List<SimpleData> ctxList;
    private ListView listView;
    private Spinner ctxSpnr;
    /**
     * Constructor
     * @param mContext - calling context
     * @param data - data to remove
     * @param progressBar1 - status
     * @param mode - deletion mode
     * @param areaEast - easting
     * @param areaNorth - northing
     * @param photoNo - photo number
     * @param listView - container view
     * @param ctxSpnr - context spinner
     */
    public DeleteTask(Context mContext, String data, ProgressBar progressBar1, String mode,
                      String areaEast, String areaNorth, String photoNo, ListView listView,
                      Spinner ctxSpnr)
    {
        con = mContext;
        this.contextNum = data;
        this.east = areaEast;
        this.north = areaNorth;
        this.photoNo = photoNo;
        this.mode = mode;
        bar = progressBar1;
        this.listView = listView;
        this.ctxSpnr = ctxSpnr;
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
     * Run before threead
     */
    @Override
    protected void onPreExecute()
    {
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
        SimpleObjectFactory factory1 = SimpleObjectFactory.getInstance();
        list = factory1.DeleteContext(ipAddress, mode, east, north, contextNum, photoNo);
        SimpleListFactory factory = SimpleListFactory.getInstance();
        ctxList = factory.getContextList(ipAddress, east, north,photoNo);
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
        bar.setVisibility(View.GONE);
        if(list.result == ResponseResult.success)
        {
            AppConstants.tempContextNo.remove(contextNum);
            if (AppConstants.tempContextNo != null && AppConstants.tempContextNo.size() > 0)
            {
                listView.setVisibility(View.VISIBLE);
                SimpleContextSelectedAdapter adyt =
                        new SimpleContextSelectedAdapter(con, AppConstants.tempContextNo, east,
                                north, photoNo, listView, ctxSpnr);
                listView.setAdapter(adyt);
            }
            else
            {
                listView.setVisibility(View.GONE);
            }
        }
        if (ctxList != null && ctxList.size() > 0)
        {
            ctxSpnr.setVisibility(View.VISIBLE);
            SimpleStringAdapter asdad = new SimpleStringAdapter(con, ctxList);
            ctxSpnr.setAdapter(asdad);
        }
        else
        {
            Toast.makeText(con,"There is no Context numbers for these areas",
                    Toast.LENGTH_SHORT).show();
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