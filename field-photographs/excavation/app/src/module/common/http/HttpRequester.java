package module.common.http;

public enum HttpRequester {

	
	
	GET_AREA("get_areas.php"),
	GET_LISTING("get_listing.php"),
	MULTIPLE("multiple_photo_upload.php"),
	ADD_MULTIPLE_PHOTO("add_multiple_photo.php"),
	ADD_SAMPLE_PHOTO("add_single_photo_workflow3.php"),
	ADD_SINGLE_PHOTO("add_single_photo.php"),
	ADD_CONTEXT_NUM("add_context.php"),
	GET_IMAGE("get_photo.php"),
	REPLACE_PHOTO("replace_photo.php"),
	DELETE_CTX("delete_context.php"),
	GET_PROPERTY("get_property.php")
	
	;
	
	private String fileName;

	HttpRequester(String file) {
		fileName = file;
	}

	public String getFileName() {
		return fileName;
	}
}
