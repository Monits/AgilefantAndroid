package com.monits.agilefant.adapter.recyclerviewholders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.monits.agilefant.AgilefantApplication;
import com.monits.agilefant.R;
import com.monits.agilefant.fragment.userchooser.UserChooserFragment;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.User;
import com.monits.agilefant.service.IterationService;
import com.monits.agilefant.service.MetricsService;
import com.monits.agilefant.util.HoursUtils;
import com.monits.agilefant.util.IterationUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoryItemViewHolder extends WorkItemViewHolder<Story> {

	@Bind(R.id.column_name)
	TextView name;

	@Bind(R.id.column_state)
	TextView state;

	@Bind(R.id.column_responsibles)
	TextView responsibles;

	@Bind(R.id.column_effort_left)
	TextView effortLeft;

	@Bind(R.id.column_original_estimate)
	TextView originalEstimate;

	@Bind(R.id.column_spent_effort)
	TextView spendEffort;

	@Inject
	MetricsService metricsService;

	@Inject
	IterationService iterationService;

	private final FragmentActivity context;
	private Story story;

	/**
	 * Constructor
	 *
	 * @param itemView view's Item
	 * @param context The context
	 */
	public StoryItemViewHolder(final View itemView, final FragmentActivity context) {
		super(itemView);
		this.context = context;
		ButterKnife.bind(this, itemView);
		AgilefantApplication.getObjectGraph().inject(this);
	}

	@Override
	public void onBindView(final Story item) {
		this.story = item;

		name.setText(item.getName());

		state.setTextColor(context.getResources().getColor(IterationUtils.getStateTextColor(item.getState())));
		state.setText(IterationUtils.getStateName(item.getState()));
		state.setBackgroundResource(IterationUtils.getStateBackground(item.getState()));

		responsibles.setText(IterationUtils.getResposiblesDisplay(item.getResponsibles()));
		effortLeft.setText(HoursUtils.convertMinutesToHours(item.getEffortLeft()));
		originalEstimate.setText(HoursUtils.convertMinutesToHours(item.getOriginalEstimate()));
		spendEffort.setText(HoursUtils.convertMinutesToHours(item.getEffortSpent()));
	}

	@OnClick(R.id.column_state)
	void changeState() {
		final DialogInterface.OnClickListener onStoryStateSelectedListener = getOnDialogClickListener(story);

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_state_title);
		builder.setSingleChoiceItems(
				StateKey.getDisplayStates(), story.getState().ordinal(), onStoryStateSelectedListener);
		builder.show();
	}

	@OnClick(R.id.column_responsibles)
	void changeResponsible() {
		context.getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, getUserChooserFragment(story))
			.addToBackStack(null)
			.commit();
	}

	private DialogInterface.OnClickListener getOnDialogClickListener(final Story object) {
		return new DialogInterface.OnClickListener() {

			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				final StateKey state = StateKey.values()[which];

				if (state == StateKey.DONE) {
					showConfirmTaskDoneDialog(state, object);
				} else {
					executeUpdateStoryTask(state, object, false);
				}

				dialog.dismiss();
			}
		};
	}

	private void showConfirmTaskDoneDialog(final StateKey state, final Story object) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_tasks_to_done_title)
			.setMessage(R.string.dialog_tasks_to_done_msg)
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					executeUpdateStoryTask(state, object, true);
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					executeUpdateStoryTask(state, object, false);
				}
			});

		builder.show();
	}

	/**
	 * Configures and executes the {@link MetricsService#changeStoryState changeStoryState}.
	 *
	 * @param state the state key
	 * @param story the story
	 * @param allTasksToDone all task done flag
	 */
	void executeUpdateStoryTask(final StateKey state, final Story story, final boolean allTasksToDone) {
		metricsService.changeStoryState(
			state, story, allTasksToDone,
			new Response.Listener<Story>() {

				@Override
				public void onResponse(final Story arg0) {
					Toast.makeText(context, R.string.feedback_successfully_updated_story, Toast.LENGTH_SHORT).show();
				}
			},
			new Response.ErrorListener() {

				@Override
				public void onErrorResponse(final VolleyError arg0) {
					Toast.makeText(context, R.string.feedback_failed_update_story, Toast.LENGTH_SHORT).show();
				}
			});
	}

	private UserChooserFragment getUserChooserFragment(final Story object) {
		return UserChooserFragment.newInstance(
			object.getResponsibles(),
			new UserChooserFragment.OnUsersSubmittedListener() {

				@Override
				public void onSubmitUsers(final List<User> users) {
					metricsService.changeStoryResponsibles(
						users,
						object,
						new Response.Listener<Story>() {
							@Override
							public void onResponse(final Story project) {
								Toast.makeText(
									context, R.string.feedback_success_updated_project, Toast.LENGTH_SHORT).show();
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(final VolleyError arg0) {
								Toast.makeText(
									context, R.string.feedback_failed_update_project, Toast.LENGTH_SHORT).show();
							}
						});
				}
			});
	}

	@Override
	public String toString() {
		return "StoryItemViewHolder { story=" + story + ", context=" + context + '}';
	}
}
