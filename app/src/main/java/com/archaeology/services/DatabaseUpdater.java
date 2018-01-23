// Update database
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.archaeology.services;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import okhttp3.OkHttpClient;
import okhttp3.Request;
public class DatabaseUpdater extends AsyncTask<String, Object, Void>
{
    private AsyncResponse asyncResponse = null;
    public interface AsyncResponse
    {
        /**
         * Finish the process
         */
        void processFinish();
    }
    /**
     * Constructor
     * @param asyncResponse - background response
     */
    public DatabaseUpdater(AsyncResponse asyncResponse)
    {
        this.asyncResponse = asyncResponse;
    }

    /**
     * Background process
     * @param urls - urls to update
     * @return Returns nothing
     */
    protected Void doInBackground(String... urls)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urls[0]).addHeader("Content-Type",
                "application/zip").build();
        okhttp3.Response response;
        try
        {
            response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            File dir = new File(Environment.getExternalStorageDirectory(), "a.zip");
            OutputStream outputStream = new FileOutputStream(dir);
            byte[] buffer = new byte[2 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, len);
            }
            unpackZip(Environment.getExternalStorageDirectory().getPath());
            dir.delete();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Issue POST request
     * @param zipResponse - response
     */
    protected void onPostExecute(Void zipResponse)
    {
        super.onPostExecute(zipResponse);
        asyncResponse.processFinish();
    }

    /**
     * Unzip folder
     * @param path - folder path
     */
    private void unpackZip(String path)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename = "";
            is = new FileInputStream(path + "/a.zip");
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null)
            {
                filename = "/museumjson.csv";
                System.out.println(ze.getName());
                // Need to create directories if not exists, or it will generate an Exception...
                if (ze.isDirectory())
                {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }
                FileOutputStream fOut = new FileOutputStream(path + filename);
                while ((count = zis.read(buffer)) != -1)
                {
                    fOut.write(buffer, 0, count);
                }
                fOut.close();
                zis.closeEntry();
            }
            zis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}