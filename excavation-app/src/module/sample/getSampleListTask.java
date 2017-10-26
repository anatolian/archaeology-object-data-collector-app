package module.sample;

import java.util.List;

import module.common.adapter.SimpleTextAdapter;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.factory.SimpleListFactory;
import module.common.task.BaseTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.appenginedemo.db.DBHelper;

public class getSampleListTask extends BaseTask {

	Context con;
	Spinner spnMaterial, spnContext, spnSample;
	List<SimpleData> list;
	List<SimpleData> material;
	ProgressDialog progressDialog = null;
	SimpleTextAdapter adp;
	String m, north, east, cloth, gtype, contno, sno, co, sn;
	String type, mode = "list";
	String ip_address = "", ff, img;
	ListView lv;
	ProgressBar progressBar2;
	int count;

	public getSampleListTask(Context activity, int count,
			Spinner spnClothmaterial, String md, String material, String type,
			String conNo, String SamNo, String speast, String spnorth,
			String conn, String sam, ProgressBar progressBar2) {
		con = activity;
		spnMaterial = spnClothmaterial;
		this.count = count;
		m = md;
		this.progressBar2 = progressBar2;
		cloth = material;
		gtype = type;
		contno = conNo;
		sno = SamNo;

		east = speast;
		north = spnorth;
		co = conn;
		sn = sam;

		// Log.e("gtype", gtype + "contno" + contno + "");
		// /spnContext=context;
		// spnSample=sample;
	}

	public getSampleListTask(Context activity_Sample, ListView listViewContext,
			String md, String east, String north, String imagePath,
			ProgressBar progressBar2) {
		con = activity_Sample;
		lv = listViewContext;
		m = md;
		this.east = east;
		this.north = north;
		this.progressBar2 = progressBar2;
		img = imagePath;
	}

	public getSampleListTask(Context activity_Sample, ListView clothmaterial,
			String md, String spnmaterial2, String type2, String conNo,
			String samNo, String act, ProgressBar progressBar2) {
		con = activity_Sample;
		lv = clothmaterial;

		m = md;
		this.progressBar2 = progressBar2;
		cloth = spnmaterial2;
		gtype = type2;
		contno = conNo;
		sno = samNo;
		ff = act;
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
		// list=factory.getSampleList(type,mode);

		if (m.equalsIgnoreCase("m")) {
			list = factory.getSampleList("material", "list", m, ip_address, "",
					"", "");
		} else if (m.equalsIgnoreCase("cn")) {
			list = factory.getSampleList("context", "list", m, ip_address,
					east, north, "");
		} else if (m.equalsIgnoreCase("s")) {
			list = factory.getSampleList("sample", "list", m, ip_address, east,
					north, co);
		} else
			list = factory.getSampleList("photograph", "list", m, ip_address,
					"", "", "");

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
		if (m.equalsIgnoreCase("m")) {
			if (ff != null && ff.length() > 0) {

			} else {
				SimpleData d = new SimpleData();
				d.id = "Cloth Material";
				list.add(0, d);
			}

		} else if (m.equalsIgnoreCase("cn")) {
			if (lv != null) {

			} else {
				SimpleData d = new SimpleData();
				d.id = "Context Number";
				list.add(0, d);
			}
		} else if (m.equalsIgnoreCase("s")) {
			SimpleData d = new SimpleData();
			d.id = "Sample Number";
			list.add(0, d);
		}

		else {
			SimpleData d = new SimpleData();
			d.id = "Type";
			list.add(0, d);
		}

		if (list != null && list.size() > 0) {

			if (lv != null) {
				//here changes done
				adp = new SimpleTextAdapter(con, list, "1", ff);
				lv.setAdapter(adp);
			} else {
				adp = new SimpleTextAdapter(con, list, "", "");
				spnMaterial.setAdapter(adp);
				if (count == 1) {
					spnMaterial.setSelection(AppConstants.spnTypepos);
				} else if (count == 2) {
					spnMaterial.setSelection(AppConstants.spnConNopos);
				} else if (count == 3) {
					spnMaterial.setSelection(AppConstants.spnSamNopos);
				}
			}

		} else {
			// lv.setVisibility(View.GONE);
			// Toast.makeText(con, "NO DATA FOUND", Toast.LENGTH_LONG).show();
		}
		if (cloth != null && cloth.length() > 0) {

			int j = 0;
			for (int i = 0; i < list.size(); i++) {
				if (cloth.equals(list.get(i).id)) {
					j = i;
					break;
				}
				// Log.e("cloth task", cloth + "" + "cloth name" +
				// list.get(i).id + "");
			}
			spnMaterial.setSelection(j);
		}

		if (gtype != null && gtype.length() > 0) {

			int j = 0;
			for (int i = 0; i < list.size(); i++) {
				if (gtype.equals(list.get(i).id)) {
					j = i;
					break;
				}
				// Log.e("gtype task", gtype + "" + "gtype name" +
				// list.get(i).id+ "");
			}
			spnMaterial.setSelection(j);
		}

		if (contno != null && contno.length() > 0) {

			int j = 0;
			for (int i = 0; i < list.size(); i++) {
				if (contno.equals(list.get(i).id)) {
					j = i;
					break;
				}
				// Log.e("contno task", contno + "" + "contno name"+
				// list.get(i).id + "");
			}
			// here changes done spnMaterial.setSelection(j);
			
				spnMaterial.setSelection(0);
			
		}
		if (sno != null && sno.length() > 0) {

			int j = 0;
			for (int i = 0; i < list.size(); i++) {
				if (sno.equals(list.get(i).id)) {
					j = i;
					break;
				}
				// / Log.e("sno task", sno + "" + "sno name" + list.get(i).id +

			}
			// spnMaterial.setSelection(j);
			// here changes done spnMaterial.setSelection(j);
			
		
				spnMaterial.setSelection(0);
			
			
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

		// Intent i=new Intent(context,ActivityGallery.class);
		// context.startActivity(i);
	}

	@Override
	public void release() {

	}
}
