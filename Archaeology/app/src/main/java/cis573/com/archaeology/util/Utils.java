// Utilities
// @author: msenol86, ygowda
package cis573.com.archaeology.util;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import static cis573.com.archaeology.util.StateStatic.LOG_TAG_WIFI_DIRECT;
public class Utils
{
    private final static String p2pInt = "p2p";
    /**
     * Get last modified date
     * @return - return date last modified
     */
    public static String getLastModifiedDateOfArpFile()
    {
        File arpLog = new File("/proc/net/arp");
        return new Date(arpLog.lastModified()).toString();
    }

    /**
     * TODO: very buggy code should be fixed
     * @return Returns IP address
     */
    public static String getIPFromMac()
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null)
            {
                Log.v(LOG_TAG_WIFI_DIRECT, "/proc/net/arp line: " + line);
                String[] splitted = line.split(" +");
                if (line.matches(".*" + p2pInt + ".*"))
                {
                    return splitted[0];
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}