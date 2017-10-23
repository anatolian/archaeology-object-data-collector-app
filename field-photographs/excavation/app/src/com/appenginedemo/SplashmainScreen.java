 package com.appenginedemo;

import com.appenginedemo.db.DBHelper;

import module.common.constants.AppConstants;
import module.common.dialog.IPAddressDialog;
import module.image.property.ImagePropertyTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashmainScreen extends Activity {
	String ip_address;
	ImagePropertyTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splas);
		
		DBHelper db = DBHelper.getInstance(SplashmainScreen.this);
		db.open();
		ip_address = db.getIpAddress();
		db.deleteImageProperty();
		db.close();
		
		
		
		if(ip_address!=null && ip_address.length()>0)
		{
			Intent i=new Intent(SplashmainScreen.this,MainActivity.class);
			startActivity(i);
		}
		else
		{
			IPAddressDialog d = new IPAddressDialog(SplashmainScreen.this);
			d.show();
			d.setCancelable(false);
			d.setCanceledOnTouchOutside(false);
		}
		
		
		
//		d.setOnCancelListener(new OnCancelListener() {
//			
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				// TODO Auto-generated method stub
//			Intent i=new Intent(SplashmainScreen.this,MainActivity.class);
//			startActivity(i);
//			}
//		});
//		
//		d.setOnDismissListener(new OnDismissListener() {
//			
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				// TODO Auto-generated method stub
//				Intent i=new Intent(SplashmainScreen.this,MainActivity.class);
//				startActivity(i);
//			}
//		});
	}
	
}
