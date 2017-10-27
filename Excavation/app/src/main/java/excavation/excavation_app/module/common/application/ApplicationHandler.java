// Handle application
// @author: anatolian
package excavation.excavation_app.module.common.application;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.constants.AppConstants.IMAGES;
import excavation.excavation_app.module.common.http.HttpProcessor;
import excavation.excavation_app.module.common.http.factory.BaseFactory;
import excavation.excavation_app.module.common.task.BaseTask;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
public class ApplicationHandler
{
    private static ApplicationHandler handler;
    private static final String DOT_PNG = ".png";
    /**
     * Does the handler have data?
     * @param datas - data to process
     * @return Returns whether there is data
     */
    public boolean hasData(String... datas)
    {
        boolean hasData = true;
        for (String data: datas)
        {
            if (!hasData(data))
            {
                hasData = false;
                break;
            }
        }
        return hasData;
    }

    /**
     * Does the handler have data?
     * @param text - data
     * @return Returns whether there is data
     */
    public boolean hasData(String text)
    {
        return !(text == null || text.length() == 0);
    }

    /**
     * Release processor
     * @param processor - processor to release
     */
    public void releaseProcessor(HttpProcessor processor)
    {
        if (processor == null)
        {
            return;
        }
        processor = null;
        callGC();
    }

    /**
     * Release factory
     * @param factory - factory to release
     */
    public void releaseFactory(BaseFactory factory)
    {
        if (factory == null)
        {
            return;
        }
        factory = null;
        callGC();
    }

    /**
     * Release thread
     * @param task - thread
     */
    public void releaseTask(BaseTask task)
    {
        if (task == null)
        {
            return;
        }
        task.cancel(true);
        task.release();
        task = null;
        callGC();
    }

    /**
     * Read image
     * @param imageUrl - image location
     * @return Returns the image
     */
    public Bitmap readImage(String imageUrl)
    {
        return readImage(imageUrl, 1);
    }

    /**
     * Replace escape characters
     * @param a
     * @return
     */
    public static String replaceEscap(String a)
    {
        a = a.replaceAll("\\r", "");
        a = a.replaceAll("\\t", "");
        a = a.replaceAll("\\n\\n", "%20");
        a = a.replaceAll("\\n", "%20");
        a = a.replaceAll(" ", "%20");
        return a;
    }

    /**
     * Set list view height
     * @param listView - list view to change
     */
    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
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

    /**
     * Read an image
     * @param imageUrl - image location
     * @param scale - scaled image
     * @return Returns image
     */
    public static Bitmap readImage(String imageUrl, int scale)
    {
        InputStream is = null;
        BufferedInputStream bis = null;
        Bitmap bmp = null;
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try
        {
            String newUrl = imageUrl.replaceAll(" ", "%20");
            URL url = new URL(newUrl);
            URLConnection conn = url.openConnection();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is);
            bmp = BitmapFactory.decodeStream(bis, null, o2);
        }
        catch (IOException e)
        {
        }
        finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                bis = null;
            }
            try
            {
                if (is != null)
                {
                    is.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                is = null;
            }
        }
        return bmp;
    }

    /**
     * Get the folder
     * @param folder - folder to get
     * @param imagePath - image location
     * @return Returns the folder
     */
    public File getOrCreateFolder(String folder, IMAGES imagePath)
    {
        String strImageFolder = folder + File.separator + imagePath.name();
        File imageFolder = new File(strImageFolder);
        if (!imageFolder.exists())
        {
            imageFolder.mkdirs();
        }
        return imageFolder;
    }

    /**
     * Get folder
     * @param folder - folder to get
     * @param imagePath - image location
     * @param id - folder id
     * @return Returns the folder
     */
    public File getOrCreateFolder(String folder, IMAGES imagePath, String id)
    {
        String strImageFolder = folder + File.separator + imagePath.name() + id;
        File imageFolder = new File(strImageFolder);
        if (!imageFolder.exists())
        {
            imageFolder.mkdirs();
        }
        return imageFolder;
    }

    /**
     * Does the folder exist?
     * @param folder - folder in question
     * @param fileName - name of file
     * @return Returns whether the file exists
     */
    public boolean isExist(String folder, String fileName)
    {
        boolean isExist = false;
        String imageFilePath = folder + File.separator + fileName + DOT_PNG;
        File filePath = new File(imageFilePath);
        if (filePath.exists())
        {
            isExist = true;
        }
        filePath = null;
        imageFilePath = null;
        return isExist;
    }

    /**
     * Empty folder
     * @param folder - folder to empty
     */
    public void removeFilesInFolder(String folder)
    {
        File file = new File(folder);
        if (file.exists() && file.listFiles().length > 0) {
            for (File fileImage: file.listFiles())
            {
                fileImage.delete();
            }
        }
        else
        {
            file.mkdirs();
        }
        file = null;
    }

    /**
     * Save image
     * @param imageUrl - image location
     * @param folder - containing folder
     * @param file - file name
     */
    public void createImageFile(String imageUrl, String folder, String file)
    {
        String fileName = file + DOT_PNG;
        URL url = null;
        InputStream input = null;
        OutputStream output = null;
        if (imageUrl != null && imageUrl.length() > 0)
        {
            String newUrl = imageUrl.replaceAll(" ", "%20");
            try
            {
                url = new URL(newUrl);
                input = url.openStream();
                output = new FileOutputStream(new File(folder, fileName));
                byte[] buffer = new byte[255];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
                {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
                input.close();
            }
            catch (Exception iex)
            {
                iex.printStackTrace();

            }
            finally
            {
                try
                {
                    if (output != null)
                    {
                        output.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    output = null;
                }
                try
                {
                    if (input != null)
                    {
                        input.close();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    input = null;
                }
                url = null;
            }
        }
    }

    /**
     * Read file
     * @param f - file to read
     * @return Returns image
     */
    public Bitmap decodeFile(File f)
    {
        try
        {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
            {
                scale *= 2;
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a drawable
     * @param imagePath - location of image
     * @param fileName - file name
     * @return Returns the drawable
     */
    public Drawable getDrawable(String imagePath, String fileName)
    {
        try
        {
            String path = imagePath + File.separator + fileName + DOT_PNG;
            return Drawable.createFromPath(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Release a list
     * @param list - list to release
     */
    public <T extends ResponseData> void releaseList(List<T> list)
    {
        if (list == null)
        {
            return;
        }
        for (T t: list)
        {
            t.release();
            t = null;
        }
        list.clear();
        list = null;
    }

    /**
     * Call GC
     */
    public void callGC()
    {
    }

    /**
     * Constructor
     */
    private ApplicationHandler()
    {
    }

    /**
     * Get singleton
     * @return Returns singleton
     */
    public static ApplicationHandler getInstance()
    {
        if (handler == null)
        {
            handler = new ApplicationHandler();
        }
        return handler;
    }
}