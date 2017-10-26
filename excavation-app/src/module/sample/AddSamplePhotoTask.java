package module.sample;

import java.util.ArrayList;

import com.appenginedemo.Activity_Sample;
import com.appenginedemo.db.DBHelper;

import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import module.image.property.ImagePropertyBean;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AddSamplePhotoTask extends BaseTask {
	private Activity context;
	private SimpleData data=null;
	ProgressBar bar;
	ProgressDialog progressDialog = null;
	String album_name, user_id, cover_image, photos, albumId, mode = null,
			east, north;
	ArrayList<String> selectedItems;
	ArrayList<String> allselectedItems = new ArrayList<String>();
	int cnt = 0;
	int i = 0;
	String batch_id,tp,samno,conno,im;
	ImagePropertyBean data1;
	private Handler handler = new Handler();

	public AddSamplePhotoTask(Activity con, String spnEast, String spnnorth,
			ArrayList<String> selectedImg,String Con, String sam, String type) {
		this.context = con;
		east = spnEast;
		north = spnnorth;
		selectedItems = selectedImg;
		//bar = pbar;
		conno=Con;
		samno=sam;
		tp=type;
	}

	/*public AddSamplePhotoTask(Context con, String spnEast, String spnnorth,
			String img, String Con, String sam, String type) {
		this.context = con;
		east = spnEast;
		north = spnnorth;
		im = img;
		//bar = pbar;
		conno=Con;
		samno=sam;
		tp=type;
	}*/

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return data;
	}

	@Override
	protected void onPreExecute() {
		///bar.setVisibility(View.VISIBLE);

		/*new Thread(new Runnable() {
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
		}).start();*/
		
		  progressDialog = new ProgressDialog(context);
		  progressDialog.setCancelable(false);
		  progressDialog.setCanceledOnTouchOutside(false);
		  progressDialog.setMessage("Uploading..");
		  progressDialog.show();
		 
		// dialog.show();
	}

	@Override
	protected Void doInBackground(String... params) {
		SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
		// data = factory.getAddAlbumsPhotosData(east,north,selectedItems);
		// data =
		// factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));
	//	data = factory.AddSamplePhotosData(east, north,conno,samno,tp,im);
		
		String ip_address="";
		DBHelper db=DBHelper.getInstance(context);
		db.open();
		ip_address=db.getIpAddress();
		data1=db.getImageProperty();
		db.close();
		int j=selectedItems.size()-1;
		
		data = factory.AddSamplePhotosData(east, north,conno,samno,tp,selectedItems.get(j),ip_address,
				data1.context_subpath_3d,data1.base_image_path,data1.context_subpath,
				data1.sample_label_area_divider,data1.sample_label_context_divider,data1.sample_label_font,
				data1.sample_label_font_size,data1.sample_label_placement,data1.sample_label_sample_divider,
				data1.sample_subpath,data1.context_subpath3d1);
		//batch_id = data.id;
		/*for (int i = 1; i < selectedItems.size(); i++) {
			data = factory.AddSamplePhotosData(east, north,conno,samno,tp,selectedItems.get(i));
		}*/

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		//bar.setVisibility(View.GONE);
		
		 if (progressDialog != null && progressDialog.isShowing()) {
		 progressDialog.dismiss(); }
		 

		if (data.result == RESPONSE_RESULT.success) {
			
			context.finish();
			//AppConstants.up=1;
		/*	AppConstants.SampleselectedImg = null;
			AppConstants.SampleselectedImg = new ArrayList<String>();*/
			Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT)
					.show();
			
			Intent i = new Intent(context,
					Activity_Sample.class);
			//here changes done for putextra
			/*i.putExtra("samp_no", samno);
			i.putExtra("Context_no", conno);
			i.putExtra("type", tp);
			i.putExtra("north", north);
			i.putExtra("east", east);*/
			context.startActivity(i);
			
		} else {
			Toast.makeText(context, data.resultMsg, Toast.LENGTH_SHORT).show();
		}

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
