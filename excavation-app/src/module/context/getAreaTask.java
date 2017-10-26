package module.context;

import java.util.List;

import module.common.adapter.SimpleTextAdapter;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.factory.SimpleListFactory;
import module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.appenginedemo.db.DBHelper;

public class getAreaTask extends BaseTask {

	Context con;
	Spinner Spneast, spnnorth;
	List<SimpleData> list;
	ProgressDialog progressDialog = null;
	SimpleTextAdapter adp;
	String m, north, east, eastid;
	ProgressBar progressBar2;
	String ip_address = "";
	int flag = 0;
	int count;

	public getAreaTask(Context mainActivity, int count, Spinner Northing,
			Spinner areaEasting, String md, String north, String east,
			String id, ProgressBar progressBar2, int flag) {
		con = mainActivity;
		Spneast = areaEasting;
		spnnorth = Northing;
		m = md;
		this.progressBar2 = progressBar2;
		this.north = north;
		this.east = east;
		this.flag = flag;
		this.count = count;
		eastid = id;
	}

	@SuppressWarnings("unchecked")
	public SimpleData getData(int pos) {
		return null;
	}

	@Override
	protected void onPreExecute() {
		progressBar2.setVisibility(View.VISIBLE);

		/*
		 * pprogressDialog = new ProgressDialog(con);
		 * rogressDialog.setCancelable(false);
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

		if (m.equalsIgnoreCase("n")) {
			// Log.e("area_northing list","area_northing");
			list = factory.getNorthArea("area_northing", eastid, ip_address);
		} else {

			// Log.e("area_easting list","area_easting");
			list = factory.getEastArea("area_easting", ip_address);
		}

		// data =
		// factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		progressBar2.setVisibility(View.GONE);
		/*
		 * if (progressDialog != null && progressDialog.isShowing()) {
		 * progressDialog.dismiss(); }
		 */
		// Log.e("List",list.size()+"");
		// Log.e("north", north + "");
		// Log.e("east",east+"");

		if (m.equalsIgnoreCase("n")) {
			SimpleData d = new SimpleData();
			d.id = "Area Northing";
			list.add(0, d);
			spnnorth.setEnabled(true);
			adp = new SimpleTextAdapter(con, list, "", "");
			spnnorth.setAdapter(adp);
			if (count == 1) {
				spnnorth.setSelection(AppConstants.activity_mainSpnNorth);
			} else if (count == 2) {
				spnnorth.setSelection(AppConstants.spnNorthpos);
			} else if (count == 3) {
				spnnorth.setSelection(AppConstants.acvity_3dSpnNorth);
			}

		} else {
			SimpleData d = new SimpleData();
			d.id = "Area Easting";
			list.add(0, d);
			Spneast.setEnabled(true);
			adp = new SimpleTextAdapter(con, list, "", "");
			Spneast.setAdapter(adp);
			if (count == 1) {
				Spneast.setSelection(AppConstants.activity_mainSpnEast);
			} else if (count == 2) {
				Spneast.setSelection(AppConstants.spnEastpos);
			} else if (count == 3) {
				Spneast.setSelection(AppConstants.activity_3dSpnEast);
			}

		}
		// if(list!=null && list.size()>0)
		// {
		//
		// if(m.equalsIgnoreCase("n"))
		// {
		// spnnorth.setEnabled(true);
		// adp=new SimpleTextAdapter(con, list,"","");
		// spnnorth.setAdapter(adp);
		// spnnorth.setSelection(0);
		//
		//
		// }
		// else
		// {
		// Spneast.setEnabled(true);
		// adp=new SimpleTextAdapter(con, list,"","");
		// Spneast.setAdapter(adp);
		// Spneast.setSelection(0);
		// }
		// }
		// else
		// {
		//
		// Toast.makeText(con,"No Area Found",Toast.LENGTH_SHORT).show();
		// }
	
			if (north != null && north.length() > 0) {

				int j = 0;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).id.equals(north)) {
						j = i;

						break;
					}
					// Log.e("north task", north + "" + "north name" +
					// list.get(i).id + "");
				}
				// here changes done spnMaterial.setSelection(j);
				spnnorth.setSelection(0);
			}

			if (east != null && east.length() > 0) {

				int j = 0;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).id.equals(east)) {
						j = i;

						break;
					}
					// Log.e("east task", east + "" + "east name" +
					// list.get(i).id + "");
				}
				// here changes done spnMaterial.setSelection(j);
				Spneast.setSelection(0);

			}
		// if (data.result == RESPONSE_RESULT.success) {
			// Intent intent = new Intent(context, ActivityGallery.class);
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
