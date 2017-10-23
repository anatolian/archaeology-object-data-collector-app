package module.common.bean;

import java.util.Date;

public class SelectedDateData extends ResponseData {

	private static final long serialVersionUID = 1L;
	
	
	public int week;
	public int month;
	public int year;
	public Date date;

	public void release() {
		super.release();

		week = 0;
		month = 0;
		year = 0;
		date = null;

		callGC();
	}

}
