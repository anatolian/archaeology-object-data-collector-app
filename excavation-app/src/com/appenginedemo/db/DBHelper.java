package com.appenginedemo.db;

import java.util.ArrayList;
import java.util.List;

import module.image.property.ImagePropertyBean;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.LabeledIntent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper 
{

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	static DBHelper dbHelper;
	SQLiteDatabase database;

	final static String DB_NAME = "user.db";
	final static int DB_VERSION = 3;

	static final String TABLE_SERVER_DETAIL = "user_detail";

	static final String IP_ADDRESS = "ip_address";
	
	static final String DATABASE_SERVER = "create table " + TABLE_SERVER_DETAIL
			+ "(" + IP_ADDRESS + " text);";
	
	static final String TABLE_IMAGE_PROPERTY="image_property";
	
	//IMAGE_PROPERTY TABLE COLUMNS
	
	static final String CONTEXT_SUBPATH_3D="context_subpath_3d";
	static final String BASE_IMAGE_PATH="base_image_path";
	static final String CONTEXT_SUBPATH="context_subpath";
	static final String AREA_DIVIDER="sample_label_area_divider";
	static final String CONTEXT_DIVIDER="sample_label_context_divider";
	static final String SAMPLE_LABEL_FONT="sample_label_font";
	static final String SAMPLE_LABEL_FONT_SIZE="sample_label_font_size";
	static final String SAMPLE_LABEL_PLACEMENT="sample_label_placement";
	static final String SAMPLE_DIVIDER="sample_label_sample_divider";
	static final String SAMPLE_SUBPATH="sample_subpath";
	//static final String CONTEXT_SUBPATH_3D1="context_subpath_3d1";
	
	
	static final String CREATE_TABLE_IMAGE_PROPERTY="create table "+TABLE_IMAGE_PROPERTY
			+ "(" + CONTEXT_SUBPATH_3D + " text,"
			+ BASE_IMAGE_PATH + " text,"
			+ CONTEXT_SUBPATH +" text,"
			+ AREA_DIVIDER + " text,"
			+ CONTEXT_DIVIDER + " text,"
			+ SAMPLE_LABEL_FONT + " text,"
			+ SAMPLE_LABEL_FONT_SIZE + " text,"
			+ SAMPLE_LABEL_PLACEMENT + " text,"
			+ SAMPLE_DIVIDER + " text,"
			+ SAMPLE_SUBPATH + " text);";
			
	
	
	public boolean deleteServerDetail() {
		database.delete(TABLE_SERVER_DETAIL, null, null);
		
		return true;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_SERVER);
		db.execSQL(CREATE_TABLE_IMAGE_PROPERTY);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER_DETAIL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_PROPERTY);
		
		onCreate(db);
	}

	public boolean addServerAddress(String d) {

		ContentValues values = new ContentValues();
		values.put(IP_ADDRESS, d);
	
		//Log.e("ADDES", "addesd");
		database.insert(TABLE_SERVER_DETAIL, null, values);

		return true;
	}

	public boolean deleteUser() {

		database.delete(TABLE_SERVER_DETAIL, null, null);
		/*
		 * database.delete(TABLE_DRUG_DETAIL, null,null);
		 * database.delete(TABLE_DRUG_ADULT_INDICATION, null,null);
		 * database.delete(TABLE_CONTRADICTION, null,null);
		 * database.delete(TABLE_PHARMA, null,null);
		 * database.delete(TABLE_ADVERSE, null,null);
		 */
		return true;

	}

	public String getIpAddress() {
			String d="";
		
		Cursor cursor = database.query(TABLE_SERVER_DETAIL, new String[] {
				IP_ADDRESS }, null,null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			d = cursor.getString(0);
		
			
			cursor.close();
			return d;

		}

		cursor.close();

		return d;

	}
	
	public boolean addImageProperty(ImagePropertyBean data){
	try{
		
		
		ContentValues values=new ContentValues();
		values.put(CONTEXT_SUBPATH_3D,data.context_subpath_3d);
		values.put(BASE_IMAGE_PATH, data.base_image_path);
		values.put(CONTEXT_SUBPATH,  data.context_subpath);
		values.put(AREA_DIVIDER,  data.sample_label_area_divider);
		values.put(CONTEXT_DIVIDER, data.sample_label_context_divider);
		values.put(SAMPLE_LABEL_FONT,  data.sample_label_font);
		values.put(SAMPLE_LABEL_FONT_SIZE, data.sample_label_font_size);
		values.put(SAMPLE_LABEL_PLACEMENT, data.sample_label_placement);
		values.put(SAMPLE_DIVIDER, data.sample_label_sample_divider);
		values.put(SAMPLE_SUBPATH,  data.sample_subpath);
		database.insert(TABLE_IMAGE_PROPERTY, null, values);
		return true;
	}catch(Exception ex){
		    ex.printStackTrace();
			return false;
		}
	}
	
	
	public boolean deleteImageProperty(){
		try{
		database.delete(TABLE_IMAGE_PROPERTY, null, null);
		return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public boolean updateImageProperty(String _3dsubpath,String base_image_path,
			   String context_subpath,String area_divider,String context_divider,
			   String font_size,String item,String sample_divider,String sample_subpath
			   ){
		try{
		ContentValues values=new ContentValues();
		values.put(CONTEXT_SUBPATH_3D,_3dsubpath);
		values.put(BASE_IMAGE_PATH, base_image_path);
		values.put(CONTEXT_SUBPATH,  context_subpath);
		values.put(AREA_DIVIDER,  area_divider);
		values.put(CONTEXT_DIVIDER, context_divider);
		//values.put(SAMPLE_LABEL_FONT,  sample_label_font);
		values.put(SAMPLE_LABEL_FONT_SIZE, font_size);
		values.put(SAMPLE_LABEL_PLACEMENT, item);
		values.put(SAMPLE_DIVIDER, sample_divider);
		values.put(SAMPLE_SUBPATH,  sample_subpath);	
		database.update(TABLE_IMAGE_PROPERTY, values, null, null);
		return true;
	}
		catch(Exception ex){
		    ex.printStackTrace();
			return false;
		}
	}
		
	
	
	public ImagePropertyBean getImageProperty(){
		
		ImagePropertyBean data=new ImagePropertyBean();
		String sql=" select * from " + TABLE_IMAGE_PROPERTY;
		Cursor cur=database.rawQuery(sql,null);
		cur.moveToFirst();
		while(!cur.isAfterLast()){
			
			data.context_subpath_3d=cur.getString(0);
			data.base_image_path=cur.getString(1);
			data.context_subpath=cur.getString(2);
			data.sample_label_area_divider=cur.getString(3);
			data.sample_label_context_divider=cur.getString(4);
			data.sample_label_font=cur.getString(5);
			data.sample_label_font_size=cur.getString(6);
			data.sample_label_placement=cur.getString(7);
			data.sample_label_sample_divider=cur.getString(8);
			data.sample_subpath=cur.getString(9);
			cur.close();
			return data;
		}
		cur.close();
		return data;
		
	}
	
	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null) {
			database.close();
		}
	}

	public static DBHelper getInstance(Context context) {

		if (dbHelper == null) {
			dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
		}
		return dbHelper;
	}

	
}
