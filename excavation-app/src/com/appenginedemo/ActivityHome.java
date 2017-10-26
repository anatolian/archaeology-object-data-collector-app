package com.appenginedemo;

import module.all.GetAllImageTask;
import module.common.task.BaseTask;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class ActivityHome extends ActivityBase
{
	LayoutInflater inflater;
	RelativeLayout rlayout;
	GridView gridview;
	BaseTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		inflater = getLayoutInflater();
		rlayout = (RelativeLayout) inflater.inflate(R.layout.activity_category, null);
		wrapper.addView(rlayout);
		 
		gridview=(GridView)findViewById(R.id.gridView_category);
		
		task=new GetAllImageTask(ActivityHome.this,gridview);
		task.execute();
		
	}
	public void release() 
	{		
	}
}
