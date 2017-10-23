// Update the museum database
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
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
public class updateDatabaseMuseum implements updateDatabase
{

    private final String FILE = "/museumjson.csv";
    private final String LINK = "http://www.penn.museum/collections/assets/data/all-csv-latest.zip";

    @Override
    public boolean updateNecessary()
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + FILE);
        return !file.exists();
    }

    @Override
    public void doUpdate(Activity activity)
    {
        final Activity toastActivity = activity;
        new databaseUpdater(new databaseUpdater.AsyncResponse()
        {
            public void processFinish(Boolean output)
            {
                Toast.makeText(toastActivity, "Database Update Complete", Toast.LENGTH_LONG).show();
            }
        }).execute(LINK);
    }

    @Override
    public String getDatabaseLoction()
    {
        return Environment.getExternalStorageDirectory().getPath() + FILE;
    }

}

class databaseUpdater extends AsyncTask<String, Object, Void>
{
    public interface AsyncResponse
    {
        void processFinish(Boolean output);
    }

    AsyncResponse asyncResponse = null;

    public databaseUpdater(AsyncResponse asyncResponse)
    {
        this.asyncResponse = asyncResponse;
    }

    protected Void doInBackground(String... urls)
    {
        String responseBody = null;
        OkHttpClient client = new OkHttpClient();
        Request request =
                new Request.Builder().url(urls[0]).addHeader("Content-Type", "application/zip")
                        .build();

        okhttp3.Response response = null;
        try
        {
            response = client.newCall(request).execute();
            InputStream inputStream = response.body().byteStream();
            OutputStream outputStream = new FileOutputStream(
                    new File(Environment.getExternalStorageDirectory(), "a.zip"));
            byte[] buffer = new byte[2 * 1024];
            int len;
            int readLen = 0;
            while ((len = inputStream.read(buffer)) != -1)
            {
                //System.out.println("download loop " + Thread.currentThread().getName());
                outputStream.write(buffer, 0, len);
            }
            //System.out.println(responseBody);
            unpackZip(Environment.getExternalStorageDirectory().getPath(), "/a.zip");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void zipResponse)
    {
        super.onPostExecute(zipResponse);
        asyncResponse.processFinish(true);
    }

    private boolean unpackZip(String path, String zipname)
    {
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                // zapis do souboru
                filename = "/museumjson.csv";
                System.out.println(ze.getName());

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory())
                {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
