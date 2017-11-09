// Utilities
// @author: msenol86, ygowda, and anatolian
package objectphotography2.com.object.photography.objectphotography_app;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOG_TAG_WIFI_DIRECT;
public class Utils
{
    private final static String p2pInt = "p2p";
    /**
     * MAC address equality
     * @param mac1 - MAC address 1
     * @param mac2 - MAC address 2
     * @return Returns whether mac1 = mac2
     */
    public static boolean macEquals(String mac1, String mac2)
    {
        int numberOfCorrectColumns = 0;
        String[] mac1Splitted = mac1.trim().split(":");
        String[] mac2Splitted = mac2.trim().split(":");
        if (mac1Splitted.length == mac2Splitted.length)
        {
            for (int i = 0 ; i < mac1Splitted.length; i++)
            {
                if (mac1Splitted[i].equals(mac2Splitted[i]))
                {
                    numberOfCorrectColumns++;
                }
            }
            return numberOfCorrectColumns >= 5;
        }
        else
        {
            return false;
        }
    }

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
     * @param MAC - MAC address
     * @return Returns IP address
     */
    public static String getIPFromMac(String MAC)
    {
        // method modified from:
        // http://www.flattermann.net/2011/02/android-howto-find-the-hardware-mac-address-of-a-remote-host/
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
                    assertTrue(macEquals("c2:bd:d1:69:81:ee", "c2:bd:d1:69:01:ee"));
                    assertFalse(macEquals("c2:bd:d1:63:81:ee", "c2:bd:d1:62:01:ee"));
                    assertTrue(macEquals("bc:f5:ac:dc:f3:7e", "be:f5:ac:dc:f3:7e"));
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