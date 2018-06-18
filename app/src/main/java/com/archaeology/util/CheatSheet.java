// CheatSheet contains commons methods that are used throughout the other classes
// @author: Christopher Besser, msenol
package com.archaeology.util;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.archaeology.R;
import com.archaeology.ui.SettingsActivity;
import static com.archaeology.util.StateStatic.THUMBNAIL_EXTENSION_STRING;
import static com.archaeology.util.StateStatic.selectedSchema;

public class CheatSheet
{
    /**
     * Set items to spinner
     * @param aContext - calling context
     * @param aSpinner - spinner to set
     * @param items - items for spinner
     */
    public static void setSpinnerItems(Context aContext, Spinner aSpinner, List<String> items)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(aContext, R.layout.spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aSpinner.setAdapter(adapter);
    }

    /**
     * Read IP and MAC addresses from ARP file
     * @return Returns a list of devices
     */
    public static String findIPFromMAC(String MACAddress)
    {
        BufferedReader bufferedReader = null;
        String IP = null;
        try
        {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4)
                {
                    String MAC = splitted[3];
                    if (MAC.matches("..:..:..:..:..:..") && MAC.equals(MACAddress))
                    {
                        IP = splitted[0];
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                bufferedReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return IP;
    }

    /**
     * Creating a thumbnail for requested image
     * @param inputFileName - image file
     * @return Returns image URI
     */
    public static Uri getThumbnail(String inputFileName)
    {
        String dir = "/Archaeology/";
        if (selectedSchema.equals("Archon.Find"))
        {
            dir = "/FloridaArchaeology/";
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + dir);
        String originalFilePath = mediaStorageDir.getPath() + File.separator + inputFileName;
        String thumbPath = mediaStorageDir.getPath() + File.separator + THUMBNAIL_EXTENSION_STRING;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(originalFilePath, options);
        File thumbFile = new File(thumbPath);
        // creating a thumbnail image and setting the bounds of the thumbnail
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(originalFilePath),
                600, 500);
        try
        {
            FileOutputStream fos = new FileOutputStream(thumbFile);
            thumbImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return Uri.fromFile(thumbFile);
    }

    /**
     * Correct for exif rotation in image
     * @param img - image to rotate
     * @param context - calling context
     * @param selectedImage - image uri
     * @return Returns the rotated image
     * @throws IOException if the image cannot be opened
     */
    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException
    {
        if (selectedImage.getScheme().equals("content"))
        {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null,
                    null, null);
            if (c.moveToFirst())
            {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        }
        else
        {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    /**
     * Rotate an image
     * @param img - image to rotate
     * @param degree - degree to rotate
     * @return Returns the rotated image
     */
    private static Bitmap rotateImage(Bitmap img, int degree)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    /**
     * Count number of links in the string
     * @param body - string to search
     * @return Returns the number of links in the string
     */
    private static int countLinks(String body)
    {
        int count = 0;
        while (body.indexOf("a href") > 0)
        {
            // Skipping past "a href = '"
            body = body.substring(body.indexOf("a href") + 10);
            count++;
        }
        return count;
    }

    /**
     * Takes incoming link list and converts to an ArrayList
     * @param response - response HTML to convert
     * @return Returns an ArrayList of the link list
     */
    public static ArrayList<String> convertLinkListToArray(String response)
    {
        int links = countLinks(response);
        ArrayList<String> tmpArray = new ArrayList<>(links);
        for (int i = 0; i < links; i++)
        {
            response = response.substring(response.indexOf("?") + 1);
            String URL = response.substring(0, response.indexOf("\'"));
            tmpArray.add(URL.substring(URL.lastIndexOf("=") + 1));
        }
        return tmpArray;
    }

    /**
     * Create a File for saving an image
     * @param fileName - name of file
     * @return Returns the file
     */
    public static File getOutputMediaFile(String fileName)
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Archaeology/");
        if (!mediaStorageDir.isDirectory())
        {
            if (!mediaStorageDir.mkdirs())
            {
                return null;
            }
        }
        // Create a media file name
        return new File(mediaStorageDir.getPath() + File.separator + fileName + ".jpg");
    }

    /**
     * Open SettingsActivity
     * @param anActivity - anActivity - calling activity
     */
    public static void goToSettings(Activity anActivity)
    {
        Intent myIntent = new Intent(anActivity, SettingsActivity.class);
        anActivity.startActivity(myIntent);
    }
}