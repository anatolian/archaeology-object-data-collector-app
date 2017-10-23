package module.common.bean;

public class ShippingData extends ResponseData{

	private static final long serialVersionUID = 1L;

	
	
	public String first_name=null;
	public String last_name=null;
	public String address=null;
	public String city=null;
	public String state=null;
	public String country;
	public String post_code=null;
	public String phone=null;
	public String order_id=null;
	
	public void release(){
		
		
		first_name=null;
		  last_name=null;
		  address=null;
		 city=null;
		 state=null;
		 country=null;
		  post_code=null;
		  phone=null;
		  order_id=null;
		
		
		super.release();
		callGC();
	}
}
