package com.appenginedemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import module.common.adapter.SimpleContextSelectedAdapter;
import module.common.adapter.SimpleStringAdapter;
import module.common.application.ApplicationHandler;
import module.common.bean.SimpleData;
import module.common.constants.AppConstants;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.factory.SimpleListFactory;
import module.common.http.factory.SimpleObjectFactory;
import module.common.task.BaseTask;
import module.context.getAreaTask;
import module.image.property.ImagePropertyBean;
import module.image.property.ImagePropertyTask;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appenginedemo.db.DBHelper;
import com.utils.imageloader.ImageLoader;

public class MainActivity extends ActivityBase {

	LayoutInflater inflaterMain;
	RelativeLayout screenMain;
	LinearLayout LinearLayout1;
	Spinner areaEasting, areaNorting, spnContext;
	TextView listdisp;
	static ImageView imgphoto;
	BaseTask task;
	static String spnEast = null, spnnorth = null, imagePath = null,
			yes = null, north = null, east = null, ctx_no = null, next;
	ArrayList<String> ctx;
	public ImageLoader imgld;
	ListView listViewContext;
	TextView textViewReplacephotphoto, textViewnextphoto;
	String p1;
	ProgressBar progressBar2;
	String photo_id = null;
	int flag_context = 0;
	int a = 0;
	boolean replcae = false, photostart = false;
	private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;
	ProgressBar progressBar1;
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	private Uri fileUri; // file url to store image/video
	ImagePropertyTask task1;
	ImagePropertyBean data1;
	DBHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		inflaterMain = getLayoutInflater();
		screenMain = (RelativeLayout) inflaterMain.inflate(
				R.layout.activity_main, null);
		wrapper.addView(screenMain);
		AppConstants.up = 1;
		header.setText("Context Photo");
		header.setBackgroundColor(getResources().getColor(R.color.cream));
		progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
		areaEasting = (Spinner) findViewById(R.id.areaEasting);
		areaNorting = (Spinner) findViewById(R.id.areaNorting);
		spnContext = (Spinner) findViewById(R.id.spnContext);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		areaEasting.setEnabled(false);
		areaNorting.setEnabled(false);
		// spnContext.setEnabled(false);

		AppConstants.temp_Context_No = null;
		textViewReplacephotphoto = (TextView) findViewById(R.id.textViewReplacephotphoto);
		textViewnextphoto = (TextView) findViewById(R.id.textViewnextphoto);
		listdisp = (TextView) findViewById(R.id.textViewdisp);
		LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
		imgphoto = (ImageView) findViewById(R.id.imageView1);
		imgld = new ImageLoader(MainActivity.this);
		listViewContext = (ListView) findViewById(R.id.listViewContext);

		task1 = new ImagePropertyTask(MainActivity.this);
		task1.execute();

		ApplicationHandler apphand = ApplicationHandler.getInstance();

		if (getIntent().hasExtra("north")) {
			north = getIntent().getExtras().getString("north");
		}
		if (getIntent().hasExtra("east")) {
			east = getIntent().getExtras().getString("east");
		}
		if (getIntent().hasExtra("ctx")) {
			// ctx = getIntent().getExtras().getString("ctx");
			ctx = getIntent().getExtras().getStringArrayList("ctx");
		}

		areaNorting.setEnabled(false);
		if (spnnorth != null && spnnorth.length() > 0 || spnEast != null
				&& spnEast.length() > 0 || AppConstants.temp_Context_No != null
				&& AppConstants.temp_Context_No.size() > 0) {
		} else {

			spnnorth = north;
			spnEast = east;
			// /AppConstants.temp_Context_No=ctx;

		}

		listdisp.setText(ctx_no);
		// Log.e("spnnorth 1213", spnnorth + "");
		// Log.e("EAsr 1232", spnEast + "");

		task = new getAreaTask(MainActivity.this, 1, areaNorting, areaEasting,
				"e", "", spnEast, "", progressBar2, 0);
		task.execute();

		if (getIntent().hasExtra("imagePath") && getIntent().hasExtra("y")) {
			imagePath = getIntent().getExtras().getString("imagePath");
			yes = getIntent().getExtras().getString("y");
		}

		if (getIntent().hasExtra("next")) {
			next = getIntent().getExtras().getString("next");

		}
		// if (getIntent().hasExtra("pic")) {
		// p1 = getIntent().getExtras().getString("pic");
		//
		// }

		// Log.e("image in main", imagePath + " ");

		if (yes != null && yes.length() > 0) {
			// imgld.DisplayImage(imagePath, imgphoto);

			imgphoto.setImageBitmap(apphand.decodeFile(new File(imagePath)));
		}

