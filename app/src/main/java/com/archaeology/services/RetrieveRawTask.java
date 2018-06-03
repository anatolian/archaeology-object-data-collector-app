// Retrieve a raw image
// @author Christopher Besser
package com.archaeology.services;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.archaeology.services.AsyncHTTPCallbackWrapper;
import com.archaeology.services.AsyncHerokuHTTPWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import static com.archaeology.util.StateStatic.globalWebServerURL;
public class RetrieveRawTask extends AsyncTask<String, Void, Uri>
{
    private String hemisphere, zone, easting, northing, find;
    /**
     * Retrieve a raw file asynchronously
     * @param params - hemisphere, zone, easting, northing, find number, image url
     * @return Returns the local file
     */
    protected Uri doInBackground(String... params)
    {
        InputStream input = null;
        FileOutputStream out = null;
        String path = Environment.getExternalStorageDirectory() + "/Archaeology/temp.arw";
        hemisphere = params[0];
        zone = params[1];
        easting = params[2];
        northing = params[3];
        find = params[4];
        try
        {
            java.net.URL url = new java.net.URL(params[5]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            out = new FileOutputStream(path);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = input.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }
            out.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (input != null)
                {
                    input.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        File tempFile = new File(path);
        return Uri.parse(tempFile.toURI().toString());
    }

    /**
     * Image was retrieved
     * @param rawURI - raw image local URI
     */
    protected void onPostExecute(Uri rawURI)
    {
        AsyncHerokuHTTPWrapper.makeImageUpload(globalWebServerURL + "/upload_file", rawURI, hemisphere,
                zone, easting, northing, find, new AsyncHTTPCallbackWrapper() {
            /**
             * Connection succeeded
             * @param response - HTTP response
             */
            @Override
            public void onSuccessCallback(String response)
            {
                super.onSuccessCallback(response);
            }
        });
    }
}