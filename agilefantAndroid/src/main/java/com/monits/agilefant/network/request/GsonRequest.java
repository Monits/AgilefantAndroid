/*
* Copyright 2010 - 2014 Monits
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.monits.agilefant.network.request;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.monits.volleyrequests.network.request.RfcCompliantListenableRequest;

/**
 * A request that returns a JSON to be decoded by GSON.
 * 
 * Based on the original work by Volley's author ficusk: https://gist.github.com/ficusk/5474673
 */
public class GsonRequest<T> extends RfcCompliantListenableRequest<T> {

	private final Gson gson;
	private final Type clazz;

	/**
	 * Creates a new GsonRequest instance
	 * 
	 * @param method The request method, {@see Method}
	 * @param url The url to be requested.
	 * @param gson the json parser
	 * @param clazz the request type
	 * @param listener The listener for success.
	 * @param errListener The listener for errors.
	 */
	public GsonRequest(final int method, final String url, final Gson gson,
			final Type clazz, final Listener<T> listener,
			final ErrorListener errListener) {
		super(method, url, listener, errListener);

		this.gson = gson;
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success((T) gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (final JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public String toString() {
		return new StringBuilder("GsonRequest [gson: ").append(gson)
				.append(", clazz: ").append(clazz)
				.append(']')
				.toString();
	}
}