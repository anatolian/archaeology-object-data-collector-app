// Main Screen
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class DownloadActivity extends AppCompatActivity
{
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 0;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL = 1;
    HistoryHelper myDatabase;
    OkHttpClient client;
    int mode;
    public LocalRetriever localRetriever;
    /**
     * Activity created
     * @param savedInstanceState - state from memory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        showPermission();
        UpdateDatabase uD = new UpdateDatabaseMuseum();
        if (!uD.updateNecessary())
        {
            uD.doUpdate(this);
        }
        myDatabase = new HistoryHelper(this);
        SQLiteDatabase db = new HistoryHelper(this).getWritableDatabase();
        copyAssets();
        client = new OkHttpClient();
        mode = MODE_PRIVATE;
        try
        {
            run("https://s3.us-east-2.amazonaws.com/archaeology-lookup/test.json");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < fileList().length; i++)
        {
            System.out.println(fileList()[i]);
        }
        setUpLocalDB(uD.getDatabaseLocation());
        //setUpOnlineDB("http://owainwest.me/samplejson.txt");
        Intent intent = new Intent(this, InitialActivity.class);
        startActivity(intent);
    }

    /**
     * Create local DB
     * @param dbloc - db location
     */
    public void setUpLocalDB(String dbloc)
    {
        System.out.println(dbloc);
        Context context = getApplicationContext();
        localRetriever = new LocalRetriever(context, dbloc);
    }

    /**
     * Prompt for permissions
     */
    private void showPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL);
        }
    }

    /**
     * Run the client
     * @param url - db url
     * @throws IOException if a file cannot be read/written to
     */
    public void run(String url) throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback()
        {
            /**
             * Client failed
             * @param call - failing call
             * @param e - error
             */
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            /**
             * Response received
             * @param call - client call
             * @param response - server response
             * @throws IOException if the response is malformed
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (!response.isSuccessful())
                {
                    throw new IOException("Unexpected code " + response);
                }
                String i = response.body().string();
                String filename = "newjson.txt";
                FileOutputStream outputStream;
                try
                {
                    outputStream = openFileOutput(filename, mode);
                    outputStream.write(i.getBytes());
                    outputStream.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Explain page
     * @param title - title of window
     * @param message - alert message
     * @param permission - required permission
     * @param permissionRequestCode - permission code
     */
    private void showExplanation(String title, String message, final String permission,
                                 final int permissionRequestCode)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
            /**
             * User pressed OK
             * @param dialog - alert dialog
             * @param id - selection id
             */
            public void onClick(DialogInterface dialog, int id)
            {
                requestPermission(permission, permissionRequestCode);
            }
        });
        builder.create().show();
    }

    /**
     * Request needed permission
     * @param permissionName - permission name
     * @param permissionRequestCode - permission code
     */
    private void requestPermission(String permissionName, int permissionRequestCode)
    {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }

    /**
     * Copy files
     */
    private void copyAssets()
    {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try
        {
            files = assetManager.list("");
        }
        catch (IOException e)
        {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null)
        {
            for (String filename: files)
            {
                InputStream in = null;
                OutputStream out = null;
                try
                {
                    in = assetManager.open(filename);
                    File outFile = new File(Environment.getExternalStorageDirectory().toString() +
                            "/SimpleAndroidOCR/tessdata/", filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    System.out.println(outFile.getAbsolutePath());
                }
                catch (IOException e)
                {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
                finally
                {
                    if (in != null)
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            // NOOP
                        }
                    }
                    if (out != null)
                    {
                        try
                        {
                            out.close();
                        }
                        catch (IOException e)
                        {
                            // NOOP
                        }
                    }
                }
            }
        }
    }

    /**
     * Copy a file
     * @param in - file stream
     * @param out - output stream
     * @throws IOException if a read or write fails
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
}