		TextView3d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TextView3d.setTextColor(getResources().getColor(R.color.white));
				if (photostart) {
					if (imagePath != null && imagePath.length() > 0) {

						if (AppConstants.temp_Context_No != null
								&& AppConstants.temp_Context_No.size() > 0) {
							TextView3d.setBackgroundColor(getResources()
									.getColor(R.color.butterflyblue));

							TextViewContext.setBackgroundColor(getResources()
									.getColor(R.color.black));
							TextViewSample.setBackgroundColor(getResources()
									.getColor(R.color.black));
							// AppConstants.selectedImg = new
							// ArrayList<String>();
							/*
							 * Intent i = new Intent(ActivityBase.this,
							 * Activity_3d.class); startActivity(i);
							 */

							Intent i = new Intent(MainActivity.this,
									Activity_3d.class);
							startActivity(i);

						} else {
							Toast.makeText(
									MainActivity.this,
									"Please select atleast one context number..",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					TextView3d.setBackgroundColor(getResources().getColor(
							R.color.butterflyblue));

					TextViewContext.setBackgroundColor(getResources().getColor(
							R.color.black));
					TextViewSample.setBackgroundColor(getResources().getColor(
							R.color.black));
					// AppConstants.selectedImg = new ArrayList<String>();
					/*
					 * Intent i = new Intent(ActivityBase.this,
					 * Activity_3d.class); startActivity(i);
					 */

					Intent i = new Intent(MainActivity.this, Activity_3d.class);
					startActivity(i);

				}

			}
		});
		TextViewSample.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (photostart) {
					if (imagePath != null && imagePath.length() > 0) {
						if (AppConstants.temp_Context_No != null
								&& AppConstants.temp_Context_No.size() > 0) {
							TextViewSample.setBackgroundColor(getResources()
									.getColor(R.color.butterflyblue));

							TextView3d.setBackgroundColor(getResources()
									.getColor(R.color.black));
							TextViewContext.setBackgroundColor(getResources()
									.getColor(R.color.black));

							/*
							 * Intent i = new Intent(ActivityBase.this,
							 * Activity_Sample.class); startActivity(i);
							 */
							// if(AppConstants.up==1)
							// {
							Intent i = new Intent(MainActivity.this,
									Activity_Sample.class);
							startActivity(i);
						} else {
							Toast.makeText(
									MainActivity.this,
									"Please select atleast one context number..",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {

					TextViewSample.setBackgroundColor(getResources().getColor(
							R.color.butterflyblue));

					TextView3d.setBackgroundColor(getResources().getColor(
							R.color.black));
					TextViewContext.setBackgroundColor(getResources().getColor(
							R.color.black));

					/*
					 * Intent i = new Intent(ActivityBase.this,
					 * Activity_Sample.class); startActivity(i);
					 */
					// if(AppConstants.up==1)
					// {
					Intent i = new Intent(MainActivity.this,
							Activity_Sample.class);
					startActivity(i);
					// }
					// else
					// {

				}

			}
		});

		imgphoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (spnnorth != null && spnnorth.length() > 0
						&& spnEast != null && spnEast.length() > 0) {
					capture_image();

					// Intent i = new
					// Intent(MainActivity.this,ActivityCamera.class);
					// i.putExtra("north", spnnorth);
					// i.putExtra("east", spnEast);
					// i.putExtra("ctx", AppConstants.temp_Context_No);
					// i.putExtra("imagePath", imagePath);
					// i.putExtra("pic", "pic");
					// startActivity(i);

				} else {
					Toast.makeText(MainActivity.this, "Select area",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		textViewReplacephotphoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// if (p1 != null && p1.length() > 0) {
				if (spnnorth != null && spnnorth.length() > 0
						&& spnEast != null && spnEast.length() > 0) {
					replcae = true;
					capture_image();
					// Intent i = new Intent(MainActivity.this,
					// ActivityCamera.class);
					//
					// i.putExtra("north", spnnorth);
					// i.putExtra("east", spnEast);
					// i.putExtra("ctx", AppConstants.temp_Context_No);
					// i.putExtra("imagePath", imagePath);
					// i.putExtra("photo_id", "");
					// // i.putExtra("north",spnnorth);
					// // i.putExtra("east",spnEast);
					// startActivity(i);
					// } else {
					// Toast.makeText(MainActivity.this, "Select area",
					// Toast.LENGTH_LONG).show();
					// }
				} else {
					Toast.makeText(MainActivity.this, "No Photo to Replace",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		areaEasting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				AppConstants.activity_mainSpnEast = arg2;
				if (arg2 > 0) {
					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					if (spnEast != null && spnEast.length() > 0) {
						if (spnEast.equalsIgnoreCase("s.id")) {

						} else {
							AppConstants.temp_Context_No = null;
							photo_id = null;
							imagePath = null;
							replcae = false;
							listViewContext.setAdapter(null);
							listViewContext.setVisibility(View.GONE);
							spnContext.setVisibility(View.GONE);
							a = 0;
							textViewnextphoto.setEnabled(false);
							textViewReplacephotphoto.setEnabled(false);
							textViewnextphoto.setClickable(false);
							textViewReplacephotphoto.setClickable(false);
							imgphoto.setImageResource(R.drawable.camera);
						}
					}

					spnEast = s.id;

					// spnEast = areaEasting.getSelectedItem().toString();

					// Log.e("EAsr", spnEast + "");

					// if (imagePath != null && imagePath.length() > 0) {
					// task = new
					// getSampleListTask(MainActivity.this,listViewContext,
					// "cn", spnEast, spnnorth,
					// imagePath,progressBar1);
					// task.execute();
					// }

					areaNorting.setEnabled(true);
					areaNorting.setAdapter(null);
					task = new getAreaTask(MainActivity.this, 1, areaNorting,
							areaEasting, "n", spnnorth, "", spnEast,
							progressBar2, 1);
					task.execute();
				} else {
					spnEast = "";
					areaNorting.setEnabled(false);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		areaNorting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				AppConstants.activity_mainSpnNorth = arg2;
				//
				if (arg2 > 0) {

					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					if (spnnorth != null && spnnorth.length() > 0) {
						if (spnnorth.equalsIgnoreCase("s.id")) {

						} else {
							AppConstants.temp_Context_No = null;
							photo_id = null;
							replcae = false;
							imagePath = null;
							listViewContext.setAdapter(null);
							listViewContext.setVisibility(View.GONE);
							spnContext.setVisibility(View.GONE);
							a = 0;
							textViewnextphoto.setEnabled(false);
							textViewReplacephotphoto.setEnabled(false);
							textViewnextphoto.setClickable(false);
							textViewReplacephotphoto.setClickable(false);
							imgphoto.setImageResource(R.drawable.camera);

						}
					}
					spnnorth = s.id;
					// Log.e("spnnorth", spnnorth + "");

					// if (imagePath != null && imagePath.length() > 0) {
					// /*
					// * task = new getSampleListTask(MainActivity.this,
					// * listViewContext, "cn", spnEast, spnnorth,imagePath);
					// * task.execute();
					// */
					//
					// task = new getSampleListTask(MainActivity.this,
					// spnContext, "cn", "", "", "", "", spnEast,
					// spnnorth, "", "",progressBar1);
					// task.execute();
					// }
				} else {
					spnnorth = "";
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		spnContext.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				//
				if (arg2 > 0) {

					SimpleData s = (SimpleData) arg0.getItemAtPosition(arg2);
					ctx_no = s.id;
					// Log.e("spnnorth", spnnorth + "");
					if (AppConstants.temp_Context_No != null
							&& AppConstants.temp_Context_No.size() > 0) {

					} else {
						AppConstants.temp_Context_No = new ArrayList<String>();
					}
					AppConstants.temp_Context_No.add(ctx_no);

					// task = new addContextTaskMain();
					// task.execute();

					// if (flag_context==0) {
					// task = new addSinglePhotoTaskMain();
					// task.execute();
					// } else{
					// task = new addContextTaskMain();
					// task.execute();
					//
					// }
					textViewnextphoto.setEnabled(true);
					textViewReplacephotphoto.setEnabled(true);
					textViewnextphoto.setClickable(true);
					textViewReplacephotphoto.setClickable(true);
				}

				// SimpleStringAdapter adp = new SimpleStringAdapter(
				// getApplicationContext(), AppConstants.temp_Context_No,
				// spnEast, spnnorth, imagePath);
				// listViewContext.setAdapter(adp);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		textViewnextphoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Log.e("context no",ctx_no+"");

				if (AppConstants.temp_Context_No != null
						&& AppConstants.temp_Context_No.size() > 0) {

					if (spnnorth != null && spnnorth.length() > 0
							&& spnEast != null && spnEast.length() > 0) {

						if (imagePath != null && imagePath.length() > 0) {

							/*
							 * task = new addSinglePhotoTask(MainActivity.this,
							 * spnnorth, spnEast, imagePath,
							 * AppConstants.temp_Context_No); task.execute();
							 */

							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									MainActivity.this);
							LinearLayout myLayout = new LinearLayout(
									MainActivity.this);
							myLayout.setOrientation(LinearLayout.VERTICAL);

							final TextView t1 = new TextView(MainActivity.this);
							t1.setTextSize(15.0f);
							t1.setTextColor(Color.WHITE);
							t1.setPadding(10, 5, 10, 5);
							t1.setText("Are you done with this photograph?");
							myLayout.addView(t1);

							alertDialogBuilder.setTitle("Alert");

							// set dialog message
							alertDialogBuilder
									.setView(myLayout)
									.setCancelable(false)
									.setPositiveButton(
											"Ok",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked, close
													// current activity
													task = new addSinglePhotoTaskMain();
													task.execute();

													Intent i = new Intent(
															MainActivity.this,
															MainActivity.class);
													i.putExtra("north",
															spnnorth);
													i.putExtra("east", spnEast);
													i.putExtra("imagePath",
															imagePath);
													// i.putExtra("next",
													// "next");
													i.putExtra(
															"ctx",
															AppConstants.temp_Context_No);
													startActivity(i);

												}
											})
									.setNegativeButton(
											"Cancel",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													// if this button is
													// clicked, just
													// close
													// the dialog box and do
													// nothing
													dialog.cancel();
													// finish();
												}
											});

							AlertDialog alertDialog = alertDialogBuilder
									.create();

							// show it
							alertDialog.show();

							/*
							 * Intent i=new
							 * Intent(MainActivity.this,ActivityCamera.class);
							 * i.putExtra("north",spnnorth);
							 * i.putExtra("east",spnEast);
							 * i.putExtra("imagePath", imagePath);
							 * i.putExtra("next", "next");
							 * i.putExtra("ctx",ctx_no); startActivity(i);
							 */
						} else {
							// Toast.makeText(MainActivity.this,"You have not click any photo",Toast.LENGTH_LONG).show();
							Intent i = new Intent(MainActivity.this,
									ActivityCamera.class);
							i.putExtra("north", spnnorth);
							i.putExtra("east", spnEast);
							i.putExtra("ctx", AppConstants.temp_Context_No);
							i.putExtra("imagePath", imagePath);
							startActivity(i);
						}
					} else {
						Toast.makeText(MainActivity.this, "Select area",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(MainActivity.this,
							"Please Select atleast one Context number..",
							Toast.LENGTH_LONG).show();
				}

			}

		});

	}

