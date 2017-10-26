package module.sample;

import java.util.List;

import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.factory.SimpleListFactory;
import module.common.task.BaseTask;
import module.image.property.ImagePropertyBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;

import com.appenginedemo.db.DBHelper;

public class getSamplePhototTask extends BaseTask {

	Context con;
	Spinner spnMaterial, spnContext, spnSample;
	List<SimpleData> list;
	ProgressDialog progressDialog = null;
	SimpleImagePhotoAdapter adp;
	String m, north, east, cloth, gtype, contno, sno;
	String type, mode = "list", type1;
	GridView glist;
	ListView lv;
	ImagePropertyBean data1;
	String ip_address = "";
	int count;

	public getSamplePhototTask(Context activity_Sample,int count,
			GridView gridViewList, String spnnorth, String spnEast,
			String spncon, String spnSAm, String type) {
		con = activity_Sample;
		glist = gridViewList;
		north = spnnorth;
		east = spnEast;
		contno = spncon;
		sno = spnSAm;
		type1 = type;
		this.count=count;
	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return null;
	}

	@Override
	protected void onPreExecute() {

		DBHelper db = DBHelper.getInstance(con);
		db.open();
		ip_address = db.getIpAddress();
		data1=db.getImageProperty();
		db.close();
	}

	@Override
	protected Void doInBackground(String... params) {
		SimpleListFactory factory = SimpleListFactory.getInstance();
		// list=factory.getSampleList(type,mode);

		if(count==5 || count==3 && AppConstants.spnConNopos>1 && AppConstants.spnSamNopos>0){
			list = factory.getPhotoSampleList(north, east, contno, sno, type1,
					ip_address,data1.base_image_path,data1.sample_subpath);
		}
		

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		// Log.e("list",list.size()+"");
		if (list != null && list.size() > 0) {

			glist.setVisibility(View.VISIBLE);
			adp = new SimpleImagePhotoAdapter(con, list);
			glist.setAdapter(adp);

		} else {
			glist.setVisibility(View.GONE);
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

	}

	@Override
	public void release() {

	}
}
