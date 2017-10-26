package module.common.constants;

import java.io.File;
import java.util.ArrayList;

import module.common.bean.SimpleData;

public class AppConstants {

	public static final String TAG_APP = "TAM";
	
	public static final int INT_STATUS_SUCCESS = 1;
	
	public static final int INT_STATUS_FAILED_DOWNLOAD = -10;
	public static final int INT_STATUS_FAILED_CLIENT = -11;
	public static final int INT_STATUS_FAILED_TIMEOUT = -13;
	public static final int INT_STATUS_FAILED_IO = -12;
	public static int INT_PRICE_TOTAL =0;
	
	
	public static  int spnEastpos=0;
	public static  int spnNorthpos=0;
	public static  int spnTypepos=0;
	public static  int spnConNopos=0;
	public static  int spnSamNopos=0;
	public static  int spnPlacement=0;
	
	public static int acvity_3dSpnNorth=0;
	public static int activity_3dSpnEast=0;
	
	public static int activity_mainSpnNorth=0;
	public static int activity_mainSpnEast=0;
	
	
	
	
	
	public static final String PATH_SEPERATOR = File.separator;
	
	public static final long A_SECOND = 1000;
	public static final long A_MINUTE = A_SECOND * 60;
	public static final long LOCATION_UPDATE_INTERVAL = 5 * A_MINUTE;
	
	public static final String DEFAULT_ID = "-100";
	//public static String temp_Context_No = "0";
	public static ArrayList<String> temp_Context_No=null;
	public static ArrayList<String> contextno=null;
	public static String  ipaddress=null;
	
	public static final String NO_LIMITATION = "0";
	
	public static final int MAX_ZOOM_LEVEL = 8;

	public static final String COUNTRY_ID="-100";
	
	public static final String COUNTRY_NAME="-State-";

	public static String net_error="0";
	public static String net_error_msg="0";

	public static String id;

	public static int counter=-1;

	public static String USER_ID=null;

	public static String img=null;
	public static int up=0; 
	public static String areaeast; 
	public static int internet=0; 
	public static ArrayList<String> selectedImg=null;
	public static ArrayList<String> SampleselectedImg=null;
		public enum PAGINATION{
		First_Load,
		Previous,
		Next
	}	
	public enum IMAGES{
		ProductImages1,
		eventImages1,category;
	}
	
	
}
