package com.monits.agilefant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
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
import com.monits.agilefant.model.Story;
import com.monits.agilefant.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IterationActivity extends BaseToolbaredActivity implements ViewPager.OnPageChangeListener {

	public static final String ITERATION = "ITERATION";

	private Iteration iteration;
	private LinearLayout optionsContainer;
	private FloatingActionButton addFAB;
	private FloatingActionButton addStoryFAB;
	private FloatingActionButton addTaskFAB;

	private TextView storyFABLabel;
	private TextView taskFABLabel;
	private final static long DELAY = 90;

	@Bind(R.id.pager)
	/* default */ ViewPager viewPager;


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iteration);

		ButterKnife.bind(this);


		final Bundle bundle = getIntent().getExtras();
		iteration = (Iteration) bundle.getSerializable(ITERATION);

		final List<Fragment> fragments = new ArrayList<>();

		final ArrayList<Story> storiesArray = new ArrayList<>();
		storiesArray.addAll(iteration.getStories());

		final ArrayList<Task> tasksWithoutStory = new ArrayList<>();
		tasksWithoutStory.addAll(iteration.getTasksWithoutStory());

		fragments.add(IterationDetailsFragment.newInstance(iteration));
		fragments.add(StoriesFragment.newInstance(storiesArray, iteration));
		fragments.add(TaskWithoutStoryFragment.newInstance(tasksWithoutStory));
		fragments.add(IterationBurndownFragment.newInstance(iteration.getId()));

		viewPager.setAdapter(new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), fragments));

		setUpTabLayout(viewPager);

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
				replaceFragment(CreateStoryFragment.newInstance(iteration.getId()));
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
			final Animation rotateIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_rotate_in);
			addFAB.setAnimation(rotateIn);

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
			final Animation rotateOut =
					AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_rotate_out);
			addFAB.setAnimation(rotateOut);

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
		final int pageNum = viewPager.getCurrentItem();
		menu.findItem(R.id.action_search).setVisible(pageNum > 0 && pageNum < viewPager.getAdapter().getCount() - 1);

		return true;
	}
}

