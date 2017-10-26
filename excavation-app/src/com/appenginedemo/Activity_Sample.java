package com.appenginedemo;

import java.io.File;

import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.dialog.ImageDialog;
import module.common.task.BaseTask;
import module.context.getAreaTask;
import module.sample.getSampleListTask;
import module.sample.getSampleListTask1;
import module.sample.getSamplePhototTask;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.utils.imageloader.ImageLoader;

public class Activity_Sample extends ActivityBase {
	LayoutInflater inflaterMain;
	RelativeLayout screenMain;
	ListView clothmaterial;
	TextView clothmaterial1;
	Spinner areaEasting, areaNorting, spnType, spnContextNo, spnSampleNo;
	TextView textViewtakephoto, textViewuploadphoto, samplephoto,
			textviewuploadphoto;
	ImageView imgphoto;
	BaseTask task;
	String spnEast = "", spnnorth = "", imagePath = "", yes = "", north = "",
			east = "", spnmaterial = "", spncon = "", spnSAm = "", type = "",
			sno = "", ctxno = "", gtype = "", material = "", val = "";
	ImageLoader imgld;
	ProgressBar pbar, progressBar2;
	GridView GridViewList;
	private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;

	SharedPreferences prefernce;
	boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		inflaterMain = getLayoutInflater();
		screenMain = (RelativeLayout) inflaterMain.inflate(
				R.layout.activity_sample1, null);
		wrapper.addView(screenMain);

		header.setText("Sample");
		header.setBackgroundColor(getResources().getColor(R.color.Lavender));
		AppConstants.up = 1;
		TextViewSample.setBackgroundColor(getResources().getColor(
				R.color.butterflyblue));

		TextView3d.setBackgroundColor(getResources().getColor(R.color.black));
		TextViewContext.setBackgroundColor(getResources().getColor(
				R.color.black));
		samplephoto = (TextView) findViewById(R.id.samplephoto);

		areaEasting = (Spinner) findViewById(R.id.areaEasting);
		areaNorting = (Spinner) findViewById(R.id.areaNorting);

		clothmaterial = (ListView) findViewById(R.id.clothmaterial);
		clothmaterial1 = (TextView) findViewById(R.id.clothmaterial1);
		spnType = (Spinner) findViewById(R.id.Type);
		spnContextNo = (Spinner) findViewById(R.id.contextNo);
		spnSampleNo = (Spinner) findViewById(R.id.sampleNo);

		textViewtakephoto = (TextView) findViewById(R.id.textviewtakephoto);
		textviewuploadphoto = (TextView) findViewById(R.id.textviewuploadphoto);
		GridViewList = (GridView) findViewById(R.id.GridViewList);

