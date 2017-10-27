// Utilities
// @author: anatolian
package excavation.excavation_app.com.utils.imageloader;
import java.io.InputStream;
import java.io.OutputStream;
public class Utils
{
    /**
     * Copy input stream
     * @param is - input stream
     * @param os - output stream
     */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes = new byte[buffer_size];
            while (true)
            {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                {
                    break;
                }
                os.write(bytes, 0, count);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}