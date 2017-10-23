package module.bil3d;

import java.util.ArrayList;

import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import module.image.property.ImagePropertyBean;
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
import android.widget.Toast;

import com.appenginedemo.Activity_3d;
import com.appenginedemo.R;
import com.appenginedemo.db.DBHelper;

public class AddMultiPhotoTask extends BaseTask {
	private Context context;
	private SimpleData data;
	ProgressBar bar;
	ProgressDialog progressDialog = null;
	String album_name, user_id, cover_image, photos, albumId, mode = null,
			east, north;
	ArrayList<String> selectedItems;
	ArrayList<String> allselectedItems = new ArrayList<String>();
	int cnt = 0;
	int i=0,j=1,MAX_ATTEMPTS=3;
	String batch_id;
	private Handler handler = new Handler();
	ImagePropertyBean data1;

	public AddMultiPhotoTask(Context con, String string,
			ArrayList<String> selectedItems) {
		this.context = con;
		this.album_name = string;
		this.selectedItems = selectedItems;
		
		//Log.e("ALBUM_NM", string + "");
	}

	public AddMultiPhotoTask(Context con, String spnEast, String spnnorth,
			ArrayList<String> selectedImg, ProgressBar pbar) {
		this.context = con;
		east = spnEast;
		north = spnnorth;
		selectedItems = selectedImg;
		bar = pbar;
	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return data;
	}

	@Override
	protected void onPreExecute() {
		bar.setVisibility(View.VISIBLE);

		new Thread(new Runnable() {
			public void run() {
				while (i < 100) {
					i++;

					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							bar.setProgress(i);
						}

					});

					try {

						Thread.sleep(50);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		/*
		 * progressDialog = new ProgressDialog(context);
		 * progressDialog.setCancelable(false);
		 * progressDialog.setCanceledOnTouchOutside(false);
		 * progressDialog.show();
		 */
		// dialog.show();
	}

	@Override
	protected Void doInBackground(String... params) {
		SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
		// data = factory.getAddAlbumsPhotosData(east,north,selectedItems);
		// data =
		// factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));
		
		String ip_address="";
		DBHelper db=DBHelper.getInstance(context);
		db.open();
		ip_address=db.getIpAddress();
		data1=db.getImageProperty();
		db.close();
		
		data = factory.getAddAlbumsPhotosData(east,north,selectedItems.get(0),"",ip_address,data1.base_image_path,
				data1.context_subpath_3d);
		
		batch_id = data.id;
		
		Log.e("batch_id",batch_id+" "+data.resultMsg+" ");
		
		
	
            
         
               
            	while ( j < selectedItems.size()) {
		        			if (data.result==RESPONSE_RESULT.success){
		        				Log.e("if part",j+"");
		        				data = factory.getAddAlbumsPhotosData(east, north,selectedItems.get(j),batch_id,ip_address,
		        						data1.base_image_path,data1.context_subpath_3d);
		        			}else{
		        			
		        			
		        				for (int h = 1;h <= MAX_ATTEMPTS; h++) {
		        					Log.e("MAX_ATTEMPTS" , h + " j="+j);
		        					  if (h== MAX_ATTEMPTS) {
		        		                    break;
		        		                }else{
		        		                	data = factory.getAddAlbumsPhotosData(east, north,selectedItems.get(j),batch_id,ip_address,
		        		                			data1.base_image_path,data1.context_subpath_3d);
		        		                	if (data.result==RESPONSE_RESULT.success){
		        		                		break;
		        		                	}
		        		                
		        		                }
		        				}
        			
		        			}
        			
		        			j++;
            	}	
 
		
//		for (int i = 1; i < selectedItems.size(); i++) {
//			
//		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		bar.setVisibility(View.GONE);
		/*
		 * if (progressDialog != null && progressDialog.isShowing()) {
		 * progressDialog.dismiss(); }
		 */
	if(AppConstants.internet==0)
	{
			if (data.result == RESPONSE_RESULT.success) {
			AppConstants.up=1;
			
			AppConstants.acvity_3dSpnNorth=0;
			AppConstants.activity_3dSpnEast=0;
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();

			// Setting Dialog Title
			alertDialog.setTitle("Uploaded Successfully");

			// Setting Dialog Message
			alertDialog.setMessage("your 3d photo batch was uploaded as "+batch_id);
				// Setting alert dialog icon
				alertDialog.setIcon( R.drawable.logo_small);

			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
					AppConstants.selectedImg = null;
					Intent i = new Intent(context, Activity_3d.class);
					context.startActivity(i);
				}
			});

			// Showing Alert Message
			alertDialog.show();
			// Intent i=
		} else {
			
	
		
			
			
			
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();

			// Setting Dialog Title
			alertDialog.setTitle("Upload Failed");

			// Setting Dialog Message
			alertDialog.setMessage("Error: "+data.resultMsg);
				alertDialog.setIcon( R.drawable.logo_small);

			// Setting OK Button
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
//					AppConstants.selectedImg = null;
//					Intent i = new Intent(context, Activity_3d.class);
//					context.startActivity(i);
					
				}
			});

			// Showing Alert Message
			alertDialog.show();
			//Toast.makeText(context, data.resultMsg, Toast.LENGTH_SHORT).show();
		}
	}
	/*else
	{
		Toast.makeText(context,"network error", Toast.LENGTH_SHORT).show();
	}*/
	
		// if (data.result == RESPONSE_RESULT.success) {
		// Intent intent = new Intent(context, A.class);
		//
		// if (albumId!=null&&albumId.length()>0) {
		// intent.putExtra("data",albumId);
		// }else{
		// intent.putExtra("data",data.id);
		// }
		//
		// context.startActivity(intent);
		// } else {
		// Toast.makeText(context, data.resultMsg, Toast.LENGTH_SHORT).show();
		// }

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
