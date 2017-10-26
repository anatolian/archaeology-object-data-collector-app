package module.sample;

import java.util.List;

import module.common.adapter.SimpleTextAdapter;
import module.common.bean.SimpleData;
import module.common.http.factory.SimpleListFactory;
import module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.appenginedemo.db.DBHelper;

public class getSampleListTask1 extends BaseTask {

	Context con;

	String spnorth;
	String conn;
	String speast;
	String listing_type;
	String mode;
	String sample_no,ip_address;
	ProgressBar progressBar2;
	TextView clothe_material1;
	ListView clothe_material;
	List<SimpleData> data;

	public getSampleListTask1(Context activity, String spnorth, String conn,
			String speast, String listing_type, String mode, String sample_no,
			ProgressBar progressBar2, TextView clothe_material1,ListView clothe_material) {
		
		con = activity;

		this.spnorth = spnorth;
		this.conn = conn;
		this.speast = speast;
		this.listing_type = listing_type;
		this.mode = mode;
		this.sample_no = sample_no;
		this.progressBar2 = progressBar2;
		this.clothe_material1 = clothe_material1;
		this.clothe_material = clothe_material;

	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return null;
	}

	@Override
	protected void onPreExecute() {
		progressBar2.setVisibility(View.VISIBLE);
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
		 data=factory.getMaterial(spnorth,conn, speast,listing_type,mode,sample_no,ip_address);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		/*
		 * if (progressDialog != null && progressDialog.isShowing()) {
		 * progressDialog.dismiss(); }
		 */
		progressBar2.setVisibility(View.GONE);
		
		if(data!=null&&data.size()>0){
			if(data.get(1).material!=null&&data.get(1).material.length()>0){
				clothe_material1.setVisibility(View.VISIBLE);
				clothe_material.setVisibility(View.GONE);
				clothe_material1.setText(data.get(1).material);
			}else{
				
			}
		}

	}

	@Override
	protected void onCancelled(Void result) {

		release();
		super.onCancelled(result);
		// bar.setVisibility(View.INVISIBLE);
//		if (progressDialog != null && progressDialog.isShowing()) {
//			progressDialog.dismiss();
//		}

		// Intent i=new Intent(context,ActivityGallery.class);
		// context.startActivity(i);
	}

	@Override
	public void release() {

	}
}
