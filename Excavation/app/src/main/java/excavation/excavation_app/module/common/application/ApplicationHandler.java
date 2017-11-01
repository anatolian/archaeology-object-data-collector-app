// Handle application
// @author: anatolian
package excavation.excavation_app.module.common.application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public class ApplicationHandler
{
    private static ApplicationHandler handler;
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