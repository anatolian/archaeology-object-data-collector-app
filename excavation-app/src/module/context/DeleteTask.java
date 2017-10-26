package module.context;

import java.util.List;

import module.common.adapter.SimpleContextSelectedAdapter;
import module.common.adapter.SimpleStringAdapter;
import module.common.adapter.SimpleTextAdapter;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.factory.SimpleListFactory;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.appenginedemo.db.DBHelper;

public class DeleteTask extends BaseTask {

	
	Context con;
	Spinner Spneast,spnnorth;
	SimpleData list;
	ProgressDialog progressDialog = null;
	SimpleTextAdapter adp;
	String east,north,photo_no,context_num,mode;
	ProgressBar bar;
	String ip_address="";
	List<SimpleData> ctx_list;
	ListView listview;
	Spinner ctx_spnr;
	public DeleteTask(Context mContext, String data, ProgressBar progressBar1,String mode,String area_east,String area_north,String Photo_no,ListView listview,Spinner ctx_spnr) {
		con=mContext;
		this.context_num=data;
		this.east=area_east;
		this.north=area_north;
		this.photo_no=Photo_no;
		this.mode=mode;
		bar=progressBar1;
		this.listview=listview;
		this.ctx_spnr=ctx_spnr;
	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return null;
	}
	@Override
	protected void onPreExecute() {
		 bar.setVisibility(View.VISIBLE);
		/*pprogressDialog = new ProgressDialog(con);
		rogressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();*/
		//dialog.show();
		
		 DBHelper db=DBHelper.getInstance(con);
			db.open();
			ip_address=db.getIpAddress();
			db.close();
	
		
	}

	@Override
	protected Void doInBackground(String... params) 
	{
			SimpleObjectFactory factory1 = SimpleObjectFactory.getInstance();
		
		//	Log.e("area_easting list","area_easting");
			list=factory1.DeleteContext(ip_address, mode, east, north, context_num, photo_no);
			
			SimpleListFactory factory = SimpleListFactory.getInstance();
			ctx_list = factory.getContextList(ip_address, east, north,photo_no);
	
			
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		/*if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}*/
		
		
		bar.setVisibility(View.GONE);
		
		if(list.result==RESPONSE_RESULT.success)
		{	
			AppConstants.temp_Context_No.remove(context_num);
			
			if (AppConstants.temp_Context_No!=null&&AppConstants.temp_Context_No.size()>0) {
				listview.setVisibility(View.VISIBLE);
				SimpleContextSelectedAdapter adyt=new SimpleContextSelectedAdapter(con,AppConstants.temp_Context_No, east, north, photo_no,listview,ctx_spnr);
				listview.setAdapter(adyt);
			}else{
				listview.setVisibility(View.GONE);
			}
			
		}	
		if (ctx_list!=null&&ctx_list.size()>0) {
			ctx_spnr.setVisibility(View.VISIBLE);
			SimpleStringAdapter asdad = new SimpleStringAdapter(con, ctx_list, east, north, "");
			ctx_spnr.setAdapter(asdad);
		}else{
			Toast.makeText(con,"There is no Context numbers for these areas",Toast.LENGTH_SHORT).show();
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
		
		
//		Intent i=new Intent(context,ActivityGallery.class);
//		context.startActivity(i);
	}

	@Override
	public void release() {

	}
}
