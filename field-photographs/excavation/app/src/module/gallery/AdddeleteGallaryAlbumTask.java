package module.gallery;

import java.util.ArrayList;

import module.common.bean.SimpleData;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

public class AdddeleteGallaryAlbumTask extends BaseTask 
{
	private Context context;
	private SimpleData data;
	ProgressBar bar;
	ProgressDialog progressDialog = null;
	String album_name,user_id,cover_image,photos,albumId,mode=null;
	ArrayList<String> selectedItems;
	ArrayList<String> allselectedItems=new ArrayList<String>();

	public AdddeleteGallaryAlbumTask(
			Context con, String string,
			ArrayList<String> selectedItems) 
	{
		this.context=con;
		this.album_name=string;
		this.selectedItems=selectedItems;
		//Log.e("ALBUM_NM",string+"");
	}
	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return data;
	}
	@Override
	protected void onPreExecute() {
		// bar.setVisibility(View.VISIBLE);
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		//dialog.show();
	}

	@Override
	protected Void doInBackground(String... params) 
	{
		SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
		for (int i = 0; i < selectedItems.size(); i++) 
		{
		//data = factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
//		if (data.result == RESPONSE_RESULT.success) {
//			Intent intent = new Intent(context, ActivityGallery.class);
//			
//			if (albumId!=null&&albumId.length()>0) {
//				intent.putExtra("data",albumId);
//			}else{
//				intent.putExtra("data",data.id);
//			}
//		
//			context.startActivity(intent);
//		} else {
//			Toast.makeText(context, data.resultMsg, Toast.LENGTH_SHORT).show();
//		}

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
