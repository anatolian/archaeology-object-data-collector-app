package com.appenginedemo;

import module.common.constants.AppConstants;
import module.common.dialog.IPAddressDialog;
import module.image.property.ImagePropertyTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBase extends Activity {
	ImageView imageViewCamera, imageViewHome, imageViewFolder;
	RelativeLayout wrapper;
	TextView TextViewContext, TextView3d, TextViewSample,header;
	private static final int MENU_ITEM_1 = Menu.FIRST + 1;
    private static final int MENU_ITEM_2 = Menu.FIRST + 2;
    private static final int MENU_ITEM_3 = Menu.FIRST + 3;
    ImageView imageView1;
    public LinearLayout linearLayout2;
    Toast msg;
    ImagePropertyTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);

		wrapper = (RelativeLayout) findViewById(R.id.wrapper);
		TextViewContext = (TextView) findViewById(R.id.TextViewContext);
		TextView3d = (TextView) findViewById(R.id.TextView3d);
		TextViewSample = (TextView) findViewById(R.id.TextViewSample);
		header= (TextView) findViewById(R.id.textViewheader);
		imageView1= (ImageView) findViewById(R.id.imageView1);
		linearLayout2=(LinearLayout)findViewById(R.id.linearLayout2);
		TextViewContext.setTextColor(getResources().getColor(R.color.white));
		TextViewContext.setBackgroundColor(getResources().getColor(
				R.color.butterflyblue));
		
		
		
		
		TextViewContext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextViewContext.setTextColor(getResources().getColor(
						R.color.white));
				TextViewContext.setBackgroundColor(getResources().getColor(
						R.color.butterflyblue));
				TextView3d.setBackgroundColor(getResources().getColor(
						R.color.black));
				TextViewSample.setBackgroundColor(getResources().getColor(
						R.color.black));
				
				
			
				/*Intent i = new Intent(ActivityBase.this, MainActivity.class);
				startActivity(i);*/
				if(AppConstants.up==1)
				{
				Intent i = new Intent(ActivityBase.this, MainActivity.class);
				startActivity(i);
				}
				else
				{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityBase.this);
					LinearLayout myLayout = new LinearLayout(
							ActivityBase.this);
					myLayout.setOrientation(LinearLayout.VERTICAL);

					final TextView t1 = new TextView(
							ActivityBase.this);
					t1.setTextSize(15.0f);
					t1.setTextColor(Color.WHITE);
					t1.setPadding(10, 5, 10, 5);
					t1.setText("Photograph have not yet been Uploaded.\nAre you Sure You want to switch over the screen?");
					myLayout.addView(t1);

					alertDialogBuilder.setTitle("Alert");
					
					// set dialog message
					alertDialogBuilder
							.setView(myLayout)
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											Intent i = new Intent(ActivityBase.this, MainActivity.class);
											startActivity(i);
										
											
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											// if this button is
											// clicked, just
											// close
											// the dialog box and do
											// nothing
											TextView3d.setBackgroundColor(getResources().getColor(
													R.color.butterflyblue));

											TextViewContext.setBackgroundColor(getResources().getColor(
													R.color.black));
											TextViewSample.setBackgroundColor(getResources().getColor(
													R.color.black));
											dialog.cancel();
											//finish();
										}
									});

					AlertDialog alertDialog = alertDialogBuilder
							.create();

					// show it
					alertDialog.show();
				//	Toast.makeText(ActivityBase.this, "are you sure", Toast.LENGTH_SHORT).show();
				}
				
				
			}
		});

		TextView3d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TextView3d.setTextColor(getResources().getColor(R.color.white));
				TextView3d.setBackgroundColor(getResources().getColor(
						R.color.butterflyblue));

				TextViewContext.setBackgroundColor(getResources().getColor(
						R.color.black));
				TextViewSample.setBackgroundColor(getResources().getColor(
						R.color.black));
				//AppConstants.selectedImg = new ArrayList<String>();
			/*	Intent i = new Intent(ActivityBase.this, Activity_3d.class);
				startActivity(i);*/
			
					Intent i = new Intent(ActivityBase.this, Activity_3d.class);
					startActivity(i);
			
				/*if(AppConstants.up==1)
				{
				Intent i = new Intent(ActivityBase.this, Activity_3d.class);
				startActivity(i);
				}
				else
				{
					
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityBase.this);
					LinearLayout myLayout = new LinearLayout(
							ActivityBase.this);
					myLayout.setOrientation(LinearLayout.VERTICAL);

					final TextView t1 = new TextView(
							ActivityBase.this);
					t1.setTextSize(15.0f);
					t1.setTextColor(Color.WHITE);
					t1.setPadding(10, 5, 10, 5);
					t1.setText("Photograph have not yet been Uploaded.\nAre you Sure You want to switch over the screen?");
					myLayout.addView(t1);

					alertDialogBuilder.setTitle("Alert");
					
					// set dialog message
					alertDialogBuilder
							.setView(myLayout)
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											Intent i = new Intent(ActivityBase.this, Activity_3d.class);
											startActivity(i);
										
											
										}
									})
							.setNegativeButton("Cancel",
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
											//finish();
										}
									});

					AlertDialog alertDialog = alertDialogBuilder
							.create();

					// show it
					alertDialog.show();
					 // Toast.makeText(ActivityBase.this, "are you sure", Toast.LENGTH_SHORT).show();
				}*/
				
			}
		});
		TextViewSample.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				TextViewSample.setBackgroundColor(getResources().getColor(
						R.color.butterflyblue));

				TextView3d.setBackgroundColor(getResources().getColor(
						R.color.black));
				TextViewContext.setBackgroundColor(getResources().getColor(
						R.color.black));
			
				/*Intent i = new Intent(ActivityBase.this, Activity_Sample.class);
				startActivity(i);*/
				if(AppConstants.up==1)
				{
				Intent i = new Intent(ActivityBase.this, Activity_Sample.class);
				startActivity(i);
				}
				else
				{

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActivityBase.this);
					LinearLayout myLayout = new LinearLayout(
							ActivityBase.this);
					myLayout.setOrientation(LinearLayout.VERTICAL);

					final TextView t1 = new TextView(
							ActivityBase.this);
					t1.setTextSize(15.0f);
					t1.setTextColor(Color.WHITE);
					t1.setPadding(10, 5, 10, 5);
					t1.setText("Photograph have not yet been Uploaded.\nAre you Sure You want to switch over the screen?");
					myLayout.addView(t1);

					alertDialogBuilder.setTitle("Alert");
					
					// set dialog message
					alertDialogBuilder
							.setView(myLayout)
							.setCancelable(false)
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											Intent i = new Intent(ActivityBase.this, Activity_Sample.class);
											startActivity(i);
										
											
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int id) {
											// if this button is
											// clicked, just
											// close
											// the dialog box and do
											// nothing
											TextView3d.setBackgroundColor(getResources().getColor(
													R.color.butterflyblue));

											TextViewContext.setBackgroundColor(getResources().getColor(
													R.color.black));
											TextViewSample.setBackgroundColor(getResources().getColor(
													R.color.black));
											dialog.cancel();
											//finish();
										}
									});

					AlertDialog alertDialog = alertDialogBuilder
							.create();

					// show it
					alertDialog.show();
					//  Toast.makeText(ActivityBase.this, "are you sure ", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		/*
		 * imageViewFolder.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent i = new Intent(ActivityBase.this,
		 * MultiPhotoSelectActivity.class); startActivity(i); } });
		 * imageViewHome.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent i = new Intent(ActivityBase.this, ActivityHome.class);
		 * startActivity(i); } }); imageViewCamera.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent i = new Intent(ActivityBase.this, ActivityCamera.class);
		 * startActivity(i); } });
		 */
		
		
		
		imageView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openOptionsMenu();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
		
	     
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 
	        case R.id.menu_bookmark:
	        	
	        	IPAddressDialog d=new IPAddressDialog(ActivityBase.this);
	        	d.show();
	            //Toast.makeText(ActivityBase.this, "Setting", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.image_property:
	        	
	        	Intent i=new Intent(ActivityBase.this,ActivityImageProperty.class);
				startActivity(i);
				
	            
	        default:
	            return super.onOptionsItemSelected(item);
	 
		}
		
	}
	
	
	 
}