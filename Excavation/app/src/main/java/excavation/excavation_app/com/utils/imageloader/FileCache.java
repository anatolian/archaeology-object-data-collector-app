// Faile cache
// @author: anatolian
package excavation.excavation_app.com.utils.imageloader;
import java.io.File;
import android.content.Context;
public class FileCache
{
    private File cacheDir;
    /**
     * Constructor
     * @param context - calling context
     */
    public FileCache(Context context)
    {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
        }
        else
        {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists())
        {
            cacheDir.mkdirs();
        }
    }

    /**
     * Get a file
     * @param url - file location
     * @return Returns the file
     */
    public File getFile(String url)
    {
        // I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        // Another possible solution
        File f = new File(cacheDir, filename);
        return f;
    }

    /**
     * Clear cache
     */
    public void clear()
    {
        File[] files = cacheDir.listFiles();
        if (files == null)
        {
            return;
        }
        for (File f:files)
        {
            f.delete();
        }
    }
}