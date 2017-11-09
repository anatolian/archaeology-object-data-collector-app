// Update database
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology;
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
class DatabaseUpdater extends AsyncTask<String, Object, Void>
{
    AsyncResponse asyncResponse = null;
    public interface AsyncResponse
    {
        /**
         * Finish the process
         * @param output - whether there is output
         */
        void processFinish(Boolean output);
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
        Request request = new Request.Builder().url(urls[0]).addHeader("Content-Type", "application/zip").build();
        okhttp3.Response response = null;
        try
        {
            response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            OutputStream outputStream = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory(), "a.zip"));
            byte[] buffer = new byte[2 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, len);
            }
            unpackZip(Environment.getExternalStorageDirectory().getPath(), "/a.zip");
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
        asyncResponse.processFinish(true);
    }

    /**
     * Unzip folder
     * @param path - folder path
     * @param zipName - name of zip file
     * @return Returns whether the unzip succeeded
     */
    private boolean unpackZip(String path, String zipName)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipName);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null)
            {
                // zapis do souboru
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
                // cteni zipu a zapis
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
            return false;
        }
        return true;
    }
}