// Retriever implementation for dbs stored online
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archeaologylookup;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class WebRetreiver extends AppCompatActivity implements Retreiver
{
    String stringjson;
    JSONObject json;
    OkHttpClient client;
    Context context;
    String location;
    InputStream is;
    int mode;

    public WebRetreiver(Context context, String location)
    {
        this.context = context;
        this.location = location;
    }

    @Override
    public InputStream retrieve(String location)
    {
        InputStream is;
        try
        {
            run("http://triton11.github.io/testjson.txt");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //Count sheep to sleep
        //Count cows to wait for a download to finish
        while (stringjson == null)
        {
            System.out.println("cow");
        }
        is = IOUtils.toInputStream(stringjson);
        return is;
    }

    //Opens HTTP connection and downloads db
    public void run(String url) throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

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
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    //Calls run to download db
    @Override
    public void download(String url)
    {
        client = new OkHttpClient();
        mode = this.MODE_PRIVATE;
        try
        {
            run("https://s3.us-east-2.amazonaws.com/archaeology-lookup/test.json");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        for (int i = 0; i < fileList().length; i++)
        {
            System.out.println(fileList()[i]);
        }
    }

    //Search for item
    @Override
    public String[] search(String item)
    {
        download("http://triton11.github.io/testjson.txt");
        while (stringjson == null)
        {
            //System.out.println("cow");
        }
        try
        {
            String fixed = loadJSONFromAsset();
            System.out.println(fixed);
            json = new JSONObject(fixed);
            System.out.println(json.names().toString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(json.toString());
        //Now have db as a JSON object
        //Search begins below
        JSONObject translatedsearch;
        String searchitem = "";
        String searchurl;
        String searchdescription = "";
        String searchprovenience = "";
        String searchmaterial = "";
        String searchcuratorial_section = "";
        try
        {
            //fix JSON
            translatedsearch = json.getJSONObject(item);
            //Add features
            searchurl = translatedsearch.getString("url");
            searchitem = translatedsearch.getString("object_name");
            searchdescription = translatedsearch.getString("description");
            searchprovenience = translatedsearch.getString("provenience");
            searchmaterial = translatedsearch.getString("material");
            searchcuratorial_section = translatedsearch.getString("curatorial_section");

        } catch (JSONException e)
        {
            searchurl = "https://www.penn.museum/collections/object/";
        }
        System.out.println(searchurl);
        String[] endresult = new String[7];
        endresult[0] = item;
        endresult[1] = searchitem;
        endresult[2] = searchurl;
        endresult[3] = searchdescription;
        endresult[4] = searchprovenience;
        endresult[5] = searchmaterial;
        endresult[6] = searchcuratorial_section;
        return endresult;
    }

    //Turns Penn museum "json" into actual json
    public String loadJSONFromAsset()
    {
        String json = stringjson;
        String jsonfixed = "{ ";
        String[] jsonArr = json.split("(?=\\{)");
        for (int i = 0; i < jsonArr.length; i++)
        {
            Pattern pattern = Pattern.compile("\"object_number\": (.+)");
            Matcher matcher = pattern.matcher(jsonArr[i]);
            String id_number = "";
            if (matcher.find())
            {
                id_number = matcher.group(1);
                System.out.format("'%s'\n", id_number);
            }
            String fixed = id_number + " : {";

            jsonArr[i] = jsonArr[i].replaceAll(Pattern.quote("{"), fixed);
            jsonfixed += jsonArr[i];
        }
        jsonfixed = jsonfixed.replaceAll(Pattern.quote("}"), "\\},");
        jsonfixed = jsonfixed.substring(0, jsonfixed.length() - 1);
        jsonfixed += "}";
        System.out.println(jsonfixed);
        return jsonfixed;
    }
}
