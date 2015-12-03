package com.monits.agilefant.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.monits.volleyrequests.network.request.RfcCompliantListenableRequest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by lgnanni on 21/10/15.
 */
public class UrlGsonRequest<T> extends RfcCompliantListenableRequest {

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "No need to use in the toString")
	private final Gson gson;
	private final Type clazz;

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
	 */
	public UrlGsonRequest(final int method, final String url, final Gson gson, final Type clazz,
						final Response.Listener listener, final Response.ErrorListener errListener,
						final CancelListener cancelListener) {
		super(method, url, listener, errListener, cancelListener);
		this.gson = gson;
		this.clazz = clazz;
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
	 */
	public UrlGsonRequest(final int method, final String url, final Gson gson, final Type clazz,
						final Response.Listener listener, final Response.ErrorListener errListener) {
		super(method, url, listener, errListener);
		this.gson = gson;
		this.clazz = clazz;
	}

	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success((T) gson.fromJson(json, clazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (final JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}
