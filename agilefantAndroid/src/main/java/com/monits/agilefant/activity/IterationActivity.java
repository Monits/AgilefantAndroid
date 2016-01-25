package com.monits.agilefant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monits.agilefant.R;
import com.monits.agilefant.adapter.ScreenSlidePagerAdapter;
import com.monits.agilefant.fragment.backlog.story.CreateStoryFragment;
import com.monits.agilefant.fragment.backlog.task.CreateTaskWithoutStory;
import com.monits.agilefant.fragment.iteration.IterationBurndownFragment;
import com.monits.agilefant.fragment.iteration.IterationDetailsFragment;
import com.monits.agilefant.fragment.iteration.StoriesFragment;
import com.monits.agilefant.fragment.iteration.TaskWithoutStoryFragment;
import com.monits.agilefant.model.Iteration;
import com.monits.agilefant.model.PageSelect;
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;
import com.monits.agilefant.model.backlog.BacklogType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IterationActivity extends BaseToolbaredActivity implements ViewPager.OnPageChangeListener {

	private static final String ITERATION = "ITERATION";
	private static final String FOCUS_PAGE = "FOCUS_PAGE";
	private static final String FOCUS_STORY = "FOCUS_STORY";

	private Iteration iteration;
	private LinearLayout optionsContainer;
	private FloatingActionButton addFAB;
	private FloatingActionButton addStoryFAB;
	private FloatingActionButton addTaskFAB;

	private TextView storyFABLabel;
	private TextView taskFABLabel;

	private static final int DURATION = 100;
	private static final float DEGREE = 135;
	private static final long DELAY = 90;

	@Bind(R.id.pager)
	/* default */ ViewPager viewPager;

	/**
	 *
	 * This factory method returns an intent of this class with it's necessary extra values
	 *
	 * @param context A Context of the application package implementing this class
	 * @param iteration A Iteration object for being sent to the returned intent
	 * @param focusPage A number of page want to focus
	 * @param storyFocusId A id of story want to focus
	 * @return An intent that contains sent data as extra values
	 */
	public static Intent getIntent(@NonNull final Context context, @NonNull final Iteration iteration,
								@NonNull final PageSelect focusPage, @NonNull final int storyFocusId) {
		final Intent intent = new Intent(context, IterationActivity.class);
		intent.putExtra(ITERATION, iteration);
		intent.putExtra(FOCUS_PAGE, getPageNumber(focusPage));
		intent.putExtra(FOCUS_STORY, storyFocusId);
		return intent;
	}

	private static int getPageNumber(final PageSelect focusPage) {
		switch (focusPage) {
		case STORIES:
			return 1;
		case TASKWITHOUTSTORY:
			return 2;
		default:
			return 0;
		}
	}

	/**
	 *
	 * This factory method returns an intent of this class with it's necessary extra values
	 *
	 * @param context A Context of the application package implementing this class
	 * @param iteration A Iteration object for being sent to the returned intent
	 * @return An intent that contains sent data as extra values
	 */
	public static Intent getIntent(@NonNull final Context context, @NonNull final Iteration iteration) {
		final Intent intent = new Intent(context, IterationActivity.class);
		intent.putExtra(ITERATION, iteration);
		return intent;
	}



	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);

		ButterKnife.bind(this);


		final Bundle bundle = getIntent().getExtras();
		iteration = (Iteration) bundle.getSerializable(ITERATION);

		final int focusPage = bundle.getInt(FOCUS_PAGE, 0);

		final List<Fragment> fragments = new ArrayList<>();

		final ArrayList<Story> storiesArray = new ArrayList<>();
		storiesArray.addAll(iteration.getStories());

		final ArrayList<Task> tasksWithoutStory = new ArrayList<>();
		tasksWithoutStory.addAll(iteration.getTasksWithoutStory());

		fragments.add(IterationDetailsFragment.newInstance(iteration));

		final int focusStory = bundle.getInt(FOCUS_STORY, 0);
		if (focusStory == 0) {
			fragments.add(StoriesFragment.newInstance(storiesArray, iteration));
		} else {
			fragments.add(StoriesFragment.newInstance(storiesArray, iteration, focusStory));
		}

		fragments.add(TaskWithoutStoryFragment.newInstance(tasksWithoutStory));
		fragments.add(IterationBurndownFragment.newInstance(iteration.getId()));

		viewPager.setAdapter(new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), fragments));
		viewPager.addOnPageChangeListener(this);
		setUpTabLayout(viewPager);

		viewPager.setCurrentItem(focusPage); //Change page focus

		final ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
		final View fabContainer = getLayoutInflater().inflate(R.layout.fab_iteration_menu_layout, content);
		initFABs(fabContainer);
	}

	private void initFABs(final View fabContainer) {
		addFAB = (FloatingActionButton) fabContainer.findViewById(R.id.iteration_add_fab);
		addStoryFAB =
				(FloatingActionButton) fabContainer.findViewById(R.id.iteration_fab_new_story);
		addTaskFAB =
				(FloatingActionButton) fabContainer.findViewById(R.id.iteration_fab_new_task);
		optionsContainer = (LinearLayout) fabContainer.findViewById(R.id.fab_buttons_container);

		storyFABLabel = (TextView) fabContainer.findViewById(R.id.story_fab_label);
		taskFABLabel = (TextView) fabContainer.findViewById(R.id.task_fab_label);

		View.OnClickListener animationClick = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				animationFABMenu();
			}
		};

		optionsContainer.setOnClickListener(animationClick);
		addFAB.setOnClickListener(animationClick);

		final View.OnClickListener fabMenuStoryItemsClick = new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				animationFABMenu();
				replaceFragment(CreateStoryFragment.newInstance(BacklogType.ITERATION, iteration.getId()));
			}
		};

		addStoryFAB.setOnClickListener(fabMenuStoryItemsClick);
		storyFABLabel.setOnClickListener(fabMenuStoryItemsClick);


		final View.OnClickListener fabMenuTaskItemsClick = new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				animationFABMenu();
				replaceFragment(CreateTaskWithoutStory.newInstance(iteration.getId()));
			}
		};
		addTaskFAB.setOnClickListener(fabMenuTaskItemsClick);
		taskFABLabel.setOnClickListener(fabMenuTaskItemsClick);
	}


	private void animationFABMenu() { //Open animation

		if (optionsContainer.getVisibility() == View.INVISIBLE) {
			ViewCompat.animate(addFAB).rotation(DEGREE)
					.setDuration(DURATION).start();

			optionsContainer.setVisibility(View.VISIBLE);
			final Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_menu_fade_in);

			optionsContainer.setAnimation(fadeIn);

			//Clear animation for correct repeat
			addStoryFAB.clearAnimation();
			storyFABLabel.clearAnimation();
			addTaskFAB.clearAnimation();
			taskFABLabel.clearAnimation();

			setAnimationFabItems(storyFABLabel, 0);
			setAnimationFabItems(addStoryFAB, 0);

			setAnimationFabItems(taskFABLabel, DELAY);
			setAnimationFabItems(addTaskFAB, DELAY);

		} else { //Close fab menu animation
			ViewCompat.animate(addFAB).rotation(0f)
					.setDuration(DURATION).start();

			final Animation fadeOut =
					AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_menu_fade_out) ;
			optionsContainer.setAnimation(fadeOut);
			optionsContainer.setVisibility(View.INVISIBLE);
		}
	}

	private void replaceFragment(final Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment)
				.addToBackStack(null)
				.commit();
	}

	private void setAnimationFabItems(final View v, final long delay) {
		final Animation anim =
				AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_item_scale);
		anim.setStartOffset(delay);
		v.setAnimation(anim);
	}

	@Override
	public String toString() {
		return "IterationActivity{" + "iteration=" + iteration + '}';
	}

	@Override
	public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
		//We only need to implement the onPageSelected method
	}

	@Override
	public void onPageSelected(final int position) {
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onPageScrollStateChanged(final int state) {
		//We only need to implement the onPageSelected method
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final MenuItem menuItem = menu.findItem(R.id.action_search);
		menuItem.setVisible(false);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		searchView.setMaxWidth(getMaxWidthScreen());
		return true;
	}
}

