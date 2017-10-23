// CheatSheet contains commons methods that are used throughout the other classes
// @author: msenol
package objectphotography2.com.object.photography.objectphotography_app;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.LOGTAG;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.THUMBNAIL_EXTENSION_STRING;
import static objectphotography2.com.object.photography.objectphotography_app.StateStatic.getGlobalPhotoSavePath;
public class CheatSheet
{
    /**
     * returns all the items in the spinner
     * @param aContext - calling context
     * @param aSpinner - spinner to read
     */
    public static List<String> getSpinnerItems(Context aContext, Spinner aSpinner)
    {
        int itemCount = aSpinner.getCount();
        List<String> items = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++)
        {
            items.add(aSpinner.getItemAtPosition(i).toString());
        }
        return items;
    }

    /**
     * set items to spinner
     * @param aContext - calling context
     * @param aSpinner - spinner to set
     * @param items - items for spinner
     */
    public static void setSpinnerItems(Context aContext, Spinner aSpinner, List<String> items)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(aContext, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aSpinner.setAdapter(adapter);
    }

    /**
     * set items to spinner
     * @param aContext - calling context
     * @param aSpinner - spinner to fill
     * @param items - spinner items
     * @param selectedItem - default item
     */
    public static void setSpinnerItems(Context aContext, Spinner aSpinner, List<String> items, String selectedItem)
    {
        int selectedItemIndex = 0;
        for (int i = 0; i < items.size(); i++)
        {
            if (items.get(i).equals(selectedItem))
            {
                selectedItemIndex = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(aContext, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aSpinner.setAdapter(adapter);
        aSpinner.setSelection(selectedItemIndex);
    }

    /**
     * set items to spinner
     * @param aContext - calling context
     * @param aSpinner - spinner to fill
     * @param items - spinner items
     * @param selectedItem - default item
     * @param spinnerItemLayout - ordering to items
     */
    public static void setSpinnerItems(Context aContext, Spinner aSpinner, List<String> items,
                                       String selectedItem, int spinnerItemLayout)
    {
        int selectedItemIndex = 0;
        for (int i = 0; i < items.size(); i++)
        {
            if (items.get(i).equals(selectedItem))
            {
                selectedItemIndex = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(aContext, spinnerItemLayout, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aSpinner.setAdapter(adapter);
        aSpinner.setSelection(selectedItemIndex);
    }

    /**
     * creating a thumbnail for requested image
     * @param inputFilename - image file
     * @return Returns image URI
     */
    public static Uri getThumbnail(String inputFilename)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getGlobalPhotoSavePath());
        String originalFilePath = mediaStorageDir.getPath() + File.separator + inputFilename;
        String thumbPath = mediaStorageDir.getPath() + File.separator + inputFilename + THUMBNAIL_EXTENSION_STRING;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(originalFilePath, options);
        int width = options.outWidth;
        int height = options.outHeight;
        Log.v(LOGTAG, "originalFilePath: " + originalFilePath + " thumbPath: " + thumbPath);
        File thumbFile = new File(thumbPath);
        // creating a thumbnail image and setting the bounds of the thumbnail
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(originalFilePath),
                Math.round(width / 4.1f), Math.round(height / 4.1f));
        try
        {
            FileOutputStream fos = new FileOutputStream(thumbFile);
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        }
        catch (Exception ex)
        {
            // e.getMessage is returning a null value
            Log.d("Error", "Error Message: " + ex.getMessage());
            ex.printStackTrace();
        }
        return Uri.fromFile(thumbFile);
    }

    /**
     * Compress the image
     * @param inputImage - image to compress
     * @return Returns the compressed image
     */
    public static Bitmap compressImage(Bitmap inputImage)
    {
        Bitmap outputImage = ThumbnailUtils.extractThumbnail(inputImage, Math.round(inputImage.getWidth() / 2.0f),
                Math.round(inputImage.getHeight() / 2.0f));
        inputImage.recycle();
        return outputImage;
    }

    /**
     * returns the URI of the original image
     * @param thumbnailUri - thumbnail location
     * @return Returns the original's location
     */
    public static Uri getOriginalImageUri(Uri thumbnailUri)
    {
        String thumnailUriString = thumbnailUri.toString();
        String x = thumnailUriString.substring(0, thumnailUriString.length() - THUMBNAIL_EXTENSION_STRING.length());
        Log.v(LOGTAG, "Original file uri: " + x);
        return Uri.parse(x);
    }

    /**
     * Get the thumbnail location
     * @param originalImageUri - original location
     * @return Returns the thumbnail location
     */
    public static Uri getThumbnailImageUri(Uri originalImageUri)
    {
        return Uri.parse(originalImageUri.toString() + THUMBNAIL_EXTENSION_STRING);
    }

    /**
     * takes incoming json array and converts to a regular array
     * @param aJsonArray - JSON array to convert
     * @return Returns an array of the JSON array
     * @throws JSONException if the JSON is malformed
     */
    public static ArrayList<String> convertJSONArrayToList(JSONArray aJsonArray) throws JSONException
    {
        ArrayList<String> tmpArray = new ArrayList<>(aJsonArray.length());
        for (int i = 0; i < aJsonArray.length(); i++)
        {
            tmpArray.add(aJsonArray.getString(i));
        }
        Log.v(LOGTAG, "the array contains " + tmpArray.toString());
        return tmpArray;
    }

    /**
     * Scale and combine bytes
     * @param byte1 - first
     * @param byte2 - second
     * @return Returns the combination
     */
    public static int combineScaleBytes(String byte1, String byte2)
    {
        Log.v(LOGTAG, "Incoming byte1: " + byte1);
        Log.v(LOGTAG, "Incoming byte2: " + byte2);
        Log.v(LOGTAG, "Byte1 stripped: " + byte1.substring(3));
        return Integer.parseInt(byte1.substring(3) + byte2, 2);
    }

    /**
     * Create a file Uri for saving an image
     * @param filename - image location
     * @return Returns the image URI
     */
    public static Uri getOutputMediaFileUri(String filename)
    {
        return Uri.fromFile(getOutputMediaFile(filename));
    }

    /**
     * Create a File for saving an image
     * @param filename - name of file
     * @return Returns the file
     */
    public static File getOutputMediaFile(String filename)
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getGlobalPhotoSavePath());
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        Log.v(LOGTAG, "Photo Directory exists?");
        if (! mediaStorageDir.isDirectory())
        {
            Log.v(LOGTAG, "isDirectory returns false");
            if (! mediaStorageDir.mkdirs())
            {
                Log.v(LOGTAG, "failed to create directory" + getGlobalPhotoSavePath());
                return null;
            }
        }
        // Create a media file name
        String path = mediaStorageDir.getPath() + File.separator + filename +".jpg";
        return new File(path);
    }

    /**
     * deletes photos from external storage public directory
     */
    public static void clearThePhotosDirectory()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getGlobalPhotoSavePath());
        if (mediaStorageDir.isDirectory())
        {
            for (File file: mediaStorageDir.listFiles())
            {
                file.delete();
            }
            Log.v(LOGTAG, "Clearing Photos Dir");
        }
    }

    /**
     * uses thumbnail to get original file and deletes both
     * @param thumbnailUri - thumbnail location
     */
    public static void deleteOriginalAndThumnailPhoto(Uri thumbnailUri)
    {
        Uri originalImageUri = getOriginalImageUri(thumbnailUri);
        File thumbnailFile = new File(thumbnailUri.getPath());
        File originalFile = new File(originalImageUri.getPath());
        Log.v(LOGTAG, "Deleting original and thumbnail photo: "+ thumbnailUri.toString() + ", "
                + originalImageUri.toString());
        thumbnailFile.delete();
        originalFile.delete();
    }

    /**
     * Open SettingsActivity
     * @param view - current view
     * @param anActivity - anActivity - calling activity
     */
    public static void goToSettings(View view, Activity anActivity)
    {
        Log.v(LOGTAG, "Settings button clicked");
        Intent myIntent = new Intent(anActivity, SettingsActivity.class);
        anActivity.startActivity(myIntent);
    }
}