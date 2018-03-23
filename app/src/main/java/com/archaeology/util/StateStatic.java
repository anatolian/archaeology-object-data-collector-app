// Static stuff
// @author: msenol
package com.archaeology.util;
import android.bluetooth.BluetoothAdapter;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class StateStatic
{
    // This class holds global state variables. This class should be only used in a static way
    public static final String LOG_TAG = "Ceramic App";
    public static final String LOG_TAG_WIFI_DIRECT = "WIFIDIRECT";
    public static final String LOG_TAG_BLUETOOTH = "BLUETOOTH";
    public static final int REQUEST_IMAGE_CAPTURE = 301;
    public static final int REQUEST_REMOTE_IMAGE = 302;
    public static final int MESSAGE_WEIGHT = 501;
    public static final int MESSAGE_STATUS_CHANGE = 502;
    public static final int REQUEST_ENABLE_BT = 301;
    public static final String DEFAULT_WEB_SERVER_URL = "https://pacific-brook-36642.herokuapp.com";
    public static final String DEFAULT_BUCKET_URL = "s3.console.aws.amazon.com/s3/buckets/pennmuseum/";
    public static final String DEFAULT_CAMERA_MAC = "fe:c2:de:31:0a:e1";
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
    public static final String ALL_SAMPLE_NUMBER = "all_available_sample_number";
    public static String deviceName = "nutriscale_1910";
    // offset values that help you to locate the correct fields in the data tables to store
    // information. the global web server is being set to a default value. need to make sure that
    // app is able to find IP address on its own
    // DEFAULT_WEB_SERVER_URL; connection to current IP camera address
    private static String globalWebServerURL = DEFAULT_WEB_SERVER_URL;
    private static String globalBucketURL = DEFAULT_BUCKET_URL;
    private static String globalCameraMAC = DEFAULT_CAMERA_MAC;
    // global current object most likely is used to track the current object from the database that
    // you are trying to view.
    private static long remoteCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
    private static long tabletCameraCalibrationInterval = DEFAULT_CALIBRATION_INTERVAL;
    // variable to track connections
    private static boolean isRemoteCameraSelected = true;
    public static boolean connectedToRemoteCamera = false;
    public static String cameraMACAddress = DEFAULT_CAMERA_MAC;
    public static String cameraIPAddress = null;
    public static boolean isTakePhotoButtonClicked = false;
    /**
     * Return web server URL that is used to connect to the main database
     * @return Returns the web server URL
     */
    public static String getGlobalWebServerURL()
    {
        return globalWebServerURL;
    }

    /**
     * Return Bucket URL for storing images
     * @return Returns the bucket URL
     */
    public static String getGlobalBucketURL()
    {
        return globalBucketURL;
    }

    /**
     * Change server URL
     * @param globalWebServerURL - new server URL
     */
    public static void setGlobalWebServerURL(String globalWebServerURL)
    {
        StateStatic.globalWebServerURL = globalWebServerURL;
    }

    /**
     * Change bucket URL
     * @param globalBucketURL - new bucket URL
     */
    public static void setGlobalBucketURL(String globalBucketURL)
    {
        StateStatic.globalBucketURL = globalBucketURL;
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
        StateStatic.globalCameraMAC = globalCameraMAC;
        cameraMACAddress = globalCameraMAC;
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
    public static boolean isRemoteCameraSelected()
    {
        return isRemoteCameraSelected;
    }

    /**
     * Change active camera
     * @param isRemoteCameraSelected - true if Sony is used
     */
    public static void setIsRemoteCameraSelected(boolean isRemoteCameraSelected)
    {
        StateStatic.isRemoteCameraSelected = isRemoteCameraSelected;
    }

    /**
     * Is Bluetooth on?
     * @return Returns if Bluetooth is on
     */
    public static boolean isBluetoothEnabled()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * Convert dp to px
     * @param dp - pixel
     * @return - return px of dp
     */
    public static float convertDPToPixel(float dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * Convert dp to px
     * @param dp - pixel
     * @return Returns px of dp
     */
    public static int convertDPToPixel(int dp)
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
}