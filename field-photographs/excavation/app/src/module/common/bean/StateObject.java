package module.common.bean;

public class StateObject extends ResponseData {

	private static final long serialVersionUID = 1L;

	public String id = null;
	public String name = null;
	public String stateCode = null;

	public void release() {

		id = null;
		name = null;
		stateCode = null;

		super.release();
		callGC();
	}
}
