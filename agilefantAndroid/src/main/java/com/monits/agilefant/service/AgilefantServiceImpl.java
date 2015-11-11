package com.monits.agilefant.service;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequeueAfterRequestDecorator;
import com.android.volley.RequeuePolicy;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.monits.volleyrequests.network.request.RfcCompliantListenableRequest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class AgilefantServiceImpl implements AgilefantService {

	private static final String HTTP_REXEG = "^https?://.*$";
	private static final String HTTP = "http://";
	private String host;
	private static final String LOGIN_URL = "%1$s/j_spring_security_check";
	private static final String PASSWORD = "j_password";
	private static final String USERNAME = "j_username";
	private static final String LOGIN_OK = "/index.jsp";

	private final ReloginRequeuePolicy reloginRequeuePolicy = new ReloginRequeuePolicy();

	private final SharedPreferences sharedPreferences;

	private final RequestQueue requestQueue;

	/**
	 * @param sharedPreferences Injected via constructor by Dagger
	 * @param requestQueue Injected via constructor by Dagger
	 */
	@Inject
	public AgilefantServiceImpl(final SharedPreferences sharedPreferences, final RequestQueue requestQueue) {
		this.sharedPreferences = sharedPreferences;
		this.requestQueue = requestQueue;
	}

	@Override
	public void login(final String userName, final String password, final Listener<String> listener,
					final ErrorListener error) {
		final String url = String.format(Locale.US, LOGIN_URL, getHost());

		final ErrorListener reqError = new ErrorListener() {

			@Override
			public void onErrorResponse(final VolleyError volleyError) {
				final NetworkResponse response = volleyError.networkResponse;
				if (response != null && response.statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
					final String location = response.headers.get("Location");
					if (location != null && location.split(";")[0].equals(getHost() + LOGIN_OK)) {
						listener.onResponse(location);
					} else {
						error.onErrorResponse(volleyError);
					}
				} else {
					error.onErrorResponse(volleyError);
				}
			}
		};

		final RfcCompliantListenableRequest<String> request =
				new RfcCompliantListenableRequest<String>(Method.POST, url, listener, reqError) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						final Map<String, String> params = new HashMap<>();

						params.put(USERNAME, userName);
						params.put(PASSWORD, password);

						return params;
					}

					@Override
					protected Response<String> parseNetworkResponse(final NetworkResponse response) {
						// We're looking for a redirect and a header, we don't care about a 2xx response.
						return null;
					}
				};

		requestQueue.add(request);
	}

	@Override
	public void setDomain(final String domain) {
		if (domain.matches(HTTP_REXEG)) {
			this.host = domain;
		} else {
			this.host = HTTP + domain;
		}
	}

	@Override
	public String getHost() {
		if (host == null) {
			setDomain(sharedPreferences.getString(UserService.DOMAIN_KEY, null));
		}
		return host;
	}

	@Override
	public void addRequest(final Request<?> request) {
		requestQueue.add(RequeueAfterRequestDecorator.wrap(request, reloginRequeuePolicy));
	}

	@Override
	public String toString() {
		return "AgilefantServiceImpl{host=" + host + '}';
	}

	/**
	 * Implementation of {@link RequeuePolicy} which will attempt to re-login before requeueing.
	 *
	 * @author gmuniz
	 *
	 */
	private final class ReloginRequeuePolicy implements RequeuePolicy {

		private ReloginRequeuePolicy() {
		}

		@Override
		public boolean shouldRequeue(final NetworkResponse networkResponse) {
			return networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_MOVED_TEMP;
		}

		@Override
		public void executeBeforeRequeueing(final Listener<?> successCallback, final ErrorListener errorCallback) {

			login(
					sharedPreferences.getString(UserService.USER_NAME_KEY, null),
					sharedPreferences.getString(UserService.PASSWORD_KEY, null),
					new Listener<String>() {

						@Override
						public void onResponse(final String arg0) {
							// We don't really care about response, we only need to be notified.
							successCallback.onResponse(null);
						}
					},
					errorCallback
			);
		}
	}
}