package module.common.application;

import module.common.adapter.ViewType;

@SuppressWarnings("rawtypes")
public class Session {

	private static Session session;

	private Session() {
	}

	public static Session getInstance() {
		if (session == null) {
			session = new Session();
		}
		return session;
	}
/*
	private LoginData login = null;

	public void setUser(LoginData t) {
		login = t;
	}

	public LoginData getUser() {
		return login;
	}
*/
	private String catid;

	public void setCatId(String t) {
		catid = t;
	}

	public String getCatId() {
		return catid;
	}

	private String catname;

	public void setCatName(String t) {
		catname = t;
	}

	public String getCatName() {
		return catname;
	}

	private Class screen;

	public void setScreen(Class t) {
		screen = t;
	}

	public Class getScreen() {
		return screen;
	}

	private String searchText;

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSearchText() {
		return searchText;
	}

	public ViewType addressViewType;
	public String destinationAddress;

	public void reset() {
		screen = null;
		searchText = null;
		addressViewType = null;
		destinationAddress = null;

	}

	public void release() {

		reset();

	}

	private String email;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	private String lang;

	public void setLanguage(String lang) {
		this.lang = lang;
	}

	public String getLanguage() {
		return lang;
	}

	private int START = 0;
	private int START_PARAM = START;
	private boolean previousFlag = true;
	private boolean nextFlag = true;

	private int NEXT = START;
	private int PREVIOUS = START;

	public void setStartParam(int value) {
		START_PARAM = value;
	}

	public int getStartParam() {
		return START_PARAM;
	}

	public void setNext(int next) {
		NEXT = next;
	}

	public int getNext() {
		return NEXT;
	}

	public void setPrevious(int previous) {
		PREVIOUS = previous;
	}

	public int getPrevious() {
		return PREVIOUS;
	}

	public void setPreviousFlag(boolean value) {
		previousFlag = value;
	}

	public boolean getPreviousFlag() {
		return previousFlag;
	}

	public void setNextFlag(boolean value) {
		nextFlag = value;
	}

	public boolean getNextFlag() {
		return nextFlag;
	}

	public void loadFirst(int startValue, int size) {

		if (size > 0 && size % 10 == 0) {
			setNext(startValue + size);
			setPrevious(startValue);

			setPreviousFlag(false);
			setNextFlag(true);

		} else {

			setPreviousFlag(false);
			setNextFlag(false);
		}

	}

	public void clickNext(int startValue, int size) {

		if (size > 0 && size % 10 == 0) {
			setPrevious(startValue);
			setNext(startValue + size);

			setPreviousFlag(true);
			setNextFlag(true);

		} else {
			setPreviousFlag(true);
			setNextFlag(false);
		}

	}

	public void clickPrevious(int startValue, int size) {

		if (startValue > 0 && startValue % 10 == 0) {
			setPrevious(startValue - size);
			setNext(startValue);

			setPreviousFlag(true);
			setNextFlag(true);
		} else {
			setPreviousFlag(false);
			setNextFlag(true);
		}

	}

	public void resetNav() {

		START_PARAM = START;
		previousFlag = false;
		nextFlag = false;

		NEXT = START;
		PREVIOUS = START;
	}

	public void resetListing() {

		START_PARAM = START;

		previousFlag = false;
		nextFlag = false;

		NEXT = START;
		PREVIOUS = START;
	}

	public boolean showPrevious() {
		return previousFlag;
	}

	public boolean showNext() {
		return nextFlag;
	}

	public boolean showBoth() {
		return (previousFlag && nextFlag);
	}

	public String checkLanguage() {

		String lang;
		if (session.getLanguage() == null) {
			session.setLanguage("en");
		}
		lang = session.getLanguage();
		return lang;

	}
}
