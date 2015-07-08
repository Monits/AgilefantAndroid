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

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.monits.volleyrequests.network.request.JsonRfcCompliantListenableRequest;

public class StringJsonRfcCompliantRequest extends JsonRfcCompliantListenableRequest<String> {

	/**
	 * Constructor
	 * @param method The http method
	 * @param url The url
	 * @param listener The success listener
	 * @param errListener The error listener
	 * @param jsonBody The json body
	 */
	public StringJsonRfcCompliantRequest(final int method, final String url,
			final Listener<String> listener, final ErrorListener errListener, final String jsonBody) {
		super(method, url, listener, errListener, jsonBody);
	}

	@Override
	protected Response<String> parseNetworkResponse(final NetworkResponse response) {
		try {
			final String str = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
		} catch (final UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}
}
