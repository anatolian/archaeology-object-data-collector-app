// Communicate with the scale over bluetooth
// @author: J. Patrick Taggart
package widac.cis350.upenn.edu.widac;
import static android.R.attr.x;
public class BluetoothHelper
{
    /**
     * Parse the data received from scale
     * @param bytes - the bytes received from the scale
     * @param size - the size of the object
     * @return Returns the weight of the object
     */
    public static int parseBytesNutriscale(byte[] bytes, int size)
    {
        int sign = 1;
        int value;
        if (bytes[size - 2] > 0)
        {
            sign = -1;
        }
        // bits 12-0 gives value of the scale
        value = ((bytes[size - 2] & 0xf) * 256) + (((int) bytes[size - 1]) & 0xff);
        return sign * value;
    }
}