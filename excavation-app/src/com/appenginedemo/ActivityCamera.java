package com.appenginedemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import module.common.application.ApplicationHandler;
import module.common.constants.AppConstants;
import module.common.task.BaseTask;
import module.context.addSinglePhotoTask;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ActivityCamera extends ActivityBase {
	LayoutInflater inflater;
	RelativeLayout rlayout;
	ImageView cemera_image;
	String imagePath, photo_id;
	String north, east, img, nxt, act3d,pic;
	BaseTask task;
	ArrayList<String> ctx_no;
	private static int RESULT_LOAD_IMAGE = 1, CAMERA_CAPTURE = 999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		rlayout = (RelativeLayout) inflater.inflate(R.layout.activity_cemera,
				null);
		wrapper.addView(rlayout);
		cemera_image = (ImageView) findViewById(R.id.cemera_image);
		if (getIntent().hasExtra("north") || getIntent().hasExtra("east")
				|| getIntent().hasExtra("imagePath")
				|| getIntent().hasExtra("ctx")) {
			north = getIntent().getExtras().getString("north");
			east = getIntent().getExtras().getString("east");
			img = getIntent().getExtras().getString("imagePath");
			ctx_no= getIntent().getExtras().getStringArrayList("ctx");
			//ctx_no = getIntent().getExtras().getString("ctx");
		}

		if (getIntent().hasExtra("3d")) {
			act3d = getIntent().getExtras().getString("3d");
		}

		if (getIntent().hasExtra("pic")) {
			pic = getIntent().getExtras().getString("pic");
		}
		if (getIntent().hasExtra("next")) {
			nxt = getIntent().getExtras().getString("next");
		}
		
		if (getIntent().hasExtra("photo_id")) {
			photo_id = getIntent().getExtras().getString("photo_id");
		}
		
		// Log.e("imagepath",imagePath.length()+" ");
		if (nxt != null && nxt.length() > 0) {
			// Log.e("imagepath innnnnnnn",imagePath.length()+" ");

		/*	if (img != null && img.length() > 0) {

				task = new addSinglePhotoTask(ActivityCamera.this, north, east,
						img, ctx_no);
				task.execute();

			}*/
		}

		try {
			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// we will handle the returned data in
			// onActivityResult
			startActivityForResult(captureIntent, CAMERA_CAPTURE);

		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support capturing images!";
			Toast toast = Toast.makeText(ActivityCamera.this, errorMessage,
					Toast.LENGTH_SHORT);
			toast.show();
			ActivityCamera.this.finish();
		}
	}

	public void release() {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			imagePath = cursor.getString(columnIndex);

			ApplicationHandler apphand = ApplicationHandler.getInstance();
			cemera_image
					.setImageBitmap(apphand.decodeFile(new File(imagePath)));
			// user is returning from capturing an image using the camera

			//Log.e("imagepath load", imagePath.length() + " ");

		}
		if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {

			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			cemera_image.setImageBitmap(thumbnail);
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "image.jpg");
			try {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				fo.write(bytes.toByteArray());
				fo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			imagePath = Environment.getExternalStorageDirectory()
					+ File.separator + "image.jpg";

			/*
			 * AppConstants.selectedImg = new ArrayList<String>();
			 * 
			 * if (act3d != null && act3d.length() > 0) { if (imagePath != null)
			 * { AppConstants.selectedImg.add(imagePath); } }
			 */
			//Log.e("imagepath", imagePath + " ");

			/*
			 * if (act3d != null && act3d.length() > 0) { Intent i = new
			 * Intent(ActivityCamera.this, Activity_3d.class); i.putExtra("y",
			 * "yes"); i.putExtra("imagePath", imagePath); //
			 * i.putExtra("north",north); // i.putExtra("east",east);
			 * startActivity(i); } else {
			 */
			
			
			task = new addSinglePhotoTask(ActivityCamera.this, north, east,
					imagePath, AppConstants.temp_Context_No,photo_id,pic);
			task.execute();
			
			
			/*if (imagePath != null && imagePath.length() > 0) {
				finish();
				Intent i = new Intent(ActivityCamera.this, MainActivity.class);
				i.putExtra("y", "yes");
				i.putExtra("imagePath", imagePath);
				i.putExtra("north", north);
				i.putExtra("east", east);
				i.putExtra("ctx", ctx_no);
				i.putExtra("next", nxt);
				i.putExtra("pic", pic);
				startActivity(i);
				 }*/
			}

		}
	}

