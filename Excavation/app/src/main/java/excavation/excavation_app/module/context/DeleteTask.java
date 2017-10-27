// Delete thread
// @author: anatolian
package excavation.excavation_app.module.context;
import java.util.List;
import excavation.excavation_app.module.common.adapter.SimpleContextSelectedAdapter;
import excavation.excavation_app.module.common.adapter.SimpleStringAdapter;
import excavation.excavation_app.module.common.adapter.SimpleTextAdapter;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleListFactory;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class DeleteTask extends BaseTask
{
    //Context con;
    //Spinner Spneast, spnnorth;
    SimpleData list;
    ProgressDialog progressDialog = null;
    SimpleTextAdapter adp;
    String east, north, photo_no, context_num, mode;
    //ProgressBar bar;
    String ip_address = "";
    List<SimpleData> ctx_list;
    //ListView listview;
    //Spinner ctx_spnr;
    /**
     * Constructor
     * @param mContext - calling context
     * @param data - data to delete
     * @param progressBar1 - loading bar
     * @param mode - deletion mode
     * @param area_east - easting
     * @param area_north - northing
     * @param Photo_no - photo number
     * @param listview - container view
     * @param ctx_spnr - spinner
     */
    public DeleteTask(Context mContext, String data, ProgressBar progressBar1, String mode,
                      String area_east, String area_north, String Photo_no, ListView listview,
                      Spinner ctx_spnr)
    {
        this.context_num = data;
        this.east = area_east;
        this.north = area_north;
        this.photo_no = Photo_no;
        this.mode = mode;
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
        //bar.setVisibility(View.VISIBLE);
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
        SimpleObjectFactory factory1 = SimpleObjectFactory.getInstance();
        list = factory1.DeleteContext(ip_address, mode, east, north, context_num, photo_no);
        SimpleListFactory factory = SimpleListFactory.getInstance();
        ctx_list = factory.getContextList(ip_address, east, north,photo_no);
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
        //bar.setVisibility(View.GONE);
        if (list.result == RESPONSE_RESULT.success)
        {
            AppConstants.temp_Context_No.remove(context_num);
            if (AppConstants.temp_Context_No != null && AppConstants.temp_Context_No.size() > 0)
            {
                SimpleContextSelectedAdapter adyt =
                        new SimpleContextSelectedAdapter(null, AppConstants.temp_Context_No, east,
                                north, photo_no, null, null);
            }
        }
        if (ctx_list != null && ctx_list.size() > 0)
        {
            SimpleStringAdapter asdad = new SimpleStringAdapter(null, ctx_list, east, north, "");
        }
        else
        {
            Toast.makeText(null, "There is no Context numbers for these areas", Toast.LENGTH_SHORT).show();
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