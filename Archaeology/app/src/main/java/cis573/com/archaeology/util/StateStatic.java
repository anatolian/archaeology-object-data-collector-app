// Static stuff
// @author: msenol
package cis573.com.archaeology.util;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
public class StateStatic
{
    // This class holds global state variables. This class should be only used in static way
    public static final String LOG_TAG = "Ceramic App";
    public static final String LOG_TAG_WIFI_DIRECT = "WIFIDIRECT";
    public static final String LOG_TAG_BLUETOOTH = "BLUETOOTH";
    public static final int REQUEST_IMAGE_CAPTURE = 301;
    public static final int MESSAGE_WEIGHT = 501;
    public static final int MESSAGE_STATUS_CHANGE = 502;
    public static final int REQUEST_ENABLE_BT = 301;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String DEFAULT_WEB_SERVER_URL
            = "https://thawing-plains-16187.herokuapp.com/web/";
    public static final String DEFAULT_CAMERA_MAC = "bc:f5:ac:dc:f3:7e";
    // 30 minutes
    public static final long DEFAULT_CALIBRATION_INTERVAL = 1800000;
    // default url to connect to database to send photos back and forth
    private static final String DEFAULT_PHOTO_PATH = "ARCPHOTOS";
    public static final String THUMBNAIL_EXTENSION_STRING = "thumb.jpg";
    public static final int DEFAULT_VOLLEY_TIMEOUT = 7000;
    public static final String SYNCED = "S";
    public static final String MARKED_AS_ADDED = "A";
    public static final String MARKED_AS_TO_DOWNLOAD = "D";
    // fields in the database
    public static final String AREA_EASTING = "area_easting";
    public static final String AREA_NORTHING = "area_northing";
    public static final String CONTEXT_NUMBER = "context_number";
    public static final String SAMPLE_NUMBER = "sample_number";
    public static final String ALL_SAMPLE_NUMBER = "all_avaiable_sample_number";
    // offset values that help you to locate the correct fields in the data tables to store
    // information. the global webserver is being set to a default value
    // need to make sure that app is able to find ip address on its own
    private static String globalWebServerURL = DEFAULT_WEB_SERVER_URL;
    // DEFAULTWEBSERVERURL; connection to current mac camera address
    private static String globalCameraMAC = DEFAULT_CAMERA_MAC;
    // global current object most likely is used to track the current object from the database that
    // you are trying to view.
    private static long remoteCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
    private static long tabletCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
    private static int scaleTare = 0;
    // variable to track connections
    private static boolean isRemoteCameraSelect = true;
    public static boolean connectedToRemoteCamera = false;
    public static String connectedMacAddress = "";
    public static boolean isTakePhotoButtonClicked = false;
    /**
     * return webserver url that is used to connect to the main database
     * @return Returns the web server URL
     */
    public static String getGlobalWebServerURL()
    {
        return globalWebServerURL;
    }

    /**
     * Change server URL
     * @param globalWebServerURL - new server URL
     */
    public static void setGlobalWebServerURL(String globalWebServerURL)
    {
        Log.v(LOG_TAG, "globalWebServerUrl changed into " + globalWebServerURL);
        StateStatic.globalWebServerURL = globalWebServerURL;
    }

    /**
     * Get Camera MAC address
     * @return Returns camera MAC address
     */
    public static String getGlobalCameraMAC()
    {
        return globalCameraMAC;
    }

    /**
     * Set MAC address
     * @param globalCameraMAC - new MAC address
     */
    public static void setGlobalCameraMAC(String globalCameraMAC)
    {
        Log.v(LOG_TAG, "globalCameraMAC changed into " + globalCameraMAC);
        StateStatic.globalCameraMAC = globalCameraMAC;
    }

    /**
     * Get photo save location
     * @return Returns photo save location
     */
    public static String getGlobalPhotoSavePath()
    {
        return DEFAULT_PHOTO_PATH;
    }

    /**
     * Get tare
     * @return Returns tare
     */
    public static int getScaleTare()
    {
        return scaleTare;
    }

    /**
     * Set tare
     * @param scaleTare - new tare
     */
    public static void setScaleTare(int scaleTare)
    {
        StateStatic.scaleTare = scaleTare;
    }

    /**
     * Camera Methods
     * @return Returns camera calibration interval
     */
    public static long getRemoteCameraCalibrationInterval()
    {
        return remoteCameraCalibrationInterval;
    }

    /**
     * Change calibration frequency
     * @param remoteCameraCalibrationInterval - new frequency
     */
    public static void setRemoteCameraCalibrationInterval(long remoteCameraCalibrationInterval)
    {
        StateStatic.remoteCameraCalibrationInterval = remoteCameraCalibrationInterval;
    }

    /**
     * Get phone camera calibration frequency
     * @return - Returns phone camera calibration frequency
     */
    public static long getTabletCameraCalibrationInterval()
    {
        return tabletCameraCalibrationInterval;
    }

    /**
     * Change phone camera calibration frequency
     * @param tabletCameraCalibrationInterval - new phone camera calibration frequency
     */
    public static void setTabletCameraCalibrationInterval(long tabletCameraCalibrationInterval)
    {
        StateStatic.tabletCameraCalibrationInterval = tabletCameraCalibrationInterval;
    }

    /**
     * Are we using Sony camera?
     * @return Returns whether Sony camera is used
     */
    public static boolean isIsRemoteCameraSelect()
    {
        return isRemoteCameraSelect;
    }

    /**
     * Change active camera
     * @param isRemoteCameraSelect - true if Sony is used
     */
    public static void setIsRemoteCameraSelect(boolean isRemoteCameraSelect)
    {
        StateStatic.isRemoteCameraSelect = isRemoteCameraSelect;
    }

    /**
     * Is Bluetooth on?
     * @return Returns if Bluetooth is on
     */
    public static boolean isBluetoothEnabled()
    {
        return false;
    }

    /**
     * Convert dp to px
     * @param dp - pixel
     * @return - return px of dp
     */
    public static float convertDpToPixel(float dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * Convert dp to px
     * @param dp - pixel
     * @return Returns px of dp
     */
    public static int convertDpToPixel(int dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160);
    }

    /**
     * Get current date
     * @return Returns timestamp
     */
    public static String getTimeStamp()
    {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    }

    /**
     * Display error
     * @param error - error to display
     * @param cont - calling context
     */
    public static void showToastError(Exception error, Context cont)
    {
        Toast.makeText(cont, error.toString(), Toast.LENGTH_SHORT).show();
    }
}