	public void capture_image() {
		// try {
		// Intent localIntent = new Intent(
		// "android.media.action.IMAGE_CAPTURE");
		// MainActivity.this.startActivityForResult(
		// localIntent, MainActivity.CAMERA_CAPTURE);
		//
		// } catch (ActivityNotFoundException localActivityNotFoundException) {
		// Toast.makeText(
		// MainActivity.this,
		// "Whoops - your device doesn't support capturing images!",
		// Toast.LENGTH_SHORT).show();
		// MainActivity.this.finish();
		// }
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE);
		//
		// try {
		// Intent captureIntent = new Intent(
		// MediaStore.ACTION_IMAGE_CAPTURE);
		// // we will handle the returned data in
		// // onActivityResult
		// startActivityForResult(captureIntent,
		// CAMERA_CAPTURE);
		//
		// } catch (ActivityNotFoundException anfe) {
		// // display an error message
		// String errorMessage =
		// "Whoops - your device doesn't support capturing images!";
		// Toast toast = Toast.makeText(
		// MainActivity.this, errorMessage,
		// Toast.LENGTH_SHORT);
		// toast.show();
		//
		// }

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save file url in bundle as it will be null on scren orientation
		// changes
		outState.putParcelable("file_uri", fileUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// get the file url
		fileUri = savedInstanceState.getParcelable("file_uri");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image

		if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
			// Log.e("URI", data.getExtras().get("data")+" ");
			// Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			// imgphoto.setImageBitmap(thumbnail);
			// ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			// thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
			// File file = new File(Environment.getExternalStorageDirectory()
			// + File.separator + "image.jpg");
			// try {
			// file.createNewFile();
			// FileOutputStream fo = new FileOutputStream(file);
			// fo.write(bytes.toByteArray());
			// fo.close();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			try {
				Bitmap thumbnail = decodeUri(fileUri);
				imgphoto.setImageBitmap(thumbnail);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// imagePath = Environment.getExternalStorageDirectory()+
			// File.separator + "image.jpg";

			// changes done here
			if (replcae) {
				System.out.println("spneast" + spnEast + "spnnorth" + spnnorth);
				//task = new GetContextList(MainActivity.this); task.execute();
				//changes done here
				/*task = new ReplacePhotoMain();
				task.execute();
*/				// }else{
				// task=new GetContextList(MainActivity.this);
				// task.execute();
				// }
			} else {
				task = new GetContextList(MainActivity.this); task.execute();
				 

				//flag_context = flag_context + 1;

				/*task = new addSinglePhotoTaskMain();
				task.execute();
	*/		}
		}

