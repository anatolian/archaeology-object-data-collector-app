package module.common.task;

import java.io.File;

import module.common.application.ApplicationHandler;
import module.common.bean.ResponseData;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DownloadImageTask extends BaseTask {

	private ImageView imageView;
	private ProgressBar progressBar;
	private String imageUrl;
	private ApplicationHandler handler;
	private String imageFolder;
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public DownloadImageTask() {		
		handler = ApplicationHandler.getInstance();
	}
	
	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}

	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
		//imageView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected Void doInBackground(String... params) {
		
		handler.createImageFile(imageUrl, imageFolder, fileName);
		progressBar.setVisibility(View.VISIBLE);
					
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		
		//imageView.setImageDrawable(handler.getDrawable(imageFolder, fileName));
		imageView.setImageBitmap(appHandler.decodeFile(new File(imageFolder + File.separator + fileName + ".png")));
		progressBar.setVisibility(View.INVISIBLE);
		imageView.setVisibility(View.VISIBLE);
		
		System.gc();
	}

	@Override
	public <T extends ResponseData> T getData(int pos) {
		return null;
	}

	@Override
	public void release() {				
	}
}
