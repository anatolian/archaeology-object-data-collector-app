package module.common.bean;

import module.common.http.Response.RESPONSE_RESULT;


public class ResponseData implements DataInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RESPONSE_RESULT result;
	public String resultMsg; 
	public String email;
	public int status;
	
	/**
	 * Object Release Code
	 */	
	public void release(){
		
		result = null;
		resultMsg =  null;
		callGC();
	}
	
	public void callGC(){		
	}
}
