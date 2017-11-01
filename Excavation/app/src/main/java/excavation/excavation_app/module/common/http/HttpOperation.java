// HTTP Operation
// @author: anatolian
package excavation.excavation_app.module.common.http;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import excavation.excavation_app.module.common.bean.ResponseData;
import excavation.excavation_app.module.common.constants.AppConstants;
import excavation.excavation_app.module.common.constants.MessageConstants;
import excavation.excavation_app.module.common.http.Response.RESPONSE_RESULT;
import excavation.excavation_app.module.common.http.bean.HttpObject;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.util.Log;
public abstract class HttpOperation
{
    private static final String QUE = "?";
    private static final String AND = "&";
    private static final String EQ = "=";
    /**
     * Create URL
     * @param info - HTTP info
     * @param mapValues - parameters
     * @param url - base URL
     * @return Returns appended URL
     */
    protected String generateUrlWithParams(final HttpRequester info, Map<Request, String> mapValues,
                                           String url)
    {
        String dhagsj = "http://" + url + "/bil/webservices/";
        StringBuilder finalUrl = new StringBuilder(dhagsj).append(info.getFileName()).append(QUE);
        if (mapValues != null && mapValues.size() > 0)
        {
            for (Request paramName: mapValues.keySet())
            {
                finalUrl.append(AND).append(paramName.getParameter()).append(EQ)
                        .append(mapValues.get(paramName));
            }
        }
        String newUrl = finalUrl.toString().replaceAll(" ", "%20");
        newUrl = newUrl.replaceAll("\\r", "");
        newUrl = newUrl.replaceAll("\\t", "");
        newUrl = newUrl.replaceAll("\\n\\n", "%20");
        newUrl = newUrl.replaceAll("\\n", "%20");
        final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        String urlEncoded = Uri.encode(newUrl, ALLOWED_URI_CHARS);
        Log.e("URL: ", urlEncoded);
        return urlEncoded;
    }

    /**
     * HTTP request
     * @param httpObject - HTTP object
     * @return Returns the response
     */
    protected HttpObject request(HttpObject httpObject)
    {
        String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
        newUrl = newUrl.replaceAll("\\r", "");
        newUrl = newUrl.replaceAll("\\t", "");
        newUrl = newUrl.replaceAll("\\n\\n", "%20");
        newUrl = newUrl.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newUrl);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            client.setDoOutput(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        int STATUS;
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder response = new StringBuilder();
            while (br.ready())
            {
                response.append(br.readLine());
            }
            br.close();
            String resp = response.toString();
            String[] tokens = resp.split("\n");
            int statusCode = Integer.parseInt(tokens[0].split(" ")[1]);
            if (statusCode == 200)
            {
                httpObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                STATUS = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        httpObject.setStatus(STATUS);
        return httpObject;
    }

    /**
     * HTTP request
     * @param httpObject - HTTP object
     * @param b - parameter
     * @param a - value
     * @return Returns the response
     */
    protected HttpObject request(HttpObject httpObject, String b, String a)
    {
        String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
        newUrl = newUrl.replaceAll("\\r", "");
        newUrl = newUrl.replaceAll("\\t", "");
        newUrl = newUrl.replaceAll("\\n\\n", "%20");
        newUrl = newUrl.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newUrl);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            writer.append("GET ").append(b).append("\nGET ").append(a).append("\n");
            writer.close();
            client.setDoOutput(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        int STATUS;
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder response = new StringBuilder();
            while (br.ready())
            {
                response.append(br.readLine());
            }
            br.close();
            String resp = response.toString();
            String[] tokens = resp.split("\n");
            int statusCode = Integer.parseInt(tokens[0].split(" ")[1]);
            if (statusCode == 200)
            {
                httpObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                STATUS = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        httpObject.setStatus(STATUS);
        return httpObject;
    }

    /**
     * HTTP request
     * @param httpObject - HTTP object
     * @param imageFile - image
     * @return Returns the response
     */
    protected HttpObject request(HttpObject httpObject, ArrayList<String> imageFile)
    {
        String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
        newUrl = newUrl.replaceAll("\\r", "");
        newUrl = newUrl.replaceAll("\\t", "");
        newUrl = newUrl.replaceAll("\\n\\n", "%20");
        newUrl = newUrl.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newUrl);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            for (String s: imageFile)
            {
                writer.append("GET ").append(s).append("\n");
            }
            writer.close();
            client.setDoOutput(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        int STATUS;
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder response = new StringBuilder();
            while (br.ready())
            {
                response.append(br.readLine());
            }
            br.close();
            String resp = response.toString();
            String[] tokens = resp.split("\n");
            int statusCode = Integer.parseInt(tokens[0].split(" ")[1]);
            if (statusCode == 200)
            {
                httpObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                STATUS = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        httpObject.setStatus(STATUS);
        return httpObject;
    }

    /**
     * HTTP request
     * @param httpObject - HTTP object
     * @param imageFile - image
     * @return Returns the response
     */
    protected HttpObject request(HttpObject httpObject, String imageFile)
    {
        String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
        newUrl = newUrl.replaceAll("\\r", "");
        newUrl = newUrl.replaceAll("\\t", "");
        newUrl = newUrl.replaceAll("\\n\\n", "%20");
        newUrl = newUrl.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newUrl);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            writer.append("GET ").append(imageFile).append("\n");
            writer.close();
            client.setDoOutput(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        int STATUS;
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            StringBuilder response = new StringBuilder();
            while (br.ready())
            {
                response.append(br.readLine());
            }
            br.close();
            String resp = response.toString();
            String[] tokens = resp.split("\n");
            int statusCode = Integer.parseInt(tokens[0].split(" ")[1]);
            if (statusCode == 200)
            {
                httpObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                STATUS = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            STATUS = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        httpObject.setStatus(STATUS);
        return httpObject;
    }

    /**
     * Check status
     * @param httpObject - HTTP object
     * @param data - response
     */
    protected void checkHttpStatus(HttpObject httpObject, ResponseData data)
    {
        if (data == null)
        {
            data = new ResponseData();
        }
        data.result = RESPONSE_RESULT.failed;
        data.resultMsg = MessageConstants.NO_DATA_FOUND;
        switch (httpObject.getStatus())
        {
            case AppConstants.INT_STATUS_FAILED_DOWNLOAD:
                data.resultMsg = MessageConstants.FAILED_TO_CONNECT;
                break;
            case AppConstants.INT_STATUS_FAILED_TIMEOUT:
                data.resultMsg = MessageConstants.FAILED_TIMEOUT;
                break;
            case AppConstants.INT_STATUS_FAILED_IO:
                data.resultMsg = MessageConstants.FAILED_TO_READ;
                break;
            case AppConstants.INT_STATUS_SUCCESS:
                data.resultMsg = null;
                data.result = RESPONSE_RESULT.success;
                break;
        }
    }

    /**
     * Get a value
     * @param key - item key
     * @param resItem - response
     * @return Returns the value
     * @throws JSONException if the response is malformed
     */
    protected String get(String key, JSONObject resItem) throws JSONException
    {
        return (resItem.has(key)) ? resItem.getString(key) : null;
    }
}