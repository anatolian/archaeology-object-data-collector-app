package module.image.property;

import module.common.bean.ResponseData;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.appenginedemo.ActivityImageProperty;
import com.appenginedemo.db.DBHelper;

public class ImagePropertyTask extends BaseTask{

	Context context;
	ProgressDialog pDialog;
	String ip_address;
	ImagePropertyBean data;
	DBHelper db;
	boolean flag1=false;;
	public ImagePropertyTask(Context context) {
		 
					this.context = context;
					db=DBHelper.getInstance(context);
					db.open();
					ip_address=db.getIpAddress();
					data=db.getImageProperty();
					db.close();
		
	}

	@Override
	public <T extends ResponseData> T getData(int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		
		pDialog=new ProgressDialog(context);
		pDialog.setTitle("Loading Image Property");
		pDialog.setMessage("Loading....");
		pDialog.setIndeterminate(false);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		SimpleObjectFactory factory=SimpleObjectFactory.getInstance();
	
	  
		if(flag1==false){
			
			data=factory.getImageProperty(ip_address);
			
		}else{
			
			flag1=true;
		   
		}
		
		return super.doInBackground(params);
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(pDialog!=null && pDialog.isShowing()){
			pDialog.dismiss();
		}
		if(data!=null && data.result==RESPONSE_RESULT.success && flag1==false){
			 
			boolean flag;

			
			db=DBHelper.getInstance(context);
			db.open();
		    flag=db.addImageProperty(data);
		    db.close();
			if(flag==true){
				Log.d("Table_image_property", "Data inserted successfully..");
			}else{
				Log.d("Table_image_property", "Data doesn'tinserted successfully");
			}
			
			
			/*Intent i=new Intent(context,ActivityImageProperty.class);
			
			context.startActivity(i);
			((Activity)context).finish();
			*/
			
			
						
		}/*else if(data!=null && flag1==true){
			System.out.println("second flag1"+flag1);
			Intent i=new Intent(context,ActivityImageProperty.class);
			
			context.startActivity(i);
			((Activity)context).finish();
		}
		*/
		
		else{
			Log.e("Table Image Property","Data is empty...");
			//Toast.makeText(context, "Data is empty", 5000).show();
		}
	}

	
	@Override
	public void release() {
		// TODO Auto-generated method stub
		System.gc();
	} 

}
