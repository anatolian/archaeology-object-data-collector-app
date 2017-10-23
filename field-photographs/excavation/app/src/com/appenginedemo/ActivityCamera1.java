package com.appenginedemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import module.common.constants.AppConstants;
import module.common.task.BaseTask;
import module.sample.AddSamplePhotoTask;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ActivityCamera1 extends ActivityBase {
	LayoutInflater inflater;
	RelativeLayout rlayout;
	ImageView cemera_image;
	static String imagePath;
	String samNo;
	String conNo;
	String material;
	String type;
	String sam;
	String north, east, img, act3d;
	BaseTask task;
	private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;

	// Activity request codes
		private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
		public static final int MEDIA_TYPE_IMAGE = 1;

		// directory name to store captured images and videos
		private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
		private Uri fileUri; // file url to store image/video

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		rlayout = (RelativeLayout) inflater.inflate(R.layout.activity_cemera,
				null);
		wrapper.addView(rlayout);
		linearLayout2.setVisibility(View.GONE);
		cemera_image = (ImageView) findViewById(R.id.cemera_image);
		if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
				|| getIntent().hasExtra("imagePath")) {
			north = getIntent().getExtras().getString("north");
			east = getIntent().getExtras().getString("east");
			img = getIntent().getExtras().getString("imagePath");
		}
		// Log.e("imagepath",imagePath.length()+" ");

		if (getIntent().hasExtra("samp_no") || getIntent().hasExtra("material")
				|| getIntent().hasExtra("type")
				|| getIntent().hasExtra("Context_no")
				|| getIntent().hasExtra("sam")) {
			samNo = getIntent().getExtras().getString("samp_no");
			material = getIntent().getExtras().getString("material");
			type = getIntent().getExtras().getString("type");
			conNo = getIntent().getExtras().getString("Context_no");
			sam = getIntent().getExtras().getString("sam");
		}

		if (getIntent().hasExtra("3d")) {
			act3d = getIntent().getExtras().getString("3d");
		}

		/*
		 * if (sam != null && sam.length() > 0) {
		 * //Log.e("select img ",AppConstants
		 * .SampleselectedImg.size()+"sam"+sam);
		 * if(AppConstants.SampleselectedImg!=null &&
		 * AppConstants.SampleselectedImg.size()>0) {
		 * //Log.e("select img ",img.length()+"sam"+sam); AddSamplePhotoTask
		 * task = new AddSamplePhotoTask(ActivityCamera1.this,
		 * east,north,AppConstants.SampleselectedImg,conNo,samNo,type);
		 * task.execute(); }
		 * 
		 * 
		 * }
		 */

		/*
		 * if (sam != null && sam.length() > 0) {
		 * //Log.e("select img ",AppConstants
		 * .SampleselectedImg.size()+"sam"+sam); if(img!=null && img.length()>0)
		 * { Log.e("select img ",img.length()+"sam"+sam); AddSamplePhotoTask
		 * task = new AddSamplePhotoTask(ActivityCamera1.this,
		 * east,north,img,conNo,samNo,type); task.execute(); }
		 * 
		 * 
		 * }
		 */
		/*
		 * if(img!=null && img.length()>0) {
		 * //Log.e("imagepath innnnnnnn",imagePath.length()+" ");
		 * 
		 * task=new addSinglePhotoTask(ActivityCamera1.this,north,east,img);
		 * task.execute(); }
		 */
