package module.context;

import java.util.ArrayList;

import module.common.adapter.SimpleTextAdapter;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import module.image.property.ImagePropertyBean;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.appenginedemo.MainActivity;
import com.appenginedemo.db.DBHelper;

public class addSinglePhotoTask extends BaseTask {

	
	Context con;
	Spinner Spneast;
	SimpleData list;
	ProgressDialog progressDialog = null;
	SimpleTextAdapter adp;
	String north1,east1,img,photo_id;
	String ip_address="",camval;
	ArrayList<String>ctx_no=null;
	ImagePropertyBean data1;
	/*public addSinglePhotoTask(Context activityCamera, String north,
			String east, String imagePath,String ctx) {
		con=activityCamera;
		north1=north;
		east1=east;
		img=imagePath;
		ctx_no=ctx;
		
	}*/
	public addSinglePhotoTask(Context activityCamera, String north,
			String east, String imagePath,ArrayList<String> temp_Context_No,String photoid,
			String camval1) {
		con=activityCamera;
		north1=north;
		east1=east;
		img=imagePath;
		ctx_no=temp_Context_No;
		photo_id=photoid;
		camval=camval1;
	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return null;
	}
	@Override
	protected void onPreExecute() {
		// bar.setVisibility(View.VISIBLE);
		progressDialog = new ProgressDialog(con);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		//dialog.show();
		
	
		DBHelper db=DBHelper.getInstance(con);
		db.open();
		ip_address=db.getIpAddress();
		data1=db.getImageProperty();
		db.close();
	}

	@Override
	protected Void doInBackground(String... params) 
	{
		SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
		
		if(photo_id!=null && photo_id.length()>0)
		{
			list=factory.addSingleimg(north1,east1,img,ctx_no.get(0),ip_address,photo_id,data1.base_image_path,data1.context_subpath);
		}
		else if (ctx_no!=null && ctx_no.size()>0) {
			
			list=factory.addSingleimg(north1,east1,img,ctx_no.get(0),ip_address,"",data1.base_image_path,data1.context_subpath);
		}
		else
		{
			list=factory.addSingleimg(north1,east1,img,"",ip_address,"",data1.base_image_path,data1.context_subpath);
		}
		/*for(int i=0;i<ctx_no.size();i++)
		{
			list=factory.addSingleimg(north1,east1,img,ctx_no.get(i),ip_address);
		}*/
			
		
	
		// data = factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		if(list.result==RESPONSE_RESULT.success)
		{
			AppConstants.temp_Context_No=null;
			
			Toast.makeText(con,"Uploaded Successfully",Toast.LENGTH_LONG).show();
			//Log.e("postttt","post");
			Intent i = new Intent(con,MainActivity.class);
			i.putExtra("pic", "camval");
			i.putExtra("north", north1);
			i.putExtra("east", east1);
			i.putExtra("ctx", ctx_no);
			/*i.putExtra("y", "yes");
		//	i.putExtra("imagePath", imagePath);
			i.putExtra("north", north1);
			i.putExtra("east", east1);
			i.putExtra("ctx", ctx_no);*/
			con.startActivity(i);
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
		
		/*if(list.result==RESPONSE_RESULT.success)
		{
			AppConstants.temp_Context_No=null;
			Toast.makeText(con,"Uploaded Successfully",Toast.LENGTH_LONG).show();
			
			Intent i = new Intent(con,
					MainActivity.class);
			/*i.putExtra("y", "yes");
		//	i.putExtra("imagePath", imagePath);
			i.putExtra("north", north1);
			i.putExtra("east", east1);
			i.putExtra("ctx", ctx_no);
			con.startActivity(i);
		}*/
//		Intent i=new Intent(context,ActivityGallery.class);
//		context.startActivity(i);
	}

	@Override
	public void release() {

	}
}
