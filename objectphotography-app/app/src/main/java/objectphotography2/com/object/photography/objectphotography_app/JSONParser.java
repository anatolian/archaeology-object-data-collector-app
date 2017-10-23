// Parse JSON
// @author: yashusgowda
package objectphotography2.com.object.photography.objectphotography_app;
import android.content.ContentValues;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
public class JSONParser
{
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    /**
     * constructor
     */
    public JSONParser()
    {
    }

    /**
     * Get HTTP Post query
     * @param params - parameters for POST
     * @return Returns the query
     * @throws UnsupportedEncodingException if the encoding is unsupported
     */
    private String getQuery(ContentValues params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String name: params.keySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                result.append("&");
            }
            result.append(URLEncoder.encode(name, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.getAsString(name), "UTF-8"));
        }
        return result.toString();
    }

    /**
     * function get json from url by making HTTP POST or GET method
     * @param url - target URL
     * @param method - GET or POST
     * @param params - HTTP parameters
     */
    public JSONObject makeHttpRequest(String url, String method, ContentValues params)
    {
        try
        {
            // check for request method
            if (method.equals("POST"))
            {
                // request method is POST defaultHttpClient
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestProperty("User-Agent", "");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                is = conn.getInputStream();
            }
            else if(method.equals("GET"))
            {
                // request method is GET
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestProperty("User-Agent", "");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                url += "?" + getQuery(params);
                conn.connect();
                is = conn.getInputStream();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
            is.close();
            json = sb.toString();
        }
        catch (Exception e)
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try
        {
            jObj = new JSONObject(json);
        }
        catch (JSONException e)
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;
    }

    /**
     * Read a JSON object from internet
     * @param url - file URL
     * @return Returns the JSON object
     */
    public JSONObject getJSONFromUrl(final String url)
    {
        // Making HTTP request
        try
        {
            // Construct the client and the HTTP request.
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", "");
            conn.setRequestMethod("POST");
            // Execute the POST request and store the response locally.
            conn.connect();
            // Extract data from the response. Open an inputStream with the data content.
            is = conn.getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            // Create a BufferedReader to parse through the inputStream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            // Declare a string builder to help with the parsing.
            StringBuilder sb = new StringBuilder();
            // Declare a string to store the JSON object data in string form.
            String line = null;
            // Build the string until null.
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
            // Close the input stream.
            is.close();
            // Convert the string builder data to an actual string.
            json = sb.toString();
        }
        catch (Exception e)
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // Try to parse the string to a JSON object
        try
        {
            jObj = new JSONObject(json);
        }
        catch (JSONException e)
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // Return the JSON Object.
        return jObj;
    }
}