		pbar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
		if (getIntent().hasExtra("db")) {
			val = getIntent().getExtras().getString("db");
		}
		if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
				|| getIntent().hasExtra("samp_no")
				|| getIntent().hasExtra("Context_no")
				|| getIntent().hasExtra("material")
				|| getIntent().hasExtra("type")) {
			north = getIntent().getExtras().getString("north");
			east = getIntent().getExtras().getString("east");
			sno = getIntent().getExtras().getString("samp_no");
			ctxno = getIntent().getExtras().getString("Context_no");
			material = getIntent().getExtras().getString("material");
			gtype = getIntent().getExtras().getString("type");
			/*
			 * areaEasting.setSelection(AppConstants.spnEastpos);
			 * areaNorting.setSelection(AppConstants.spnNorthpos);
			 * spnContextNo.setSelection(AppConstants.spnConNopos);
			 * spnSampleNo.setSelection(AppConstants.spnSamNopos);
			 * clothmaterial1.setText(material);
			 */

		}
		if (spnnorth != null && spnnorth.length() > 0 || spnEast != null
				&& spnEast.length() > 0 || type != null && type.length() > 0
				|| spnmaterial != null && spnmaterial.length() > 0
				|| spncon != null && spncon.length() > 0 || spnSAm != null
				&& spnSAm.length() > 0) {

		} else {

			spnnorth = north;
			spnEast = east;
			type = gtype;
			spnmaterial = material;
			spncon = ctxno;
			spnSAm = sno;
		}
		/*
		 * if(val!=null && val.length()>0) { if (AppConstants.SampleselectedImg
		 * != null && AppConstants.SampleselectedImg.size() > 0) {
		 * 
		 * SimpleImageAdapter img = new SimpleImageAdapter(Activity_Sample.this,
		 * AppConstants.SampleselectedImg,spnEast,spnnorth,spncon,spnSAm,val);
		 * GridViewList.setAdapter(img); } } /*else {
		 * 
		 * /*if (AppConstants.SampleselectedImg != null &&
		 * AppConstants.SampleselectedImg.size() > 0) {
		 * 
		 * SimpleImageAdapter img = new SimpleImageAdapter(Activity_Sample.this,
		 * AppConstants.SampleselectedImg,spnEast,spnnorth,spncon,spnSAm,"");
		 * GridViewList.setAdapter(img); }
		 */

		// }

		task = new getSampleListTask(Activity_Sample.this, clothmaterial, "m",
				spnmaterial, "", "", "", "act", progressBar2);
		task.execute();

		/*
		 * 
		 * task = new getSampleListTask(Activity_Sample.this, spnSampleNo,
		 * "s","","","",spnSAm); task.execute();
		 */

		task = new getSampleListTask(Activity_Sample.this, 1, spnType, "t", "",
				type, "", "", "", "", "", "", progressBar2);

		task.execute();

		task = new getAreaTask(Activity_Sample.this, 2, areaNorting,
				areaEasting, "e", "", spnEast, "", progressBar2, 0);
		task.execute();

		System.out.println("appcons pos IS===>" + AppConstants.spnNorthpos);
		flag = false;
		areaNorting.setEnabled(false);
		areaEasting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				AppConstants.spnEastpos = arg2;

				// areaNorting.setSelection(AppConstants.spnNorthpos);
				// spnContextNo.setSelection();

				if (arg2 > 0) {

					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					spnEast = s.id;
					task = new getSamplePhototTask(Activity_Sample.this,1,
							GridViewList, spnnorth, spnEast, spncon, spnSAm,
							type);
					task.execute();
					// Log.e("EAsr",spnEast+"");

					areaNorting.setEnabled(true);

				}
				task = new getAreaTask(Activity_Sample.this, 2, areaNorting,
						areaEasting, "n", spnnorth, "", spnEast, progressBar2,
						0);
				task.execute();

				clothmaterial1.setText("");

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		spnContextNo.setEnabled(false);
		areaNorting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				AppConstants.spnNorthpos = arg2;
				if (arg2 > 0) {
					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					spnnorth = s.id;
					task = new getSamplePhototTask(Activity_Sample.this,2,
							GridViewList, spnnorth, spnEast, spncon, spnSAm,
							type);
					task.execute();
					// Log.e("spnnorth",spnnorth+"");
					spnContextNo.setEnabled(true);
				}
				task = new getSampleListTask(Activity_Sample.this, 2,
						spnContextNo, "cn", "", "", spncon, "", spnEast,
						spnnorth, "", "", progressBar2);
				task.execute();

				clothmaterial1.setText("");
				// spnContextNo.setSelection(0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		GridViewList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				SimpleData d1 = (SimpleData) arg0.getItemAtPosition(arg2);

				ImageDialog d = new ImageDialog(Activity_Sample.this, d1.img);
				d.show();
			}
		});

		spnType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				AppConstants.spnTypepos = arg2;
				if (arg2 > 0) {
					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					type = s.id;
					task = new getSamplePhototTask(Activity_Sample.this,3,
							GridViewList, spnnorth, spnEast, spncon, spnSAm,
							type);
					task.execute();
					// Log.e("type in activity", type + "");
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		clothmaterial.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				if (arg2 > 0) {
					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					spnmaterial = s.id;
					samplephoto.setText(spnmaterial + "");
					// Log.e("cloth material", spnmaterial + "");
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		spnSampleNo.setEnabled(false);

		spnContextNo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				AppConstants.spnConNopos = arg2;
				if (arg2 > 0) {
					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					spncon = s.id;

					task = new getSamplePhototTask(Activity_Sample.this,4,
							GridViewList, spnnorth, spnEast, spncon, spnSAm,
							type);
					task.execute();

					// Log.e("Sample", spnSAm + "");

					spnSampleNo.setEnabled(true);

					/*
					 * task=new getSamplePhototTask(Activity_Sample.this,
					 * GridViewList,spnnorth,spnEast,spncon,spnSAm,type);
					 * task.execute();
					 */

					// Log.e("context in activity", spncon + "");
				}

				task = new getSampleListTask(Activity_Sample.this, 3,
						spnSampleNo, "s", "", "", "", spnSAm, spnEast,
						spnnorth, spncon, "", progressBar2);
				task.execute();
				clothmaterial1.setText("");

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		spnSampleNo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				AppConstants.spnSamNopos = arg2;
				if (arg2 > 0) {
					flag = true;
					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					spnSAm = s.id;

					task = new getSamplePhototTask(Activity_Sample.this,5,
							GridViewList, spnnorth, spnEast, spncon, spnSAm,
							type);
					task.execute();
					task = new getSampleListTask1(Activity_Sample.this,
							spnnorth, spncon, spnEast, "material", "list",
							spnSAm, progressBar2, clothmaterial1, clothmaterial);
					task.execute();
					// Log.e("Sample", spnSAm + "");

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		textViewtakephoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (flag == true && AppConstants.spnSamNopos > 0) {
					if (spnnorth != null && spnnorth.length() > 0

					&& spnEast != null && spnEast.length() > 0
							&& spnSAm != null && spnSAm.length() > 0
							&& spncon != null && spncon.length() > 0
							&& type != null && type.length() > 0) {
						Intent i = new Intent(Activity_Sample.this,
								ActivityCamera1.class);
						i.putExtra("north", spnnorth);
						i.putExtra("east", spnEast);
						i.putExtra("imagePath", imagePath);
						i.putExtra("samp_no", spnSAm);
						i.putExtra("Context_no", spncon);
						i.putExtra("sam", "Sam");
						i.putExtra("material", spnmaterial);
						i.putExtra("type", type);
						startActivity(i);
					}
				} else {
					Toast.makeText(Activity_Sample.this,
							"Please Fill All Required Field", Toast.LENGTH_LONG)
							.show();
				}
			}

		});

		/*
		 * textviewuploadphoto.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { /*Intent i = new
		 * Intent(Activity_Sample.this, ActivityCamera1.class);
		 * i.putExtra("north", spnnorth); i.putExtra("east", spnEast);
		 * i.putExtra("imagePath", imagePath); i.putExtra("samp_no", spnSAm);
		 * i.putExtra("Context_no", spncon);
		 * 
		 * i.putExtra("sam", "Sam"); i.putExtra("material", spnmaterial);
		 * i.putExtra("type", type); startActivity(i);
		 * 
		 * 
		 * if(AppConstants.SampleselectedImg!=null &&
		 * AppConstants.SampleselectedImg.size()>0) { // AppConstants.up=1; task
		 * = new AddSamplePhotoTask(Activity_Sample.this, spnEast, spnnorth,
		 * AppConstants.SampleselectedImg,pbar,spncon,spnSAm,type);
		 * task.execute(); } else { AppConstants.up=1;
		 * Toast.makeText(Activity_Sample.this,"Please Select Photos to upload",
		 * Toast.LENGTH_SHORT).show(); }
		 * 
		 * } });
		 */

		/*
		 * task=new getSamplePhototTask(Activity_Sample.this,
		 * GridViewList,spnnorth,spnEast,spncon,spnSAm,type); task.execute();
		 */
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		trimCache(this);
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		trimCache(this);
	}
	 public static void trimCache(Context context) {
	      try {
	         File dir = context.getCacheDir();
	         if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);
	         }
	      } catch (Exception e) {
	         // TODO: handle exception
	      }
	   }

	   public static boolean deleteDir(File dir) {
	      if (dir != null && dir.isDirectory()) {
	         String[] children = dir.list();
	         for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	               return false;
	            }
	         }
	      }

	      // The directory is now empty so delete it
	      return dir.delete();
	   }

}
