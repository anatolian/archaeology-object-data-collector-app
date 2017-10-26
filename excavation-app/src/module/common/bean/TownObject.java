package module.common.bean;

public class TownObject extends ResponseData {

	private static final long serialVersionUID = 1L;

	public String id = null;
	public String name = null;
	public double city_latitude = 0;
	public double city_longitude = 0;
	public String city_zip = null;

	public void release() {

		id = null;
		name = null;
		city_zip = null;
		city_latitude = 0;
		city_longitude = 0;

		super.release();
		callGC();
	}
}
