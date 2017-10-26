package module.common.bean;

public class SettingsBean extends ResponseData{

	private static final long serialVersionUID = 1L;

	public int flag_sms,flag_vibrate;
	public String alerm_tone=null,sms_text=null;

	
	public void release(){
		super.release();
		callGC();
	}
	
	
}

