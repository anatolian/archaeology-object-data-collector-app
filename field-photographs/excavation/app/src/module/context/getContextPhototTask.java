package module.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import module.common.application.ApplicationHandler;
import module.common.bean.SimpleData;
import module.common.http.factory.SimpleListFactory;
import module.common.task.BaseTask;
import module.sample.SimpleImagePhotoAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.appenginedemo.db.DBHelper;

public class getContextPhototTask extends BaseTask {

	Context con;
	Spinner spnMaterial, spnContext, spnSample;
	List<SimpleData> list;
	ProgressDialog progressDialog = null;
	SimpleImagePhotoAdapter adp;
	String m, north, east, cloth, gtype, contno, sno;
	String type, mode = "list", type1;
	GridView glist;
	ListView lv;
	String ip_address = "";
	ArrayList<String> contextttno = null;;
	ImageView imagephoto;
	ApplicationHandler apphand = ApplicationHandler.getInstance();

	public getContextPhototTask(Context mainActivity, ImageView imgphoto,
			String spnEast, String spnnorth, ArrayList<String> temp_Context_No,Spinner spnContext) {
		con = mainActivity;
		imagephoto = imgphoto;
		north = spnnorth;
		east = spnEast;
		contextttno = temp_Context_No;
		this.spnContext=spnContext;
		apphand = ApplicationHandler.getInstance();
	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return null;
	}

	@Override
	protected void onPreExecute() {
		// bar.setVisibility(View.VISIBLE);
		/*
		 * progressDialog = new ProgressDialog(con);
		 * progressDialog.setCancelable(false);
		 * progressDialog.setCanceledOnTouchOutside(false);
		 * progressDialog.show();
		 */
		// dialog.show();

		DBHelper db = DBHelper.getInstance(con);
		db.open();
		ip_address = db.getIpAddress();
		db.close();
	}

	@Override
	protected Void doInBackground(String... params) {
		SimpleListFactory factory = SimpleListFactory.getInstance();
		// list=factory.getSampleList(type,mode);
			if (contextttno!=null&&contextttno.size()>0) {
				list = factory.getContextPhoto(north, east, contextttno.get(0),ip_address);
			}else{
				list = factory.getContextPhoto(north, east,null,ip_address);
			}
		
		

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		/*
		 * if (progressDialog != null && progressDialog.isShowing()) {
		 * progressDialog.dismiss(); }
		 */

		/*
		 * AppConstants.SampleselectedImg=null;
		 * AppConstants.SampleselectedImg=new ArrayList<String>();
		 */

		// Log.e("list",list.size()+"");
		if (list != null && list.size() > 0) {

			imagephoto.setImageBitmap(apphand.decodeFile(new File(
					list.get(0).img)));
			

		} else {

			// Toast.makeText(con,"NO DATA FOUND",Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onCancelled(Void result) {

		release();
		super.onCancelled(result);
		// bar.setVisibility(View.INVISIBLE);
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		// Intent i=new Intent(context,ActivityGallery.class);
		// context.startActivity(i);
	}

	@Override
	public void release() {

	}
}
