package com.monits.agilefant.network.request;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.monits.volleyrequests.network.request.GsonRequest;

import java.lang.reflect.Type;

/**
 * Created by lgnanni on 21/10/15.
 */
public class UrlGsonRequest<T> extends GsonRequest {


	/**
	 * Creates a new UrlGsonRequest instance
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param gson The gson instance to parse the response
	 * @param clazz The {@link Type} of the class T
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param cancelListener The listener for errors.
	 * @param jsonBody The contents of the json to be sent in the request's body.
	 */
	public UrlGsonRequest(final int method, final String url, final Gson gson, final Type clazz,
						final Response.Listener listener, final Response.ErrorListener errListener,
						final CancelListener cancelListener, final String jsonBody) {
		super(method, url, gson, clazz, listener, errListener, cancelListener, jsonBody);
	}

	/**
	 * Creates a new UrlGsonRequest instance, with less parameters for
	 * backwards compatibility.
	 *
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param gson The gson instance to parse the response
	 * @param clazz The {@link Type} of the class T
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 * @param jsonBody The contents of the json to be sent in the request's body.
	 */
	public UrlGsonRequest(final int method, final String url, final Gson gson, final Type clazz,
						final Response.Listener listener, final Response.ErrorListener errListener,
						final String jsonBody) {
		super(method, url, gson, clazz, listener, errListener, jsonBody);
	}

	@Override
	public String getBodyContentType() {
		return "application/x-www-form-urlencoded; charset=" + this.getParamsEncoding();
	}
}
