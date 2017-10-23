package module.common.bean;




public class SimpleData extends ResponseData{

	private static final long serialVersionUID = 1L;

	public String id = null;
	public String name = null;
	public String img=null;
	public String east=null;
	public String north=null;
	public String samNo=null;
	public String conNo=null;
	public String image_path=null;
	public String material=null;
	
	public String count = "0";

	public String state_code;

	public String photowidth,photoheight;
	
	
	public void release(){
		
		id = null;
		name = null;
		image_path=null;
		
		super.release();
		callGC();
	}
}
