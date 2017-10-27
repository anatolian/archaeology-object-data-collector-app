// Image loader
// @author: anatolian
package excavation.excavation_app.com.utils.imageloader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import com.appenginedemo.R;
public class ImageLoader
{
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    // handler to display images in UI thread
    Handler handler = new Handler();
    final int stub_id = R.drawable.ic_launcher;
    final int loading_image = R.drawable.no_img_prv;
    /**
     * Constructor
     * @param context - calling context
     */
    public ImageLoader(Context context)
    {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    /**
     * Draw the image
     * @param url - image URL
     * @param imageView - image window
     */
    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        queuePhoto(url, imageView);
        imageView.setImageResource(loading_image);
    }

    /**
     * Queue photo
     * @param url - image URL
     * @param imageView - image view
     */
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    /**
     * Get the image
     * @param url - image location
     * @return Return the image
     */
    private Bitmap getBitmap(String url)
    {
        File f = fileCache.getFile(url);
        // from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
        {
            return b;
        }
        // from web
        try
        {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        }
        catch (Throwable ex)
        {
            if (ex instanceof OutOfMemoryError)
            {
                memoryCache.clear();
            }
            return null;
        }
    }

    /**
     * decodes image and scales it to reduce memory consumption
     * @param f - the file
     * @return Return the image
     */
    private Bitmap decodeFile(File f)
    {
        try
        {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 100;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true)
            {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        /**
         * Load a photo
         * @param u - url
         * @param i - image view
         */
        public PhotoToLoad(String u, ImageView i)
        {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable
    {
        PhotoToLoad photoToLoad;
        /**
         * Constructor
         * @param photoToLoad - image to load
         */
        PhotosLoader(PhotoToLoad photoToLoad)
        {
            this.photoToLoad = photoToLoad;
        }

        /**
         * Run thread
         */
        @Override
        public void run()
        {
            try
            {
                if (imageViewReused(photoToLoad))
                {
                    return;
                }
                Bitmap bmp = getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if (imageViewReused(photoToLoad))
                {
                    return;
                }
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }
            catch (Throwable th)
            {
                th.printStackTrace();
            }
        }
    }

    /**
     * Was the image view reused?
     * @param photoToLoad - image to load
     * @return Returns whether the imageview was reused
     */
    boolean imageViewReused(PhotoToLoad photoToLoad)
    {
        String tag = imageViews.get(photoToLoad.imageView);
        return (tag == null || !tag.equals(photoToLoad.url));
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        /**
         * Constructor
         * @param b - image
         * @param p - image to load
         */
        public BitmapDisplayer(Bitmap b, PhotoToLoad p)
        {
            bitmap = b;
            photoToLoad = p;
        }

        /**
         * Run thread
         */
        public void run()
        {
            if (imageViewReused(photoToLoad))
            {
                return;
            }
            if (bitmap != null)
            {
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
            else
            {
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

    /**
     * Clear cache
     */
    public void clearCache()
    {
        memoryCache.clear();
        fileCache.clear();
    }
}