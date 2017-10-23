package module.gallery;

import java.util.ArrayList;

import module.common.bean.ResponseData;

public class GalleryBean extends ResponseData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String gallery_name,image_path;
	public ArrayList<AlbumImagesBean> album_detail = new ArrayList<AlbumImagesBean>();
}
