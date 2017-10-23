package module.common.bean;

public class StaticPage extends ResponseData {
	
	public StaticPage(){
		
	}

	private static final long serialVersionUID = 1L;
	
	public String id = null;
	public String name = null;
	public String sname = null;
	public String content = null;
	public String scontent = null;
	
	public void release() {
		
		id = null;	
		name = null;
		sname = null;
		content = null;
		scontent = null;
		
		super.release();
		callGC();
	}

}
