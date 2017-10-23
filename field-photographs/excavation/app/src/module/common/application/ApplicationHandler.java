package module.common.application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import module.common.bean.ResponseData;
import module.common.constants.AppConstants.IMAGES;
import module.common.http.HttpProcessor;
import module.common.http.factory.BaseFactory;
import module.common.task.BaseTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ApplicationHandler {

	/*
	 * public static void moveToLoginScreen(Session session,Context
	 * context,Class screenName,String id,String name) { Intent myIntent = new
	 * Intent(context, ActivityWithOutLogin.class);
	 * myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * session.setScreen(screenName); session.setCatId(id);
	 * session.setCatName(name); context.startActivity(myIntent); ((Activity)
	 * context).finish();
	 * 
	 * }
	 * 
	 * public static void moveToLoginScreenSimple(Session session,Context
	 * context,Class screenName) { Intent myIntent = new Intent(context,
	 * ActivityWithOutLogin.class);
	 * myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 * session.setScreen(screenName); context.startActivity(myIntent);
	 * ((Activity) context).finish();
	 * 
	 * }
	 */
	// public void ImageLoaderInti(Context ctx) {
	//
	//
	// ImageLoaderConfiguration config = new
	// ImageLoaderConfiguration.Builder(ctx)
	// .threadPoolSize(3)
	// .threadPriority(Thread.NORM_PRIORITY - 2)
	// .memoryCacheSize(1500000)
	// // 1.5 Mb
	// .denyCacheImageMultipleSizesInMemory()
	// .discCacheFileNameGenerator(new Md5FileNameGenerator())
	// .enableLogging() // Not necessary in common
	// .build();
	// // Initialize ImageLoader with configuration.
	// ImageLoader.getInstance().init(config);
	// }
	public boolean hasData(String... datas) {

		boolean hasData = true;
		for (String data : datas) {

			if (!hasData(data)) {
				hasData = false;
				break;
			}
		}
		return hasData;
	}

	public boolean hasData(String text) {

		if (text == null || text.length() == 0)
			return false;

		return true;
	}

	public void releaseProcessor(HttpProcessor processor) {

		if (processor == null)
			return;

		processor = null;
		callGC();
	}

	public void releaseFactory(BaseFactory factory) {

		if (factory == null)
			return;

		factory = null;
		callGC();
	}

	public void releaseTask(BaseTask task) {

		if (task == null)
			return;

		task.cancel(true);
		task.release();
		task = null;

		callGC();
	}

	public Bitmap readImage(String imageUrl) {
		return readImage(imageUrl, 1);
	}

	public static String replaceEscap(String a) {
		a = a.replaceAll("\\r", "");
		a = a.replaceAll("\\t", "");
		a = a.replaceAll("\\n\\n", "%20");
		a = a.replaceAll("\\n", "%20");
		a = a.replaceAll(" ", "%20");
		return a;

	}
	// public class Utility {
		public void setListViewHeightBasedOnChildren(ListView listView) {
		    ListAdapter listAdapter = listView.getAdapter(); 
	        if (listAdapter == null) {
	            // pre-condition
	            return;
	        }

	        int totalHeight = 0;
	        for (int i = 0; i < listAdapter.getCount(); i++)
	        {
	            View listItem = listAdapter.getView(i, null, listView);
	            listItem.measure(0, 0);
	            totalHeight += listItem.getMeasuredHeight();
	        }

	        ViewGroup.LayoutParams params = listView.getLayoutParams();
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	        listView.setLayoutParams(params);
		}
	public static Bitmap readImage(String imageUrl, int scale) {

		InputStream is = null;
		BufferedInputStream bis = null;
		Bitmap bmp = null;

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;

		try {
			String newUrl = imageUrl.replaceAll(" ", "%20");

			URL url = new URL(newUrl);
			URLConnection conn = url.openConnection();
			// conn.connect();

			is = conn.getInputStream();
			bis = new BufferedInputStream(is);
			bmp = BitmapFactory.decodeStream(bis, null, o2);

		} catch (MalformedURLException e) {

		} catch (IOException e) {
			// e.printStackTrace();

		} finally {
			try {
				if (bis != null) {
					bis.close();
				}

			} catch (IOException e) {

			} finally {
				bis = null;
			}
			try {

				if (is != null) {
					is.close();
				}
			} catch (IOException e) {

			} finally {
				is = null;
			}

		}

		return bmp;
	}

	public File getOrCreateFolder(String folder, IMAGES imagePath) {

		String strImageFolder = folder + File.separator + imagePath.name();
		File imageFolder = new File(strImageFolder);
		if (imageFolder != null && !imageFolder.exists()) {
			imageFolder.mkdirs();
		}
		return imageFolder;
	}

	public File getOrCreateFolder(String folder, IMAGES imagePath, String id) {

		String strImageFolder = folder + File.separator + imagePath.name() + id;
		File imageFolder = new File(strImageFolder);
		if (imageFolder != null && !imageFolder.exists()) {
			imageFolder.mkdirs();
		}
		return imageFolder;
	}

	private static final String DOT_PNG = ".png";

	public boolean isExist(String folder, String fileName) {

		boolean isExist = false;
		String imageFilePath = folder + File.separator + fileName + DOT_PNG;

		File filePath = new File(imageFilePath);
		if (filePath != null && filePath.exists()) {
			isExist = true;
		}
		filePath = null;
		imageFilePath = null;

		return isExist;
	}

	public void removeFilesInFolder(String folder) {

		File file = new File(folder);
		if (file != null && file.exists() && file.listFiles().length > 0) {

			for (File fileImage : file.listFiles()) {
				fileImage.delete();
			}

		} else {
			file.mkdirs();
		}
		file = null;
	}

	public void createImageFile(String imageUrl, String folder, String file) {

		String fileName = file + DOT_PNG;

		URL url = null;
		InputStream input = null;
		OutputStream output = null;
		if (imageUrl != null && imageUrl.length() > 0) {
			String newUrl = imageUrl.replaceAll(" ", "%20");
			try {
				url = new URL(newUrl);
				input = url.openStream();

				output = new FileOutputStream(new File(folder, fileName));

				byte[] buffer = new byte[255];
				int bytesRead = 0;
				while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
					output.write(buffer, 0, bytesRead);
				}

				output.close();
				input.close();

			} catch (IOException iex) {
				iex.printStackTrace();

			} catch (Exception ex) {
				ex.printStackTrace();

			} finally {

				try {
					if (output != null) {
						output.close();
					}
				} catch (Exception e) {
				} finally {
					output = null;
				}

				try {
					if (input != null) {
						input.close();
					}
				} catch (Exception e) {
				} finally {
					input = null;
				}

				url = null;
			}
		}
	}

	public Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public Drawable getDrawable(String imagePath, String fileName) {
		try {
			String path = imagePath + File.separator + fileName + DOT_PNG;
			return Drawable.createFromPath(path);
		} catch (Exception e) {

		}
		return null;
	}

	public <T extends ResponseData> void releaseList(List<T> list) {

		if (list == null)
			return;

		for (T t : list) {
			t.release();
			t = null;
		}

		list.clear();
		list = null;
	}

	public void callGC() {
		// System.gc();
		// Runtime.getRuntime().gc();
	}

	private static ApplicationHandler handler;

	private ApplicationHandler() {
	}

	public static ApplicationHandler getInstance() {

		// mContext=context;
		if (handler == null)
			handler = new ApplicationHandler();

		return handler;
	}

}
