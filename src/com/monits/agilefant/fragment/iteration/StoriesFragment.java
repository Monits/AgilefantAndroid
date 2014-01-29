package com.monits.agilefant.fragment.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.fragment.RoboFragment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.dialog.PromptDialogFragment;
import com.monits.agilefant.dialog.PromptDialogFragment.PromptDialogListener;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment;
import com.monits.agilefant.fragment.user_chooser.UserChooserFragment.OnUsersSubmittedListener;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.MetricsService;

public class StoriesFragment extends RoboFragment implements Observer {

	@Inject
	private MetricsService metricsService;

	private static final String STORIES = "STORIES";
	private List<Story> stories;

	private ExpandableListView storiesListView;
	private StoriesAdapter storiesAdapter;

	public static StoriesFragment newInstance(final ArrayList<Story> stories){
		final Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(STORIES, stories);

		final StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);

		return storiesFragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Bundle arguments = getArguments();
		this.stories= arguments.getParcelableArrayList(STORIES);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		storiesListView = (ExpandableListView)rootView.findViewById(R.id.stories);
		storiesListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		storiesAdapter = new StoriesAdapter(rootView.getContext(), stories);
		storiesAdapter.setOnChildActionListener(new AdapterViewActionListener<Task>() {

			@Override
			public void onAction(final View view, final Task object) {
				object.addObserver(StoriesFragment.this);

				switch (view.getId()) {
				case R.id.task_effort_left:

					// Agilefant's tasks that are already done, can't have it's EL changed.
					if (!object.getState().equals(StateKey.DONE)) {

						final PromptDialogFragment dialogFragment = PromptDialogFragment.newInstance(
								R.string.dialog_effortleft_title,
								String.valueOf((float) object.getEffortLeft() / 60), // Made this way to avoid strings added in utils method.
								InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);

						dialogFragment.setPromptDialogListener(new PromptDialogListener() {

							@Override
							public void onAccept(final String inputValue) {
								double el = 0;
								if (!inputValue.trim().equals("")) {
									el = Double.valueOf(inputValue.trim());
								}

								metricsService.changeEffortLeft(
										el, object,
										new Listener<Task>() {

											@Override
											public void onResponse(final Task task) {

												Toast.makeText(
														getActivity(), R.string.feedback_succesfully_updated_effort_left, Toast.LENGTH_SHORT).show();
											}
										},
										new ErrorListener() {

											@Override
											public void onErrorResponse(final VolleyError arg0) {

												Toast.makeText(
														getActivity(), R.string.feedback_failed_update_effort_left, Toast.LENGTH_SHORT).show();
											}
										});
							}
						});

						dialogFragment.show(getFragmentManager(), "effortLeftDialog");
					}

					break;

				case R.id.task_spend_effort:

					final FragmentTransaction transaction = getParentFragment().getFragmentManager().beginTransaction();
					transaction.add(R.id.container, SpentEffortFragment.newInstance(object));
					transaction.addToBackStack(null);
					transaction.commit();

					break;

				case R.id.task_state:

					final OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog, final int which) {

							metricsService.taskChangeState(
									StateKey.values()[which],
									object,
									new Listener<Task>() {

										@Override
										public void onResponse(final Task arg0) {
											Toast.makeText(
													getActivity(), R.string.feedback_successfully_updated_state, Toast.LENGTH_SHORT).show();
										};
									},
									new ErrorListener() {

										@Override
										public void onErrorResponse(final VolleyError arg0) {
											Toast.makeText(
													getActivity(), R.string.feedback_failed_update_state, Toast.LENGTH_SHORT).show();
										};
									});

							dialog.dismiss();
						}
					};

					final AlertDialog.Builder builder = new Builder(getActivity());
					builder.setTitle(R.string.dialog_state_title);
					builder.setSingleChoiceItems(
							StateKey.getDisplayStates(), object.getState().ordinal(), onChoiceSelectedListener);
					builder.show();

					break;
				}
			}
		});

		storiesAdapter.setOnGroupActionListener(new AdapterViewActionListener<Story>() {

			@Override
			public void onAction(final View view, final Story object) {
				object.addObserver(StoriesFragment.this);

				switch (view.getId()) {
					case R.id.storie_state:
						final OnClickListener onStoryStateSelectedListener = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(final DialogInterface dialog, final int which) {
								final StateKey state = StateKey.values()[which];

								if (state == StateKey.DONE) {
									final AlertDialog.Builder builder = new Builder(getActivity());
									builder.setTitle(R.string.dialog_tasks_to_done_title)
									.setMessage(R.string.dialog_tasks_to_done_msg)
									.setPositiveButton(android.R.string.yes, new OnClickListener() {

										@Override
										public void onClick(final DialogInterface dialog, final int which) {
											executeUpdateStoryTask(state, object, true);
										}
									})
									.setNegativeButton(android.R.string.no, new OnClickListener() {

										@Override
										public void onClick(final DialogInterface dialog, final int which) {
											executeUpdateStoryTask(state, object, false);
										}
									});

									builder.show();
								} else {
									executeUpdateStoryTask(state, object, false);
								}

								dialog.dismiss();
							}
						};

						final AlertDialog.Builder builder = new Builder(getActivity());
						builder.setTitle(R.string.dialog_state_title);
						builder.setSingleChoiceItems(
								StateKey.getDisplayStates(), object.getState().ordinal(), onStoryStateSelectedListener);
						builder.show();

						break;

					case R.id.storie_responsibles:

						final Fragment fragment = UserChooserFragment.newInstance(
								object.getResponsibles(),
								new OnUsersSubmittedListener() {

									@Override
									public void onSubmitUsers(final List<User> users) {

										final FragmentActivity activity = getActivity();
										metricsService.changeStoryResponsibles(
												users,
												object,
												new Listener<Story>() {

													@Override
													public void onResponse(final Story project) {
														Toast.makeText(activity, R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
													}
												},
												new ErrorListener() {

													@Override
													public void onErrorResponse(final VolleyError arg0) {
														Toast.makeText(activity, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
													}
												});
									}
								});

						getActivity().getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, fragment)
							.addToBackStack(null)
							.commit();

						break;
					default:
						break;
				}
			}
		});

		storiesListView.setAdapter(storiesAdapter);

		return rootView;
	}

	/**
	 * Configures and executes the {@link UpdateStoryTask}.
	 *
	 * @param state
	 * @param story
	 * @param allTasksToDone
	 */
	private void executeUpdateStoryTask(final StateKey state, final Story story, final boolean allTasksToDone) {

		metricsService.changeStoryState(
				state,
				story,
				allTasksToDone,
				new Listener<Story>() {

					@Override
					public void onResponse(final Story arg0) {
						Toast.makeText(
								getActivity(), R.string.feedback_successfully_updated_story, Toast.LENGTH_SHORT).show();
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(final VolleyError arg0) {
						Toast.makeText(
								getActivity(), R.string.feedback_failed_update_story, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public void update(final Observable observable, final Object data) {

		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}
}
