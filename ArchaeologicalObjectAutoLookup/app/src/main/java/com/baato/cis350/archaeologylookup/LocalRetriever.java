// Retriever implementation for locally stored DBs
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package com.baato.cis350.archaeologylookup;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
public class LocalRetriever extends AppCompatActivity implements Retriever
{
    String stringjson;
    private JSONObject json;
    Context context;
    private String location;
    /**
     * Constructor
     * @param context - calling context
     * @param location - db location
     */
    public LocalRetriever(Context context, String location)
    {
        this.context = context;
        this.location = location;
    }

    /**
     * Retrieves db from android storage. Please save your db there if using this strategy for db
     * handling.
     * @param location - db location
     * @return Returns null
     */
    @Override
    public InputStream retrieve(String location)
    {
        File file = new File(location);
        try
        {
            return new FileInputStream(file);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * No need for download; db is already stored locally
     */
    @Override
    public void download(String url)
    {
    }

    /**
     * Search function
     * @item - item to look for
     */
    @Override
    public String[] search(String item)
    {
        // Set input stream based on file location
        InputStream in = retrieve(location);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        // Hashmap to store database
        HashMap<String, String> map = new HashMap<String, String>();
        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] RowData = line.split(",");
                // First item: object id
                String date = RowData[2];
                // Second item: url
                String value = RowData[RowData.length - 1];
                map.put(date, value);
            }
        }
        catch (IOException ex)
        {
            // handle exception
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                // handle exception
            }
        }
        // Return possible search features
        String searchitem = "";
        String searchdescription = "";
        String searchprovenience = "";
        String searchmaterial = "";
        String searchcuratorial_section = "";
        String[] endresult = new String[7];
        endresult[0] = item;
        endresult[1] = searchitem;
        if (map.containsKey(item))
        {
            endresult[2] = map.get(item);
        }
        else
        {
            endresult[2] = "https://www.penn.museum/collections/object/";
        }
        endresult[3] = searchdescription;
        endresult[4] = searchprovenience;
        endresult[5] = searchmaterial;
        endresult[6] = searchcuratorial_section;
        // return final result
        return endresult;
    }
}