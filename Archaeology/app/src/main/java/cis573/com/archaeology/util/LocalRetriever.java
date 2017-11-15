// Retriever implementation for locally stored DBs
// @author: Andrej Ilic, Ben Greenberg, Anton Relin, and Tristrum Tuttle
package cis573.com.archaeology.util;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import cis573.com.archaeology.services.Retriever;
public class LocalRetriever extends AppCompatActivity implements Retriever
{
    Context context;
    private String location;
    /**
     * Useless constructor
     */
    public LocalRetriever()
    {
    }

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
     * Search function
     * @param item - item to look for
     */
    @Override
    public String[] search(String item)
    {
        // Set input stream based on file location
        InputStream in = retrieve(location);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        // Hashmap to store database
        HashMap<String, String> map = new HashMap<>();
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
        String searchItem = "";
        String searchDescription = "";
        String searchProvenience = "";
        String searchMaterial = "";
        String searchCuratorialSection = "";
        String[] endResult = new String[7];
        endResult[0] = item;
        endResult[1] = searchItem;
        if (map.containsKey(item))
        {
            endResult[2] = map.get(item);
        }
        else
        {
            endResult[2] = "https://www.penn.museum/collections/object/";
        }
        endResult[3] = searchDescription;
        endResult[4] = searchProvenience;
        endResult[5] = searchMaterial;
        endResult[6] = searchCuratorialSection;
        // return final result
        return endResult;
    }
}