package module.all;

import java.util.List;

import com.appenginedemo.db.DBHelper;

import module.common.http.factory.SimpleListFactory;
import module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

public class GetAllImageTask extends BaseTask {
	Context context;
	GridView gridView_category;
	ProgressDialog progressDialog;
	List<ImageBean> list;
	String query,key_secrete,name;
	ListView menu_list;
	String flag;
	
	
	public GetAllImageTask(Context con, GridView gridview) {
		// TODO Auto-generated constructor stub
		context=con;
		this.gridView_category=gridview;
	}

	@SuppressWarnings("unchecked")
	public ImageBean getData(int pos)
	{
		return list.get(pos);
	}

	@Override
	protected void onPreExecute() 
	{
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}
	@Override
	protected Void doInBackground(String... params) 
	{
		SimpleListFactory factory = SimpleListFactory.getInstance();
		String ip_address="";
		DBHelper db=DBHelper.getInstance(context);
		db.open();
		ip_address=db.getIpAddress();
		db.close();
		list=factory.getImage(ip_address);
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);
		if (progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
		if(list!=null && list.size()>0)
		{
			AllImageAdapter adapter=new AllImageAdapter(context,list);
			gridView_category.setAdapter(adapter);
		}
		
	}
	@Override
	public void release() {

	}

	
}