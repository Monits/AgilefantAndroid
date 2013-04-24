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
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.google.inject.Inject;
import com.monits.agilefant.R;
import com.monits.agilefant.adapter.StoriesAdapter;
import com.monits.agilefant.dialog.PromptDialogFragment;
import com.monits.agilefant.dialog.PromptDialogFragment.PromptDialogListener;
import com.monits.agilefant.listeners.AdapterViewActionListener;
import com.monits.agilefant.listeners.TaskCallback;
import com.monits.agilefant.model.StateKey;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.task.UpdateEffortLeftTask;
import com.monits.agilefant.task.UpdateStateTask;

public class StoriesFragment extends RoboFragment implements Observer {

	@Inject
	private UpdateEffortLeftTask updateEffortLeftTask;

	@Inject
	private UpdateStateTask updateStateTask;

	private static final String STORIES = "STORIES";

	private List<Story> stories;

	private ExpandableListView storiesListView;

	private StoriesAdapter storiesAdapter;

	public static StoriesFragment newInstance(ArrayList<Story> stories){
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(STORIES, stories);

		StoriesFragment storiesFragment = new StoriesFragment();
		storiesFragment.setArguments(bundle);

		return storiesFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		this.stories= arguments.getParcelableArrayList(STORIES);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stories, container, false);

		storiesListView = (ExpandableListView)rootView.findViewById(R.id.stories);
		storiesListView.setEmptyView(rootView.findViewById(R.id.stories_empty_view));
		storiesAdapter = new StoriesAdapter(rootView.getContext(), stories);
		storiesAdapter.setOnChildActionListener(new AdapterViewActionListener<Task>() {

			@Override
			public void onAction(final View view, final Task object) {
				object.addObserver(StoriesFragment.this);

				switch (view.getId()) {
				case R.id.task_effort_left:

					PromptDialogFragment dialogFragment = PromptDialogFragment.newInstance(
							R.string.dialog_effortleft_title,
							String.valueOf(object.getEffortLeft() / 60), // Made this way to avoid strings added in utils method.
							InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);

					dialogFragment.setPromptDialogListener(new PromptDialogListener() {

						@Override
						public void onAccept(String inputValue) {
							updateEffortLeftTask.configure(object.getId(), Long.parseLong(inputValue), new TaskCallback<Task>() {

								@Override
								public void onError() {
									Toast.makeText(
											getActivity(), "Failed to update Effort Left.", Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onSuccess(Task response) {
									Toast.makeText(
											getActivity(), "Successfully updated Effort Left.", Toast.LENGTH_SHORT).show();
									object.updateValues(response);
								}
							});

							updateEffortLeftTask.execute();

						}
					});

					dialogFragment.show(getFragmentManager(), "effortLeftDialog");

					break;

				case R.id.task_spend_effort:

					FragmentTransaction transaction = getParentFragment().getFragmentManager().beginTransaction();
					transaction.add(R.id.container, SpentEffortFragment.newInstance(object));
					transaction.addToBackStack(null);
					transaction.commit();

					break;

				case R.id.task_state:

					OnClickListener onChoiceSelectedListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							updateStateTask.configure(
									StateKey.values()[which],
									object.getId(),
									new TaskCallback<Task>() {

										@Override
										public void onSuccess(Task response) {
											object.updateValues(response);
										}

										@Override
										public void onError() {
											Toast.makeText(
													getActivity(), "Failed to update the state", Toast.LENGTH_SHORT).show();
										}
									});

							updateStateTask.execute();

							dialog.dismiss();
						}
					};

					AlertDialog.Builder builder = new Builder(getActivity());
					builder.setTitle(R.string.dialog_state_title);
					builder.setSingleChoiceItems(
							StateKey.getDisplayStates(), object.getState().ordinal(), onChoiceSelectedListener);
					builder.show();

					break;
				}
			}
		});

		storiesListView.setAdapter(storiesAdapter);

		return rootView;
	}

	@Override
	public void update(Observable observable, Object data) {
		BitmapAjaxCallback.clearCache();

		if (isVisible()) {
			storiesAdapter.notifyDataSetChanged();
			observable.deleteObserver(this);
		}
	}
}