//		try {
//			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			// we will handle the returned data in
//			// onActivityResult
//			startActivityForResult(captureIntent, CAMERA_CAPTURE);
//
//		} catch (ActivityNotFoundException anfe) {
//			// display an error message
//			String errorMessage = "Whoops - your device doesn't support capturing images!";
//			Toast toast = Toast.makeText(ActivityCamera1.this, errorMessage,
//					Toast.LENGTH_SHORT);
//			toast.show();
//			ActivityCamera1.this.finish();
//		}
		capture_image();
	}

	public void release() {
	}
	public void capture_image() {
//		try {
//			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			// we will handle the returned data in
//			// onActivityResult
//			startActivityForResult(captureIntent, CAMERA_CAPTURE);
//
//		} catch (ActivityNotFoundException anfe) {
//			// display an error message
//			String errorMessage = "Whoops - your device doesn't support capturing images!";
//			Toast toast = Toast.makeText(MainActivity.this, errorMessage,Toast.LENGTH_SHORT);
//			toast.show();
//
//		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				//previewCapturedImage();
				

				try {
					Bitmap thumbnail=decodeUri(fileUri);
					cemera_image.setVisibility(View.VISIBLE);
					cemera_image.setImageBitmap(thumbnail);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//			
				
				//	Log.e("sam", "smple" + "");
					if (sam != null && sam.length() > 0) {

						if (AppConstants.SampleselectedImg != null
								&& AppConstants.SampleselectedImg.size() > 0) {

							if (imagePath != null && imagePath.length() > 0) {
								Log.e("array image size > 0", imagePath);
								AppConstants.SampleselectedImg.add(imagePath);
							}

						} else {
							AppConstants.SampleselectedImg = new ArrayList<String>();
							if (imagePath != null && imagePath.length() > 0) {
								Log.e("array image size is 0", imagePath);
								AppConstants.SampleselectedImg.add(imagePath);
							}
						}

						if (AppConstants.SampleselectedImg != null
								&& AppConstants.SampleselectedImg.size() > 0) { // Log.e("select img ",img.length()+"sam"+sam);
							if (east != null && east.length() > 0 && north != null
									&& north.length() > 0 && conNo != null
									&& conNo.length() > 0 && samNo != null
									&& samNo.length() > 0 && type != null
									&& type.length() > 0) {

								AddSamplePhotoTask task = new AddSamplePhotoTask(
										ActivityCamera1.this, east, north,
										AppConstants.SampleselectedImg, conNo, samNo,
										type);
								task.execute();
							}
							

						}

						//Log.e("imagepath for sample in camera", imagePath + "");
						/*
						 * if (imagePath != null && imagePath.length() > 0) {
						 * 
						 * Intent i = new Intent(ActivityCamera1.this,
						 * Activity_Sample.class); i.putExtra("samp_no", samNo);
						 * i.putExtra("Context_no", conNo); i.putExtra("material",
						 * material); i.putExtra("type", type); i.putExtra("imagePath",
						 * imagePath); i.putExtra("north", north); i.putExtra("east",
						 * east); startActivity(i); /* Intent i=new
						 * Intent(ActivityCamera1.this,MainActivity.class);
						 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
						 * i.putExtra("north",north); i.putExtra("east",east);
						 * startActivity(i);
						 * 
						 * 
						 * }
						 */

					} else {
						if (AppConstants.selectedImg != null
								&& AppConstants.selectedImg.size() > 0) {

							if (imagePath != null && imagePath.length() > 0) {
								AppConstants.selectedImg.add(imagePath);
							}

						} else {
							AppConstants.selectedImg = new ArrayList<String>();
							if (imagePath != null && imagePath.length() > 0) {
								AppConstants.selectedImg.add(imagePath);
							}
						}

					//	Log.e("3ddddd", "3dddddd");
						if (imagePath != null && imagePath.length() > 0) {
							finish();
							Intent i = new Intent(ActivityCamera1.this,
									Activity_3d.class);
							i.putExtra("y", "yes");
							i.putExtra("imagePath", imagePath);
							i.putExtra("north", north);
							i.putExtra("east", east);
							startActivity(i);
							/*
							 * Intent i=new
							 * Intent(ActivityCamera1.this,MainActivity.class);
							 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
							 * i.putExtra("north",north); i.putExtra("east",east);
							 * startActivity(i);
							 */
						}
					}
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}
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


	/*
	 * Display image from a path to ImageView
	 */
	private void previewCapturedImage() {
		try {
			// hide video preview
			//videoPreview.setVisibility(View.GONE);

			cemera_image.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 1;

			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);

			cemera_image.setImageBitmap(bitmap);
			
			
		//	Log.e("sam", "smple" + "");
			if (sam != null && sam.length() > 0) {

				if (AppConstants.SampleselectedImg != null
						&& AppConstants.SampleselectedImg.size() > 0) {

					if (imagePath != null && imagePath.length() > 0) {
						AppConstants.SampleselectedImg.add(imagePath);
					}

				} else {
					AppConstants.SampleselectedImg = new ArrayList<String>();
					if (imagePath != null && imagePath.length() > 0) {
						AppConstants.SampleselectedImg.add(imagePath);
					}
				}

				if (AppConstants.SampleselectedImg != null
						&& AppConstants.SampleselectedImg.size() > 0) { // Log.e("select img ",img.length()+"sam"+sam);
					if (east != null && east.length() > 0 && north != null
							&& north.length() > 0 && conNo != null
							&& conNo.length() > 0 && samNo != null
							&& samNo.length() > 0 && type != null
							&& type.length() > 0) {

						AddSamplePhotoTask task = new AddSamplePhotoTask(
								ActivityCamera1.this, east, north,
								AppConstants.SampleselectedImg, conNo, samNo,
								type);
						task.execute();
					}
					

				}

				//Log.e("imagepath for sample in camera", imagePath + "");
				/*
				 * if (imagePath != null && imagePath.length() > 0) {
				 * 
				 * Intent i = new Intent(ActivityCamera1.this,
				 * Activity_Sample.class); i.putExtra("samp_no", samNo);
				 * i.putExtra("Context_no", conNo); i.putExtra("material",
				 * material); i.putExtra("type", type); i.putExtra("imagePath",
				 * imagePath); i.putExtra("north", north); i.putExtra("east",
				 * east); startActivity(i); /* Intent i=new
				 * Intent(ActivityCamera1.this,MainActivity.class);
				 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
				 * i.putExtra("north",north); i.putExtra("east",east);
				 * startActivity(i);
				 * 
				 * 
				 * }
				 */

			} else {
				if (AppConstants.selectedImg != null
						&& AppConstants.selectedImg.size() > 0) {

					if (imagePath != null && imagePath.length() > 0) {
						AppConstants.selectedImg.add(imagePath);
					}

				} else {
					AppConstants.selectedImg = new ArrayList<String>();
					if (imagePath != null && imagePath.length() > 0) {
						AppConstants.selectedImg.add(imagePath);
					}
				}

			//	Log.e("3ddddd", "3dddddd");
				if (imagePath != null && imagePath.length() > 0) {
					finish();
					Intent i = new Intent(ActivityCamera1.this,
							Activity_3d.class);
					i.putExtra("y", "yes");
					i.putExtra("imagePath", imagePath);
					i.putExtra("north", north);
					i.putExtra("east", east);
					startActivity(i);
					/*
					 * Intent i=new
					 * Intent(ActivityCamera1.this,MainActivity.class);
					 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
					 * i.putExtra("north",north); i.putExtra("east",east);
					 * startActivity(i);
					 */
				}
			}
//			if (replcae) {
//				task=new ReplacePhotoMain();
//				task.execute();
////			}else{
////				task=new GetContextList(MainActivity.this);
////				task.execute();
////			}
//			}else{
//				task = new addSinglePhotoTaskMain();
//				task.execute();
//			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream( getContentResolver().openInputStream(selectedImage), null, o);

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
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
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
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if(!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSSS",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
//			Log.e("ImagePAth",mediaStorageDir.getPath() + File.separator
//					+ "IMG_" + timeStamp + ".jpg");
			Log.e("ImagePath", mediaFile.getPath());
			imagePath=mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg";
			
			/*
			 * if (imagePath != null && imagePath.length() > 0) {
			 * 
			 * Intent i = new Intent(ActivityCamera1.this, Activity_3d.class);
			 * i.putExtra("y", "yes"); i.putExtra("imagePath", imagePath);
			 * i.putExtra("north", north); i.putExtra("east", east);
			 * startActivity(i); /* Intent i=new
			 * Intent(ActivityCamera1.this,MainActivity.class);
			 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
			 * i.putExtra("north",north); i.putExtra("east",east);
			 * startActivity(i);
			 * 
			 * }
			 */
			// profile_image.setVisibility(View.VISIBLE);
			// data.getExtras().
			// File a=new File();
		
//		} else if (type == MEDIA_TYPE_VIDEO) {
//			mediaFile = new File(mediaStorageDir.getPath() + File.separator
//					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
//				&& data != null) {
//			Uri selectedImage = data.getData();
//			String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//			Cursor cursor = getContentResolver().query(selectedImage,
//					filePathColumn, null, null, null);
//			cursor.moveToFirst();
//
//			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//			imagePath = cursor.getString(columnIndex);
//
//			ApplicationHandler apphand = ApplicationHandler.getInstance();
//			cemera_image
//					.setImageBitmap(apphand.decodeFile(new File(imagePath)));
//			// user is returning from capturing an image using the camera
//
//			Log.e("imagepath load", imagePath.length() + " ");
//
//		}
//		if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
//
//			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//			cemera_image.setImageBitmap(thumbnail);
//			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//			thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//			File file = new File(Environment.getExternalStorageDirectory()
//					+ File.separator + "image.jpg");
//			try {
//				file.createNewFile();
//				FileOutputStream fo = new FileOutputStream(file);
//				fo.write(bytes.toByteArray());
//				fo.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			imagePath = Environment.getExternalStorageDirectory()
//					+ File.separator + "image.jpg";
//			Log.e("imagepath", imagePath + " ");
//
//			/*
//			 * if (AppConstants.selectedImg != null &&
//			 * AppConstants.selectedImg.size() > 0) {
//			 * 
//			 * if (imagePath != null && imagePath.length() > 0) {
//			 * AppConstants.selectedImg.add(imagePath); }
//			 * 
//			 * } else { AppConstants.selectedImg = new ArrayList<String>(); if
//			 * (imagePath != null && imagePath.length() > 0) {
//			 * AppConstants.selectedImg.add(imagePath); } }
//			 */
//			Log.e("sam", "smple" + "");
//			if (sam != null && sam.length() > 0) {
//
//				if (AppConstants.SampleselectedImg != null
//						&& AppConstants.SampleselectedImg.size() > 0) {
//
//					if (imagePath != null && imagePath.length() > 0) {
//						AppConstants.SampleselectedImg.add(imagePath);
//					}
//
//				} else {
//					AppConstants.SampleselectedImg = new ArrayList<String>();
//					if (imagePath != null && imagePath.length() > 0) {
//						AppConstants.SampleselectedImg.add(imagePath);
//					}
//				}
//
//				if (AppConstants.SampleselectedImg != null
//						&& AppConstants.SampleselectedImg.size() > 0) { // Log.e("select img ",img.length()+"sam"+sam);
//					if (east != null && east.length() > 0 && north != null
//							&& north.length() > 0 && conNo != null
//							&& conNo.length() > 0 && samNo != null
//							&& samNo.length() > 0 && type != null
//							&& type.length() > 0) {
//
//						AddSamplePhotoTask task = new AddSamplePhotoTask(
//								ActivityCamera1.this, east, north,
//								AppConstants.SampleselectedImg, conNo, samNo,
//								type);
//						task.execute();
//					}
//					
//
//				}
//
//				Log.e("imagepath for sample in camera", imagePath + "");
//				/*
//				 * if (imagePath != null && imagePath.length() > 0) {
//				 * 
//				 * Intent i = new Intent(ActivityCamera1.this,
//				 * Activity_Sample.class); i.putExtra("samp_no", samNo);
//				 * i.putExtra("Context_no", conNo); i.putExtra("material",
//				 * material); i.putExtra("type", type); i.putExtra("imagePath",
//				 * imagePath); i.putExtra("north", north); i.putExtra("east",
//				 * east); startActivity(i); /* Intent i=new
//				 * Intent(ActivityCamera1.this,MainActivity.class);
//				 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
//				 * i.putExtra("north",north); i.putExtra("east",east);
//				 * startActivity(i);
//				 * 
//				 * 
//				 * }
//				 */
//
//			} else {
//				if (AppConstants.selectedImg != null
//						&& AppConstants.selectedImg.size() > 0) {
//
//					if (imagePath != null && imagePath.length() > 0) {
//						AppConstants.selectedImg.add(imagePath);
//					}
//
//				} else {
//					AppConstants.selectedImg = new ArrayList<String>();
//					if (imagePath != null && imagePath.length() > 0) {
//						AppConstants.selectedImg.add(imagePath);
//					}
//				}
//
//				Log.e("3ddddd", "3dddddd");
//				if (imagePath != null && imagePath.length() > 0) {
//					finish();
//					Intent i = new Intent(ActivityCamera1.this,
//							Activity_3d.class);
//					i.putExtra("y", "yes");
//					i.putExtra("imagePath", imagePath);
//					i.putExtra("north", north);
//					i.putExtra("east", east);
//					startActivity(i);
//					/*
//					 * Intent i=new
//					 * Intent(ActivityCamera1.this,MainActivity.class);
//					 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
//					 * i.putExtra("north",north); i.putExtra("east",east);
//					 * startActivity(i);
//					 */
//				}
//			}
//			/*
//			 * if (imagePath != null && imagePath.length() > 0) {
//			 * 
//			 * Intent i = new Intent(ActivityCamera1.this, Activity_3d.class);
//			 * i.putExtra("y", "yes"); i.putExtra("imagePath", imagePath);
//			 * i.putExtra("north", north); i.putExtra("east", east);
//			 * startActivity(i); /* Intent i=new
//			 * Intent(ActivityCamera1.this,MainActivity.class);
//			 * i.putExtra("y","yes"); i.putExtra("imagePath",imagePath);
//			 * i.putExtra("north",north); i.putExtra("east",east);
//			 * startActivity(i);
//			 * 
//			 * }
//			 */
//			// profile_image.setVisibility(View.VISIBLE);
//			// data.getExtras().
//			// File a=new File();
//		}
//	}
}
