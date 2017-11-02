// Add multiple photos thread
package excavation.excavation_app.module.bil3d;
import java.util.ArrayList;
import excavation.excavation_app.com.appenginedemo.Activity3d;
import excavation.excavation_app.module.common.bean.SimpleData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.factory.SimpleObjectFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import excavation.excavation_app.module.image.property.ImagePropertyBean;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.appenginedemo.R;
import excavation.excavation_app.com.appenginedemo.db.DBHelper;
public class AddMultiPhotoTask extends BaseTask
{
    private Context context;
    private SimpleData data;
    private ProgressBar bar;
    private ProgressDialog progressDialog = null;
    private String east, north;
    private ArrayList<String> selectedItems;
    private int i = 0, j = 1;
    private String batchId;
    private Handler handler = new Handler();
    /**
     * Constructor
     * @param con - calling context
     * @param spnEast - easting
     * @param spnNorth - northing
     * @param selectedImg - image
     * @param pbar - progress
     */
    public AddMultiPhotoTask(Context con, String spnEast, String spnNorth,
                             ArrayList<String> selectedImg, ProgressBar pbar)
    {
        this.context = con;
        east = spnEast;
        north = spnNorth;
        selectedItems = selectedImg;
        bar = pbar;
    }

    /**
     * Get data
     * @param pos - position
     * @return Returns null
     */
    @SuppressWarnings("unchecked")
    public SimpleData getData(int pos)
    {
        return data;
    }

    /**
     * Run before thread
     */
    @Override
    protected void onPreExecute()
    {
        bar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            /**
             * Run thread
             */
            public void run()
            {
                while (i < 100)
                {
                    i++;
                    handler.post(new Runnable() {
                        /**
                         * Run thread
                         */
                        @Override
                        public void run()
                        {
                            bar.setProgress(i);
                        }

                    });
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
        DBHelper db = DBHelper.getInstance(context);
        db.open();
        String ipAddress = db.getIpAddress();
        ImagePropertyBean data1 = db.getImageProperty();
        db.close();
        data = factory.getAddAlbumsPhotosData(east, north, selectedItems.get(0),"", ipAddress,
                data1.baseImagePath, data1.contextSubpath3d);
        batchId = data.id;
        Log.e("batch_id",batchId + " " + data.resultMsg + " ");
        while ( j < selectedItems.size())
        {
            if (data.result == RESPONSE_RESULT.success)
            {
                Log.e("if part",j + "");
                data = factory.getAddAlbumsPhotosData(east, north,selectedItems.get(j), batchId,
                        ipAddress, data1.baseImagePath, data1.contextSubpath3d);
            }
            else
            {
                for (int h = 1; h <= 3; h++)
                {
                    Log.e("MAX_ATTEMPTS" , h + " j=" + j);
                    if (h == 3)
                    {
                        break;
                    }
                    else
                    {
                        data = factory.getAddAlbumsPhotosData(east, north, selectedItems.get(j),
                                batchId, ipAddress, data1.baseImagePath, data1.contextSubpath3d);
                        if (data.result == RESPONSE_RESULT.success)
                        {
                            break;
                        }
                    }
                }
            }
            j++;
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
        bar.setVisibility(View.GONE);
        if (AppConstants.internet == 0)
        {
            if (data.result == RESPONSE_RESULT.success)
            {
                AppConstants.up = 1;
                AppConstants.activity3dSpnNorth = 0;
                AppConstants.activity3dSpnEast = 0;
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                // Setting Dialog Title
                alertDialog.setTitle("Uploaded Successfully");
                // Setting Dialog Message
                alertDialog.setMessage("your 3d photo batch was uploaded as " + batchId);
                // Setting alert dialog icon
                alertDialog.setIcon( R.drawable.logo_small);
                // Setting OK Button
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK",
                        new DialogInterface.OnClickListener() {
                    /**
                     * User pressed ok
                     * @param dialog - dialog window
                     * @param which - selection
                     */
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        AppConstants.selectedImg = null;
                        Intent i = new Intent(context, Activity3d.class);
                        context.startActivity(i);
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                // Setting Dialog Title
                alertDialog.setTitle("Upload Failed");
                // Setting Dialog Message
                alertDialog.setMessage("Error: " + data.resultMsg);
                alertDialog.setIcon( R.drawable.logo_small);
                // Setting OK Button
                alertDialog.setButton(Dialog.BUTTON_POSITIVE,"OK",
                        new DialogInterface.OnClickListener() {
                    /**
                     * User pressed ok
                     * @param dialog - alert
                     * @param which - selection
                     */
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
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