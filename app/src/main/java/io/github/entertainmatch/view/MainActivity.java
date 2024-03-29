package io.github.entertainmatch.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookInitializer;
import io.github.entertainmatch.facebook.FacebookModule;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseCategoriesTemplatesController;
import io.github.entertainmatch.firebase.FirebaseEventController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteResultStage;
import io.github.entertainmatch.notifications.NotificationService;
import io.github.entertainmatch.utils.ListExt;
import io.github.entertainmatch.view.main.EventFragment;
import io.github.entertainmatch.view.main.PollFragment;
import io.github.entertainmatch.view.main.MainActivityPagerAdapter;
import io.github.entertainmatch.view.poll.CreatePollActivity;
import io.github.entertainmatch.view.result.VoteResultActivity;
import rx.Subscription;

/**
 * The main screen of the application. Displays lists of events and polls.
 */
public class MainActivity extends AppCompatActivity
        implements PollFragment.OnPollSelectedListener,
        EventFragment.OnEventSelectedListener {
    @Inject
    FacebookUsers FacebookUsers;

    @Inject
    FacebookInitializer facebookInitializer;

    /**
     * Identification number for the request to start a new poll.
     */
    public final static int NEW_POLL_REQUEST = 1;

    /**
     * Key indicating that a poll has been created from within another activity.
     */
    public final static String NEW_POLL_RESPONSE_KEY = "new_poll";
    public final static int FINISHED_POLL = 2;
    public final static String STAGE_FINISHED_POLL_ID_KEY = "stage_finished_poll_id";
    public final static String POLL_FINISHED_ID_KEY = "finished_poll_id";

    /**
     * Name of the fragment backCleanup stack used to display settings.
     */
    private final static String SETTINGS_STACK_NAME = "settings_stack";

    /**
     * The fragment used to display the settings menu.
     */
    SettingsFragment settingsFragment;

    private MainActivityPagerAdapter pagerAdapter;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    /**
     * The view toolbar, containing the menu toggle and options bar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * The floating action button used to add new polls.
     */
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private List<Subscription> subscriptions = new ArrayList<>();
    private LocationChecker locationChecker;
    private boolean settingsRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerApplication.getApp().getFacebookComponent().inject(this);

        FirebaseEventController.init();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(STAGE_FINISHED_POLL_ID_KEY)) {
            checkPollStatus(RESULT_OK, intent);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        settingsFragment = new SettingsFragment();

        pagerAdapter = new MainActivityPagerAdapter(
                getSupportFragmentManager(),
                Arrays.asList(new PollFragment(), new EventFragment()),
                subscriptions
        );

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setFabVisibility(position);
            }

            private void setFabVisibility(int position) {
                if (position == 0 && !settingsFragment.isVisible()) {
                    fab.show();
                    locationChecker.recheckCities();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onPageSelected(int position) {
                setFabVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        locationChecker = new LocationChecker(
                this,
                coordinatorLayout,
                () -> fab.hide(),
                this::navigateToSettings
        );

        // init for service
        FacebookUsers.getCurrentUser(this);
        if (!isMyServiceRunning(NotificationService.class))
            startService(new Intent(this, NotificationService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsRequested) {
            navigateToSettings();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListExt.forEach(subscriptions, Subscription::unsubscribe);

        if (locationChecker != null)
            locationChecker.unsubscribe();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            fab.show();
        }
        locationChecker.recheckCities();
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            navigateToSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToSettings() {
        fab.hide();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .hide(pagerAdapter.getItem(0))
            .hide(pagerAdapter.getItem(1))
            .add(R.id.content_frame, settingsFragment)
            .addToBackStack(SETTINGS_STACK_NAME)
            .commit();
    }

    /**
     * Navigates to the appropriate view upon the user's request to view a {@link Poll} status.
     *
     * @param poll The selected {@link Poll}.
     */
    @Override
    public void viewPollProgress(Poll poll) {
        PollStage stage = poll.getPollStage();
        Intent intent = stage.getViewStageIntent(this);
        MainActivity.this.startActivityForResult(intent, FINISHED_POLL);
    }

    @Override
    public AppCompatActivity getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_POLL_REQUEST:
                handleNewPoll(resultCode, data);
                break;
            case FINISHED_POLL:
                checkPollStatus(resultCode, data);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        facebookInitializer.initNonStatic(getApplicationContext());
    }

    private void checkPollStatus(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Bundle extras = data.getExtras();

        if (extras.containsKey(STAGE_FINISHED_POLL_ID_KEY)) {
            String pollId = data.getStringExtra(STAGE_FINISHED_POLL_ID_KEY);
            FirebasePollController.getPollOnce(pollId)
                    .map(poll -> new Poll(poll, FacebookUsers.getCurrentUser(this).facebookId))
                    .subscribe(this::viewPollProgress);
        }

        if (extras.containsKey(POLL_FINISHED_ID_KEY)) {
            String pollId = data.getStringExtra(POLL_FINISHED_ID_KEY);
            PollFragment fragment = (PollFragment) pagerAdapter.getItem(0);
            fragment.deletePoll(pollId);
        }
    }

    private void handleNewPoll(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                PollStub pollStub = data.getParcelableExtra(NEW_POLL_RESPONSE_KEY);

                // add poll to firebase, once added it should vote views of all users involved
                // and show notifications to them
                FirebasePollController.addPoll(
                        FacebookUsers.getCurrentUser(this).facebookId, pollStub
                );
                break;
            case CreatePollActivity.RESULT_NO_CITY_SET:
                settingsRequested = true;
                break;
        }
    }

    /**
     * Navigates to the {@link CreatePollActivity} activity in order to create a new poll.
     *
     * @param view The view that started the navigation interaction.
     */
    public void createNewPoll(View view) {
        Intent intent = new Intent(MainActivity.this, CreatePollActivity.class);
        MainActivity.this.startActivityForResult(intent, NEW_POLL_REQUEST);
    }

    @Override
    public void onEventClicked(FirebaseCompletedPoll item) {
        Intent intent = new Intent(this, VoteResultActivity.class);
        intent.putExtra(VoteResultStage.POLL_ID_KEY, item.getId());
        startActivity(intent);
    }
}
