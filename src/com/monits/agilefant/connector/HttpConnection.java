package com.monits.agilefant.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;

import roboguice.util.Ln;
import android.util.Log;

import com.monits.agilefant.exception.RequestException;

/**
 * Provides methods per connection with server
 *
 */
public class HttpConnection {

	public static final String LOGIN = "LOGIN";
	private static final String HTTP_CONNECTION_IMPL = "HttpConnectionImpl";
	private final int HTTP_200 = 200;
	private final int HTTP_302 = 302;
	private final int HTTP_304 = 304;
	private final int HTTP_400 = 400;
	private final int HTTP_500 = 500;

	private List<NameValuePair> params = new ArrayList<NameValuePair>();

	private static final int TIMEOUT = 40000;

	private static final DefaultHttpClient client;
	private static final int DEFAULT_REATTEMPTS = 5;

	static {
		client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT);
	}

	/**
	 * Method get
	 *
	 * @param url The URL to GET
	 * @return Server response
	 * @throws RequestException
	 */
	public String executeGet(String url) throws RequestException {
		StringBuffer getUrl = new StringBuffer(url)
			.append("?")
			.append(URLEncodedUtils.format(params, "UTF-8"));
		HttpGet get = new HttpGet(getUrl.toString());
		return execute(get);
	}

	/**
	 * Method post
	 *
	 * @param url The URL to POST
	 * @param xml The xml in format string
	 * @return Server response
	 * @throws RequestException
	 */
	public String executePost(String url, String xml) throws RequestException {
		return executePost(url, xml, new HashMap<String, String>());
	}

	/**
	 * Method post
	 *
	 * @param url The URL to POST
	 * @param xml The xml in format string
	 * @param headers The extra headers to set for this operation
	 * @return Server response
	 * @throws RequestException
	 */
	public String executePost(String url, String xml, Map<String, String> headers) throws RequestException {
		HttpPost post = null;
		try {
			post = new HttpPost(new URI(url));
			post.setEntity(new UrlEncodedFormEntity(params));
			StringEntity stringEntity = new StringEntity(xml);
			post.setEntity(stringEntity);

			for (Map.Entry<String, String> entry : headers.entrySet()) {
				post.addHeader(entry.getKey(), entry.getValue());
			}

		} catch (URISyntaxException e) {
			Ln.e(HTTP_CONNECTION_IMPL, "URI Syntax" + e);
		} catch (UnsupportedEncodingException e) {
			Ln.e(HTTP_CONNECTION_IMPL, "Unsupported Encoding" + e);
		}

		return execute(post);
	}

	/**
	 * Method post
	 *
	 * @param url The URL to POST
	 * @return Server response
	 * @throws RequestException
	 */
	public String executePost(String url) throws RequestException {
		StringBuffer getUrl = new StringBuffer(url)
			.append("?")
			.append(URLEncodedUtils.format(params, "UTF-8"));
		HttpPost post = new HttpPost(getUrl.toString());
		return execute(post);
	}

	/**
	 * Execute the method
	 *
	 * @param  Method to be executed
	 * @return Server response
	 * @throws RequestException
	 */
	private String execute(HttpRequestBase method, int reattempts) throws RequestException {
		try {

			HttpResponse res = client.execute(method);

			StatusLine statusLine = res.getStatusLine();

			switch (statusLine.getStatusCode()) {
			case HTTP_200:
				return convertStreamToString(res.getEntity().getContent());

			case HTTP_302:
				return res.getFirstHeader("Location").getValue();

			case HTTP_304:
				return null;

			case HTTP_400:
				Log.e(HTTP_CONNECTION_IMPL, "Error 400");
				throw new RequestException();

			case HTTP_500:
				Log.e(HTTP_CONNECTION_IMPL, "Error 500");
				throw new RequestException();

			default:
				break;
			}

		} catch (ClientProtocolException e) {
			Log.e(HTTP_CONNECTION_IMPL, "Client Exception ",e);
			throw new RequestException(e);
		} catch (IllegalStateException e) {
			Log.e(HTTP_CONNECTION_IMPL, "Illegal State ",e);
			throw new RequestException(e);
		} catch (IOException e) {
			Log.e(HTTP_CONNECTION_IMPL, "IO Exception ",e);

			if (reattempts > 1) {
				return execute(method, reattempts - 1);
			}

			throw new RequestException(e);
		}
		return null;
	}

	/**
	 * Execute the method
	 *
	 * @param  Method to be executed
	 * @return Server response
	 * @throws RequestException
	 */
	private String execute(HttpRequestBase method) throws RequestException {
		return execute(method, DEFAULT_REATTEMPTS);
	}

	/**
	 * Convert inputStream to string
	 *
	 * @param inputStream
	 * @return string
	 */
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		try {
			int read;

			while ((read = reader.read()) != -1) {
				sb.append((char) read);
			}
		} catch (IOException e) {
			Log.e(HTTP_CONNECTION_IMPL, "IOExeption in convertStreamToString", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e(HTTP_CONNECTION_IMPL, "IOExeption closing the input stream", e);
			}
		}
		return sb.toString();
	}

	/**
	 * add parameter per request
	 */
	public void addParameter(String name, String value) {
		this.params.add(new BasicNameValuePair(name, value));
	}

	/**
	 * clean parameter list
	 */
	public void cleanParameters(){
		this.params.clear();
	}
}