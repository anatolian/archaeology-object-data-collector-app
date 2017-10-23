package module.common.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import module.common.bean.ResponseData;
import module.common.constants.AppConstants;
import module.common.constants.MessageConstants;
import module.common.http.Response.RESPONSE_RESULT;
import module.common.http.bean.HttpObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.appenginedemo.db.DBHelper;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public abstract class HttpOperation 
{

	protected static final String QUE = "?";
	protected static final String AND = "&";
	protected static final String EQ = "=";
	//protected static final String MAIN_URL = "http://192.168.1.5/bil/webservices/";
	//protected static final String MAIN_URL ="http://192.168.0.33/bil/webservices/";
	protected static final String URL_FILLER = "1=1";
	protected String generateUrlWithParams(final HttpRequester info,Map<Request, String> mapValues,String url) 
	{
		//String finalUrl = MAIN_URL + info.getFileName() + QUE ;
		String dhagsj="http://"+url+"/bil/webservices/";
		String finalUrl = dhagsj + info.getFileName() + QUE ;

		if (mapValues != null && mapValues.size() > 0)
		{

			for (Request paramName : mapValues.keySet()) 
			{
				finalUrl += AND + paramName.getParameter() + EQ	+ mapValues.get(paramName);
			}
		}
		String newUrl = finalUrl.replaceAll(" ", "%20");
		newUrl = newUrl.replaceAll("\\r", "");
		newUrl = newUrl.replaceAll("\\t", "");
		newUrl = newUrl.replaceAll("\\n\\n", "%20");
		newUrl = newUrl.replaceAll("\\n", "%20");
		final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
		String urlEncoded = Uri.encode(newUrl, ALLOWED_URI_CHARS);
		Log.e("URL: ", urlEncoded);
		//Debugger.message("URL : " + newUrl);
		return urlEncoded;
	}
	protected HttpObject request(HttpObject httpObject)
	{
		StringBuilder builder = new StringBuilder();
		String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
		newUrl = newUrl.replaceAll("\\r", "");
		newUrl = newUrl.replaceAll("\\t", "");
		newUrl = newUrl.replaceAll("\\n\\n", "%20");
		newUrl = newUrl.replaceAll("\\n", "%20");
	
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(newUrl);
		HttpResponse response = null;
		StatusLine statusLine = null;
		HttpEntity entity = null;
		InputStream content = null;
		BufferedReader reader = null;

		int STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
		try
		{
			response = client.execute(httpPost);
			statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) 
			{
				entity = response.getEntity();
				content = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					builder.append(line);
				}
				STATUS = AppConstants.INT_STATUS_SUCCESS;

				reader.close();
				content.close();

			} 
			else 
			{
				STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
			}

		}
		catch (ClientProtocolException e) 
		{
			STATUS = AppConstants.INT_STATUS_FAILED_CLIENT;

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
			try 
			{
				if (reader != null) 
				{
					reader.close();
				}
			} 
			catch (Exception e) 
			{
			}
			finally 
			{
				reader = null;
			}
			try
			{
				if (content != null) {
					content.close();
				}
			} catch (Exception e) {
			} finally {
				content = null;
			}

			response = null;
			statusLine = null;
			entity = null;

			httpPost = null;
			client = null;
			newUrl = null;
			System.gc();
		}

		httpObject.setResponseString(builder.toString());
		httpObject.setStatus(STATUS);

		return httpObject;
	}
	protected HttpObject request(HttpObject httpObject,String b,String a) 
	{

		StringBuilder builder = new StringBuilder();
		String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
		newUrl = newUrl.replaceAll("\\r", "");
		newUrl = newUrl.replaceAll("\\t", "");
		newUrl = newUrl.replaceAll("\\n\\n", "%20");
		newUrl = newUrl.replaceAll("\\n", "%20");
		
		Bitmap bm;

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(newUrl);
		HttpResponse response = null;
		StatusLine statusLine = null;
		HttpEntity entity = null;
		InputStream content = null;
		BufferedReader reader = null;

		int STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
		try {

			/*
			 * bm = BitmapFactory.decodeFile(imageFile);
			 * 
			 * ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 * 
			 * bm.compress(CompressFormat.JPEG,75, bos); byte[] data =
			 * bos.toByteArray(); ByteArrayBody bab = new ByteArrayBody(data,
			 * "login.jpg");
			 * 
			 * MultipartEntity reqEntity = new
			 * MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 * reqEntity.addPart("image_name", bab);
			 * httpPost.setEntity(reqEntity);
			 */

			/*
			 * MultipartEntity mpEntity = new MultipartEntity(); File
			 * imageFile1=new File(imageFile); ContentBody content1 = new
			 * FileBody(imageFile1,"image/png"); mpEntity.addPart("image_name",
			 * content1);
			 * 
			 * httpPost.setEntity(mpEntity);
			 */

			MultipartEntity entityMulti = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entityMulti.addPart("before_image", new FileBody(new File(b)));
			entityMulti.addPart("after_image", new FileBody(new File(a)));
			httpPost.setEntity(entityMulti);

			response = client.execute(httpPost);
			statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {

				entity = response.getEntity();
				content = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				STATUS = AppConstants.INT_STATUS_SUCCESS;

				reader.close();
				content.close();

			} else {
				STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
			}

		} catch (ClientProtocolException e) {
			STATUS = AppConstants.INT_STATUS_FAILED_CLIENT;

		} catch (ConnectTimeoutException e) {
			STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;

		} catch (IOException e) {
			STATUS = AppConstants.INT_STATUS_FAILED_IO;

		} finally {

			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			} finally {
				reader = null;
			}

			try {
				if (content != null) {
					content.close();
				}
			} catch (Exception e) {
			} finally {
				content = null;
			}

			response = null;
			statusLine = null;
			entity = null;

			httpPost = null;
			client = null;
			newUrl = null;
			System.gc();
		}

		httpObject.setResponseString(builder.toString());
		httpObject.setStatus(STATUS);
		return httpObject;
	}
	
	protected HttpObject request(HttpObject httpObject,
			ArrayList<String> imageFile) {

		StringBuilder builder = new StringBuilder();
		String newUrl = httpObject.getUrl().replaceAll(" ", "%20");

		Bitmap bm;

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(newUrl);
		HttpResponse response = null;
		StatusLine statusLine = null;
		HttpEntity entity = null;
		InputStream content = null;
		BufferedReader reader = null;

		int STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
		try {

			MultipartEntity entityMulti = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			for (int i = 0; i < imageFile.size(); i++) {
				if (i == 0) {
					entityMulti.addPart("image_path", new FileBody(
							new File(imageFile.get(i))));
				} else {
					entityMulti.addPart("image_path" + i, new FileBody(
							new File(imageFile.get(i))));
				}

			}

			httpPost.setEntity(entityMulti);

			response = client.execute(httpPost);
			statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {

				entity = response.getEntity();
				content = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				STATUS = AppConstants.INT_STATUS_SUCCESS;

				reader.close();
				content.close();

			} else {
				STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
			}

		} catch (ClientProtocolException e) {
			STATUS = AppConstants.INT_STATUS_FAILED_CLIENT;

		} catch (ConnectTimeoutException e) {
			STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;

		} catch (IOException e) {
			STATUS = AppConstants.INT_STATUS_FAILED_IO;

		} finally {

			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			} finally {
				reader = null;
			}

			try {
				if (content != null) {
					content.close();
				}
			} catch (Exception e) {
			} finally {
				content = null;
			}

			response = null;
			statusLine = null;
			entity = null;

			httpPost = null;
			client = null;
			newUrl = null;
			System.gc();
		}

		httpObject.setResponseString(builder.toString());
		httpObject.setStatus(STATUS);
		return httpObject;
	}
	
	protected HttpObject request(HttpObject httpObject, String imageFile) {

		StringBuilder builder = new StringBuilder();
		String newUrl = httpObject.getUrl().replaceAll(" ", "%20");
		newUrl = newUrl.replaceAll("\\r", "");
		newUrl = newUrl.replaceAll("\\t", "");
		newUrl = newUrl.replaceAll("\\n\\n", "%20");
		newUrl = newUrl.replaceAll("\\n", "%20");
		
		Bitmap bm;
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(newUrl);
		HttpResponse response = null;
		StatusLine statusLine = null;
		HttpEntity entity = null;
		InputStream content = null;
		BufferedReader reader = null;
		int STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
		try
		{
			/*
			 * bm = BitmapFactory.decodeFile(imageFile);
			 * 
			 * ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 * 
			 * bm.compress(CompressFormat.JPEG,75, bos); byte[] data =
			 * bos.toByteArray(); ByteArrayBody bab = new ByteArrayBody(data,
			 * "login.jpg");
			 * 
			 * MultipartEntity reqEntity = new
			 * MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 * reqEntity.addPart("image_name", bab);
			 * httpPost.setEntity(reqEntity);
			 */
			/*
			 * MultipartEntity mpEntity = new MultipartEntity(); File
			 * imageFile1=new File(imageFile); ContentBody content1 = new
			 * FileBody(imageFile1,"image/png"); mpEntity.addPart("image_name",
			 * content1);
			 * 
			 * httpPost.setEntity(mpEntity);
			 */

			MultipartEntity entityMulti = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entityMulti.addPart("image_path", new FileBody(new File(imageFile)));
			httpPost.setEntity(entityMulti);

			response = client.execute(httpPost);
			statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			if (statusCode == 200) {

				entity = response.getEntity();
				content = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				STATUS = AppConstants.INT_STATUS_SUCCESS;

				reader.close();
				content.close();

			} 
			else 
			{
				STATUS = AppConstants.INT_STATUS_FAILED_DOWNLOAD;
			}

		} 
		catch (ClientProtocolException e) 
		{
			STATUS = AppConstants.INT_STATUS_FAILED_CLIENT;

		}
		catch (ConnectTimeoutException e) 
		{
			STATUS = AppConstants.INT_STATUS_FAILED_TIMEOUT;

		} 
		catch (IOException e) 
		{
			STATUS = AppConstants.INT_STATUS_FAILED_IO;

		}
		finally 
		{
			try 
			{
				if (reader != null) 
				{
					reader.close();
				}
			} catch (Exception e) {
			} 
			finally 
			{
				reader = null;
			}
			try
			{
				if (content != null) 
				{
					content.close();
				}
			} catch (Exception e) {
			} 
			finally 
			{
				content = null;
			}
			response = null;
			statusLine = null;
			entity = null;

			httpPost = null;
			client = null;
			newUrl = null;
			System.gc();
		}
		httpObject.setResponseString(builder.toString());
		httpObject.setStatus(STATUS);
		return httpObject;
	}
	protected void checkHttpStatus(HttpObject httpObject, ResponseData data) 
	{
		if (data == null) 
		{
			data = new ResponseData();
		}
		data.result = RESPONSE_RESULT.failed;
		data.resultMsg = MessageConstants.No_Data_Found;

		switch (httpObject.getStatus()) 
		{

		case AppConstants.INT_STATUS_FAILED_DOWNLOAD:
			data.resultMsg = MessageConstants.Failed_To_Connect;
			break;

		case AppConstants.INT_STATUS_FAILED_TIMEOUT:
			data.resultMsg = MessageConstants.Failed_TimeOut;
			break;

		case AppConstants.INT_STATUS_FAILED_IO:
			data.resultMsg = MessageConstants.Failed_To_Read;
			break;

		case AppConstants.INT_STATUS_SUCCESS:
			data.resultMsg = null;
			data.result = RESPONSE_RESULT.success;
			break;
		}
	}
	protected boolean isHttpError(HttpObject httpObject) 
	{
		boolean isError = false;
		switch (httpObject.getStatus()) 
		{

		case AppConstants.INT_STATUS_FAILED_DOWNLOAD:
			isError = true;
			break;

		case AppConstants.INT_STATUS_FAILED_TIMEOUT:
			isError = true;
			break;

		case AppConstants.INT_STATUS_FAILED_IO:
			isError = true;
			break;

		case AppConstants.INT_STATUS_SUCCESS:
			isError = false;
			break;
		}
		return isError;
	}
	protected String get(String key, JSONObject resItem) throws JSONException 
	{
		return (resItem.has(key)) ? resItem.getString(key) : null;
	}
	protected double getDouble(String key, JSONObject resItem)	throws JSONException
	{
		return (resItem.has(key)) ? resItem.getDouble(key) : null;
	}
	protected JSONObject getJOSNObject(String key, JSONObject resItem) throws JSONException {
		return (resItem.has(key)) ? resItem.getJSONObject(key) : null;
	}
}
