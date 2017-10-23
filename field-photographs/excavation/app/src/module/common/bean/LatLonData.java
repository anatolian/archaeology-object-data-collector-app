package module.common.bean;

public class LatLonData extends ResponseData{

	private static final long serialVersionUID = 1L;

	public double lat;
	public double lon;

	
	public void release(){
		super.release();
		callGC();
	}
	
	
}

