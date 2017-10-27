// Memory cache
// @author: anatolian
package excavation.excavation_app.com.utils.imageloader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import android.graphics.Bitmap;
public class MemoryCache
{
    private static final String TAG = "MemoryCache";
    // Last
    private Map<String, Bitmap> cache = Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
    // argument true for LRU ordering. current allocated size
    private long size = 0;
    // max memory in bytes
    private long limit = 1000000;
    /**
     * Constructor
     */
    public MemoryCache()
    {
        // use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    /**
     * Set limit
     * @param new_limit - new limit
     */
    public void setLimit(long new_limit)
    {
        limit = new_limit;
    }

    /**
     * Get image
     * @param id - image id
     * @return Returns image
     */
    public Bitmap get(String id)
    {
        try
        {
            if (!cache.containsKey(id))
            {
                return null;
            }
            return cache.get(id);
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Add element to map
     * @param id - key
     * @param bitmap - image value
     */
    public void put(String id, Bitmap bitmap)
    {
        try
        {
            if (cache.containsKey(id))
            {
                size -= getSizeInBytes(cache.get(id));
            }
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }

    /**
     * Check cache size
     */
    private void checkSize()
    {
        if (size > limit)
        {
            // least
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
            // recently accessed item will be the first one iterated
            while (iter.hasNext())
            {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit)
                {
                    break;
                }
            }
        }
    }

    /**
     * Clear cache
     */
    public void clear()
    {
        try
        {
            cache.clear();
            size = 0;
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Get cache size
     * @param bitmap - image
     * @return Returns size
     */
    long getSizeInBytes(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}