		// if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
		// if (resultCode == RESULT_OK) {
		// // successfully captured the image
		// // display it in image view
		// // previewCapturedImage();
		//
		// Uri selectedImage = data.getData();
		// Log.e("selectedImage", selectedImage+" ");
		// try {
		// imgphoto.setImageBitmap(decodeUri(selectedImage));
		// if (replcae) {
		// task=new ReplacePhotoMain();
		// task.execute();
		// // }else{
		// // task=new GetContextList(MainActivity.this);
		// // task.execute();
		// // }
		// }else{
		// task = new addSinglePhotoTaskMain();
		// task.execute();
		// }
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }
		//
		//
		// // BaseTask task=new LoadImagesFromSDCard();
		// // task.execute();
		// } else if (resultCode == RESULT_CANCELED) {
		// // user cancelled Image capture
		// Toast.makeText(getApplicationContext(),
		// "User cancelled image capture", Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// // failed to capture image
		// Toast.makeText(getApplicationContext(),
		// "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
		// .show();
		// }
		// } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
		// if (resultCode == RESULT_OK) {
		// // video successfully recorded
		// // preview the recorded video
		// previewVideo();
		// } else if (resultCode == RESULT_CANCELED) {
		// // user cancelled recording
		// Toast.makeText(getApplicationContext(),
		// "User cancelled video recording", Toast.LENGTH_SHORT)
		// .show();
		// } else {
		// // failed to record video
		// Toast.makeText(getApplicationContext(),
		// "Sorry! Failed to record video", Toast.LENGTH_SHORT)
		// .show();
		// }
	}

	/*
	 * Display image from a path to ImageView
	 */

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o);

		final int REQUIRED_SIZE = 100;

		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o2);
	}

	private void previewCapturedImage() {
		try {
			// hide video preview
			// videoPreview.setVisibility(View.GONE);

			imgphoto.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 1;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);

			imgphoto.setImageBitmap(bitmap);
			bitmap.recycle();
			System.gc();
			if (replcae) {
				task = new ReplacePhotoMain();
				task.execute();
				// }else{
				// task=new GetContextList(MainActivity.this);
				// task.execute();
				// }
			} else {
				task = new addSinglePhotoTaskMain();
				task.execute();
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
			Log.e("ImagePAth", mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
			imagePath = mediaStorageDir.getPath() + File.separator + "IMG_"
					+ timeStamp + ".jpg";
			// } else if (type == MEDIA_TYPE_VIDEO) {
			// mediaFile = new File(mediaStorageDir.getPath() + File.separator
			// + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	class addSinglePhotoTaskMain extends BaseTask {

		// Context con;
		// Spinner Spneast;
		SimpleData list;
		ProgressDialog progressDialog = null;
		// SimpleTextAdapter adp;
		// String north1,east1,img,photo_id;
		String ip_address = "", camval;

		public addSinglePhotoTaskMain() {
			// con=activityCamera;
			// north1=north;
			// east1=east;
			// img=imagePath;
			// ctx_no=temp_Context_No;
			// photo_id=photoid;
			// camval=camval1;
		}

		@SuppressWarnings("unchecked")
		public SimpleData getData(int pos) {
			return null;
		}

		@Override
		protected void onPreExecute() {
			// bar.setVisibility(View.VISIBLE);
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			// progressDialog.setMessage("Getting image from server database");

			progressDialog.show();

			// dialog.show();

			DBHelper db = DBHelper.getInstance(MainActivity.this);
			db.open();
			ip_address = db.getIpAddress();
			data1 = db.getImageProperty();
			db.close();
		}

		@Override
		protected Void doInBackground(String... params) {
			SimpleObjectFactory factory = SimpleObjectFactory.getInstance();

			// list =
			// factory.addSingleimg(spnnorth,spnEast,imagePath,ctx_no,ip_address,"");
			if (imagePath != null && imagePath.length() > 0) {
				list = factory.addSingleimg(spnnorth, spnEast, imagePath, null,
						ip_address, "", data1.base_image_path,
						data1.context_subpath);

			}

			// if (photo_id != null && photo_id.length() > 0) {
			// list = factory.addSingleimg(spnnorth, spnEast, imagePath,
			// ctx_no, ip_address, photo_id);
			// } else if (ctx_no != null && ctx_no.length() > 0) {
			//
			// list = factory.addSingleimg(spnnorth, spnEast, imagePath,
			// ctx_no, ip_address, "");
			// } else {
			// list = factory.addSingleimg(spnnorth, spnEast, imagePath, "",
			// ip_address, "");
			// }
			/*
			 * for(int i=0;i<ctx_no.size();i++) {
			 * list=factory.addSingleimg(north1
			 * ,east1,img,ctx_no.get(i),ip_address); }
			 */

			// data =
			// factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (list.result == RESPONSE_RESULT.success) {
				// AppConstants.temp_Context_No = null;
				photo_id = list.id;
				System.out.println("list id...." + photo_id);
				// Toast.makeText(MainActivity.this,
				// "Uploaded Successfully",Toast.LENGTH_LONG).show();
				// Log.e("IMAGEPATH", list.image_path+" ");
				photostart = true;
				if (list.image_path != null && list.image_path.length() > 0) {
					imgld.DisplayImage(list.image_path, imgphoto);
					textViewnextphoto.setEnabled(true);
					System.out.println("inside single photo task post method");
					// textViewnextphoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.choose_file));//(R.drawable.choose_file);
					textViewnextphoto.setClickable(true);

					textViewReplacephotphoto.setEnabled(true);
					// textViewReplacephotphoto.setBackgroundDrawable(getResources().getDrawable(R.drawable.choose_file));
					textViewReplacephotphoto.setClickable(true);

				}
				/*
				 * changes done here
				 * task = new GetContextList(MainActivity.this); task.execute();
				 */

				flag_context = flag_context + 1;

				// Intent i = new Intent(con,MainActivity.class);
				// i.putExtra("pic", "camval");
				// i.putExtra("north", north1);
				// i.putExtra("east", east1);
				// i.putExtra("ctx", ctx_no);
				// /*i.putExtra("y", "yes");
				// // i.putExtra("imagePath", imagePath);
				// i.putExtra("north", north1);
				// i.putExtra("east", east1);
				// i.putExtra("ctx", ctx_no);*/
				// con.startActivity(i);
			} else {
				Toast.makeText(MainActivity.this, list.resultMsg + "",
						Toast.LENGTH_SHORT).show();
			}

			if (AppConstants.temp_Context_No != null
					&& AppConstants.temp_Context_No.size() > 0) {

				listViewContext.setVisibility(View.VISIBLE);
				SimpleContextSelectedAdapter adyt = new SimpleContextSelectedAdapter(
						MainActivity.this, AppConstants.temp_Context_No,
						spnEast, spnnorth, photo_id, listViewContext,
						spnContext);
				listViewContext.setAdapter(adyt);

			} else {
				listViewContext.setVisibility(View.GONE);
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

			/*
			 * if(list.result==RESPONSE_RESULT.success) {
			 * AppConstants.temp_Context_No=null;
			 * Toast.makeText(con,"Uploaded Successfully"
			 * ,Toast.LENGTH_LONG).show();
			 * 
			 * Intent i = new Intent(con, MainActivity.class); /*i.putExtra("y",
			 * "yes"); // i.putExtra("imagePath", imagePath);
			 * i.putExtra("north", north1); i.putExtra("east", east1);
			 * i.putExtra("ctx", ctx_no); con.startActivity(i); }
			 */
			// Intent i=new Intent(context,ActivityGallery.class);
			// context.startActivity(i);
		}

		@Override
		public void release() {

		}
	}

	// class addContextTaskMain extends BaseTask {
	//
	// // Context con;
	// // Spinner Spneast;
	// SimpleData list;
	// ProgressDialog progressDialog = null;
	// // SimpleTextAdapter adp;
	// // String north1,east1,img,photo_id;
	// String ip_address = "", camval;
	//
	// // ArrayList<String>ctx_no=null;
	// /*
	// * public addSinglePhotoTask(Context activityCamera, String north,
	// * String east, String imagePath,String ctx) { con=activityCamera;
	// * north1=north; east1=east; img=imagePath; ctx_no=ctx;
	// *
	// * }
	// */
	// public addContextTaskMain() {
	// // con=activityCamera;
	// // north1=north;
	// // east1=east;
	// // img=imagePath;
	// // ctx_no=temp_Context_No;
	// // photo_id=photoid;
	// // camval=camval1;
	// }
	//
	// @SuppressWarnings("unchecked")
	// public SimpleData getData(int pos) {
	// return null;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// // bar.setVisibility(View.VISIBLE);
	// progressDialog = new ProgressDialog(MainActivity.this);
	// progressDialog.setCancelable(false);
	// progressDialog.setCanceledOnTouchOutside(false);
	// // progressDialog.setMessage();
	//
	// progressDialog.show();
	//
	// // dialog.show();
	//
	// DBHelper db = DBHelper.getInstance(MainActivity.this);
	// db.open();
	// ip_address = db.getIpAddress();
	// db.close();
	// }
	//
	// @Override
	// protected Void doInBackground(String... params) {
	// SimpleObjectFactory factory = SimpleObjectFactory.getInstance();
	//
	// if (photo_id != null && photo_id.length() > 0) {
	// list = factory.addContextNumber(spnnorth, spnEast, ctx_no,
	// ip_address, photo_id);
	//
	// }
	// // } else if (ctx_no != null && ctx_no.length() > 0) {
	// //
	// // list = factory.addSingleimg(spnnorth, spnEast, imagePath,
	// // ctx_no, ip_address, "");
	// // } else {
	// // list = factory.addSingleimg(spnnorth, spnEast, imagePath, "",
	// // ip_address, "");
	// // }
	// /*
	// * for(int i=0;i<ctx_no.size();i++) {
	// * list=factory.addSingleimg(north1
	// * ,east1,img,ctx_no.get(i),ip_address); }
	// */
	//
	// // data =
	// // factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));
	//
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// super.onPostExecute(result);
	//
	// if (progressDialog != null && progressDialog.isShowing()) {
	// progressDialog.dismiss();
	// }
	// if (list.result == RESPONSE_RESULT.success) {
	//
	// // photo_id=list.id;
	//
	// if (list.image_path != null && list.image_path.length() > 0) {
	// imgld.DisplayImage(list.image_path, imgphoto);
	// }
	//
	// task = new GetContextList(MainActivity.this);
	// task.execute();
	//
	// if (AppConstants.temp_Context_No != null
	// && AppConstants.temp_Context_No.size() > 0) {
	// listViewContext.setVisibility(View.VISIBLE);
	// SimpleContextSelectedAdapter adyt = new SimpleContextSelectedAdapter(
	// MainActivity.this, AppConstants.temp_Context_No,
	// spnEast, spnnorth, photo_id, listViewContext,
	// spnContext);
	// listViewContext.setAdapter(adyt);
	//
	// } else {
	// listViewContext.setVisibility(View.GONE);
	// }
	//
	// } else {
	//
	// AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	// MainActivity.this);
	//
	// // set title
	// alertDialogBuilder.setTitle("Photo upload failed.");
	//
	// // set dialog message
	// alertDialogBuilder
	// .setMessage("Do you want to retry?")
	// .setCancelable(false)
	// .setPositiveButton("Yes",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// // if this button is clicked, close
	// // current activity
	// capture_image();
	// }
	// })
	// .setNegativeButton("No",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int id) {
	// // if this button is clicked, just close
	// // the dialog box and do nothing
	// dialog.cancel();
	// }
	// });
	//
	// // create alert dialog
	// AlertDialog alertDialog = alertDialogBuilder.create();
	//
	// // show it
	// alertDialog.show();
	//
	// }
	// }
	//
	// @Override
	// protected void onCancelled(Void result) {
	//
	// release();
	// super.onCancelled(result);
	// // bar.setVisibility(View.INVISIBLE);
	// if (progressDialog != null && progressDialog.isShowing()) {
	// progressDialog.dismiss();
	// }
	//
	// /*
	// * if(list.result==RESPONSE_RESULT.success) {
	// * AppConstants.temp_Context_No=null;
	// * Toast.makeText(con,"Uploaded Successfully"
	// * ,Toast.LENGTH_LONG).show();
	// *
	// * Intent i = new Intent(con, MainActivity.class); /*i.putExtra("y",
	// * "yes"); // i.putExtra("imagePath", imagePath);
	// * i.putExtra("north", north1); i.putExtra("east", east1);
	// * i.putExtra("ctx", ctx_no); con.startActivity(i); }
	// */
	// // Intent i=new Intent(context,ActivityGallery.class);
	// // context.startActivity(i);
	// }
	//
	// @Override
	// public void release() {
	//
	// }
	// }

	class ReplacePhotoMain extends BaseTask {

		Context con;
		Spinner Spneast;
		SimpleData data;
		ProgressDialog progressDialog = null;
		// SimpleTextAdapter adp;
		// String north1,east1,img,photo_id;
		String ip_address = "", camval;

		// ArrayList<String>ctx_no=null;
		/*
		 * public addSinglePhotoTask(Context activityCamera, String north,
		 * String east, String imagePath,String ctx) { con=activityCamera;
		 * north1=north; east1=east; img=imagePath; ctx_no=ctx;
		 * 
		 * }
		 */
		public ReplacePhotoMain() {
			/*
			 * con=activityCamera; north1=north; east1=east; img=imagePath;
			 * ctx_no=temp_Context_No; photo_id=photoid; camval=camval1;
			 */
		}

		@SuppressWarnings("unchecked")
		public SimpleData getData(int pos) {
			return null;
		}

		@Override
		protected void onPreExecute() {
			// bar.setVisibility(View.VISIBLE);
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			// progressDialog.setMessage();

			progressDialog.show();

			// dialog.show();

			DBHelper db = DBHelper.getInstance(MainActivity.this);
			db.open();
			ip_address = db.getIpAddress();
			db.close();
		}

		@Override
		protected Void doInBackground(String... params) {
			SimpleObjectFactory factory = SimpleObjectFactory.getInstance();

			try {
				if (photo_id != null && photo_id.length() > 0) {
					data = factory.REplacePhoto(ip_address, "delete", spnEast,
							spnnorth, ctx_no, photo_id, imagePath);
				}

				System.out.println("ip address" + ip_address);
				System.out.println("ip spnEast" + spnEast);
				System.out.println("ip spnnorth" + spnnorth);
				System.out.println("ip ctx_no" + ctx_no);
				System.out.println("ip photo_id" + photo_id);
				System.out.println("ip imagePath" + imagePath);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// } else if (ctx_no != null && ctx_no.length() > 0) {
			//
			// list = factory.addSingleimg(spnnorth, spnEast, imagePath,
			// ctx_no, ip_address, "");
			// } else {
			// list = factory.addSingleimg(spnnorth, spnEast, imagePath, "",
			// ip_address, "");
			// }
			/*
			 * for(int i=0;i<ctx_no.size();i++) {
			 * list=factory.addSingleimg(north1
			 * ,east1,img,ctx_no.get(i),ip_address); }
			 */

			// data =
			// factory.getAddAlbumsPhotosData(album_name,selectedItems.get(i));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			// list.result
			// if (data.result == RESPONSE_RESULT.success) {

			if (data.result == RESPONSE_RESULT.success) {

				// photo_id=list.id;

				Toast.makeText(MainActivity.this, "Photo replaced..",
						Toast.LENGTH_SHORT).show();

				// if (list.image_path != null && list.image_path.length() > 0)
				// {
				// imgld.DisplayImage(list.image_path, imgphoto);
				// }

			} else {
				Toast.makeText(MainActivity.this, data.resultMsg,
						Toast.LENGTH_SHORT).show();

			}

			if (AppConstants.temp_Context_No != null
					&& AppConstants.temp_Context_No.size() > 0) {
				listViewContext.setVisibility(View.VISIBLE);
				SimpleContextSelectedAdapter adyt = new SimpleContextSelectedAdapter(
						MainActivity.this, AppConstants.temp_Context_No,
						spnEast, spnnorth, photo_id, listViewContext,
						spnContext);
				listViewContext.setAdapter(adyt);

			} else {
				listViewContext.setVisibility(View.GONE);

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

			/*
			 * if(list.result==RESPONSE_RESULT.success) {
			 * AppConstants.temp_Context_No=null;
			 * Toast.makeText(con,"Uploaded Successfully"
			 * ,Toast.LENGTH_LONG).show();
			 * 
			 * Intent i = new Intent(con, MainActivity.class); /*i.putExtra("y",
			 * "yes"); // i.putExtra("imagePath", imagePath);
			 * i.putExtra("north", north1); i.putExtra("east", east1);
			 * i.putExtra("ctx", ctx_no); con.startActivity(i); }
			 */
			// Intent i=new Intent(context,ActivityGallery.class);
			// context.startActivity(i);
		}

		@Override
		public void release() {

		}
	}

	class GetContextList extends BaseTask {

		Context con;
		// Spinner Spneast;
		List<SimpleData> list;
		ProgressDialog progressDialog = null;
		// SimpleTextAdapter adp;
		// String north1,east1,img,photo_id;
		String ip_address = "", camval;

		// ArrayList<String>ctx_no=null;
		/*
		 * public addSinglePhotoTask(Context activityCamera, String north,
		 * String east, String imagePath,String ctx) { con=activityCamera;
		 * north1=north; east1=east; img=imagePath; ctx_no=ctx;
		 * 
		 * }
		 */
		public GetContextList(Context con) {
			this.con = con;
			// north1=north;
			// east1=east;
			// img=imagePath;
			// ctx_no=temp_Context_No;
			// photo_id=photoid;
			// camval=camval1;
		}

		@SuppressWarnings("unchecked")
		public SimpleData getData(int pos) {
			return null;
		}

		@Override
		protected void onPreExecute() {
			// bar.setVisibility(View.VISIBLE);
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			// progressDialog.setMessage("Getting image from server database");

			progressDialog.show();

			// dialog.show();

			DBHelper db = DBHelper.getInstance(MainActivity.this);
			db.open();
			ip_address = db.getIpAddress();
			db.close();
		}

		@Override
		protected Void doInBackground(String... params) {
			SimpleListFactory factory = SimpleListFactory.getInstance();
			list = factory.getContextList(ip_address, spnEast, spnnorth,
					photo_id);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			// Log.e("list", list.size()+" ");
			if (list != null && list.size() > 0) {
				// spnContext.setEnabled(true);
				spnContext.setVisibility(View.VISIBLE);
				SimpleStringAdapter asdad = new SimpleStringAdapter(con, list,
						spnEast, spnnorth, imagePath);
				spnContext.setAdapter(asdad);
				if (a == 0) {
					spnContext.performClick();
				}
				a = a + 1;
			} else {
				Toast.makeText(MainActivity.this,
						"There is no Context numbers for these areas",
						Toast.LENGTH_SHORT).show();
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

		}

		@Override
		public void release() {

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (AppConstants.temp_Context_No != null
				&& AppConstants.temp_Context_No.size() > 0) {

			listViewContext.setVisibility(View.VISIBLE);
			SimpleContextSelectedAdapter adyt = new SimpleContextSelectedAdapter(
					MainActivity.this, AppConstants.temp_Context_No, spnEast,
					spnnorth, photo_id, listViewContext, spnContext);
			listViewContext.setAdapter(adyt);

		} else {
			listViewContext.setVisibility(View.GONE);
		}

	}

}