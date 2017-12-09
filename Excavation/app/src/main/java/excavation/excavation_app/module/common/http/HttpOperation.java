// HTTP Operation
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
import excavation.excavation_app.module.common.http.Response.ResponseResult;
import excavation.excavation_app.module.common.http.bean.HTTPObject;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.util.Log;
public abstract class HTTPOperation
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
    protected String generateURLWithParams(final HTTPRequester info,
                                           Map<Request, String> mapValues, String url)
    {
        String dhagsj = "http://" + url + "/bil/webservices/";
        StringBuilder finalURL = new StringBuilder(dhagsj).append(info.getFileName()).append(QUE);
        if (mapValues != null && mapValues.size() > 0)
        {
            for (Request paramName: mapValues.keySet())
            {
                finalURL.append(AND).append(paramName.getParameter()).append(EQ)
                        .append(mapValues.get(paramName));
            }
        }
        String newURL = finalURL.toString().replaceAll(" ", "%20");
        newURL = newURL.replaceAll("\\r", "");
        newURL = newURL.replaceAll("\\t", "");
        newURL = newURL.replaceAll("\\n\\n", "%20");
        newURL = newURL.replaceAll("\\n", "%20");
        final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        String urlEncoded = Uri.encode(newURL, ALLOWED_URI_CHARS);
        Log.e("URL: ", urlEncoded);
        return urlEncoded;
    }

    /**
     * HTTP request
     * @param HTTPObject - HTTP object
     * @return Returns the response
     */
    protected HTTPObject request(HTTPObject HTTPObject)
    {
        String newURL = HTTPObject.getURL().replaceAll(" ", "%20");
        newURL = newURL.replaceAll("\\r", "");
        newURL = newURL.replaceAll("\\t", "");
        newURL = newURL.replaceAll("\\n\\n", "%20");
        newURL = newURL.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newURL);
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
        int status;
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
                HTTPObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                status = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                status = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            status = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        HTTPObject.setStatus(status);
        return HTTPObject;
    }

    /**
     * HTTP request
     * @param HTTPObject - HTTP object
     * @param b - parameter
     * @param a - value
     * @return Returns the response
     */
    protected HTTPObject request(HTTPObject HTTPObject, String b, String a)
    {
        String newURL = HTTPObject.getURL().replaceAll(" ", "%20");
        newURL = newURL.replaceAll("\\r", "");
        newURL = newURL.replaceAll("\\t", "");
        newURL = newURL.replaceAll("\\n\\n", "%20");
        newURL = newURL.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newURL);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream()));
            writer.append("GET ").append(b).append("\nGET ").append(a).append("\n");
            writer.close();
            client.setDoOutput(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        int status;
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
                HTTPObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                status = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                status = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            status = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        HTTPObject.setStatus(status);
        return HTTPObject;
    }

    /**
     * HTTP request
     * @param HTTPObject - HTTP object
     * @param imageFile - image
     * @return Returns the response
     */
    protected HTTPObject request(HTTPObject HTTPObject, ArrayList<String> imageFile)
    {
        String newURL = HTTPObject.getURL().replaceAll(" ", "%20");
        newURL = newURL.replaceAll("\\r", "");
        newURL = newURL.replaceAll("\\t", "");
        newURL = newURL.replaceAll("\\n\\n", "%20");
        newURL = newURL.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newURL);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream()));
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
        int status;
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
                HTTPObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                status = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                status = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            status = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        HTTPObject.setStatus(status);
        return HTTPObject;
    }

    /**
     * HTTP request
     * @param HTTPObject - HTTP object
     * @param imageFile - image
     * @return Returns the response
     */
    protected HTTPObject request(HTTPObject HTTPObject, String imageFile)
    {
        String newURL = HTTPObject.getURL().replaceAll(" ", "%20");
        newURL = newURL.replaceAll("\\r", "");
        newURL = newURL.replaceAll("\\t", "");
        newURL = newURL.replaceAll("\\n\\n", "%20");
        newURL = newURL.replaceAll("\\n", "%20");
        URL url;
        HttpURLConnection client;
        try
        {
            url = new URL(newURL);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("USER-AGENT", "Excavation/1.0");
            client.setRequestProperty("ACCEPT-LANGUAGE", "en-US");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    client.getOutputStream()));
            writer.append("GET ").append(imageFile).append("\n");
            writer.close();
            client.setDoOutput(true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        int status;
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
                HTTPObject.setResponseString(resp.substring(resp.indexOf("\n") + 1));
                status = AppConstants.INT_STATUS_SUCCESS;
            }
            else
            {
                status = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
            }
        }
        catch (ConnectTimeoutException e)
        {
            status = AppConstants.INT_STATUS_FAILED_TIMEOUT;
        }
        catch (IOException e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
        }
        catch (Exception e)
        {
            status = AppConstants.INT_STATUS_FAILED_IO;
            e.printStackTrace();
        }
        finally
        {
            System.gc();
        }
        HTTPObject.setStatus(status);
        return HTTPObject;
    }

    /**
     * Check status
     * @param HTTPObject - HTTP object
     * @param data - response
     */
    protected void checkHTTPStatus(HTTPObject HTTPObject, ResponseData data)
    {
        if (data == null)
        {
            data = new ResponseData();
        }
        data.result = ResponseResult.failed;
        data.resultMsg = MessageConstants.NO_DATA_FOUND;
        switch (HTTPObject.getStatus())
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
                data.result = ResponseResult.success;
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