package com.monits.agilefant.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monits.agilefant.model.Project;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.User;
import com.monits.agilefant.network.request.UrlGsonRequest;
import com.monits.volleyrequests.network.request.GsonRequest;

import javax.inject.Inject;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class ProjectServiceImpl implements ProjectService {

	private final AgilefantService agilefantService;

	private static final String PROJECT_LEAF_STORIES_ACTION = "%1$s/ajax/projectLeafStories.action";
	private static final String OBJECT_ID = "objectId";

	private static final String ASSIGNEE_IDS = "assigneeIds";
	private static final String ASSIGNEES_CHANGED = "assigneesChanged";
	private static final String PROJECT_ID = "projectId";
	private static final String PROJECT_DATA = "%1$s/ajax/projectData.action?projectId=%2$d";

	@SuppressFBWarnings(value = "MISSING_FIELD_IN_TO_STRING", justification = "We do not want this in the toString")
	final private Gson gson;

	/**
	 * @param agilefantService Injected via constructor by Dagger
	 * @param gson Injected via constructor by Dagger
	 */

	@Inject
	public ProjectServiceImpl(final AgilefantService agilefantService, final Gson gson) {
		this.agilefantService = agilefantService;
		this.gson = gson;
	}

	@Override
	public void getProjectData(final long projectId, final Listener<Project> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, PROJECT_DATA,
				agilefantService.getHost(), projectId);

		final GsonRequest<Project> request = new GsonRequest<>(Request.Method.GET, url,
				gson, Project.class, listener, error, null);

		agilefantService.addRequest(request);
	}

	@Override
	public void getProjectLeafStories(final long projectId,
			final Listener<List<Story>> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, PROJECT_LEAF_STORIES_ACTION, agilefantService.getHost());

		final Type listType = new TypeToken<ArrayList<Story>>() { }.getType();

		final UrlGsonRequest<List<Story>> request = new UrlGsonRequest<List<Story>>(
				Request.Method.POST, url, gson, listType, listener, error, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {

				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					appendURLEncodedParam(body, OBJECT_ID, String.valueOf(projectId), paramsEncoding);
				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		agilefantService.addRequest(request);
	}

	@Override
	public void updateProject(final Project project, final Listener<Project> listener, final ErrorListener error) {
		final String url = String.format(Locale.US, "%1$s/ajax/storeProject.action", agilefantService.getHost());

		final UrlGsonRequest<Project> request = new UrlGsonRequest<Project>(
				Request.Method.POST, url, gson, Project.class, listener, error, null) {

			@Override
			public byte[] getBody() throws AuthFailureError {
				// We have to do this, because Agilefant's API is very ugly. and serializes parameters in a weird way.
				final StringBuilder body = new StringBuilder();
				final String paramsEncoding = getParamsEncoding();
				try {
					for (final User user : project.getAssignees()) {
						appendURLEncodedParam(body, ASSIGNEE_IDS,
								String.valueOf(user.getId()), paramsEncoding);
					}

					appendURLEncodedParam(body, ASSIGNEES_CHANGED,
							String.valueOf(true), paramsEncoding);
					appendURLEncodedParam(body, PROJECT_ID,
							String.valueOf(project.getId()), paramsEncoding);

				} catch (final UnsupportedEncodingException e) {
					throw new AssertionError(e);
				}

				return body.toString().getBytes(Charset.forName(paramsEncoding));
			}
		};

		agilefantService.addRequest(request);
	}

	/**
	 * An auxiliary method in order to append parameters to the given post body's StringBuilder,
	 * for some special requests.
	 *
	 * @param sb the StringBuilder to build post body.
	 * @param key the key of the param.
	 * @param value the value for that key.
	 * @param encoding the encoding.
	 * @return the same StringBuilder that was given, with the given params appended.
	 *
	 * @throws UnsupportedEncodingException If the given encoding is not supported
	 */

	private StringBuilder appendURLEncodedParam(final StringBuilder sb, final String key, final String value,
												final String encoding) throws UnsupportedEncodingException {

		sb.append(URLEncoder.encode(key, encoding));
		sb.append('=');
		sb.append(URLEncoder.encode(value, encoding));
		sb.append('&');

		return sb;
	}
}
