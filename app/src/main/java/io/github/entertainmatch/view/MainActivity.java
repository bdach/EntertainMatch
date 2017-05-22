package io.github.entertainmatch.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
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

    /**
     * Identification number for the request to start a new poll.
     */
    public final static int NEW_POLL_REQUEST = 1;

    /**
     * Key indicating that a poll has been created from within another activity.
     */
    public final static String NEW_POLL_RESPONSE_KEY = "new_poll";
    public final static int FINISHED_POLL = 2;
    public final static String FINISHED_POLL_ID_KEY = "poll_id";

    /**
     * Name of the fragment back stack used to display settings.
     */
    private final static String SETTINGS_STACK_NAME = "settings_stack";

    /**
     * The fragment used to display the settings menu.
     */
    private SettingsFragment settingsFragment;

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

    MainActivityPagerAdapter pagerAdapter;

    private List<Subscription> subscriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        FirebaseController.init();

        settingsFragment = new SettingsFragment();

        pagerAdapter = new MainActivityPagerAdapter(
                getSupportFragmentManager(),
                Arrays.asList(new PollFragment(), new EventFragment()),
                subscriptions
        );

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (!isMyServiceRunning(NotificationService.class))
            startService(new Intent(this, NotificationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListExt.forEach(subscriptions, Subscription::unsubscribe);
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
        fab.show();
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
        // TODO: handle options here
        int id = item.getItemId();

        if (id == R.id.action_settings && getSupportFragmentManager().getBackStackEntryCount() == 0) {
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
            return true;
        }
        else if (id == R.id.logout) {
            FacebookUsers.removeCurrentUser(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
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

    private void checkPollStatus(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String pollId = data.getStringExtra(FINISHED_POLL_ID_KEY);
        FirebasePollController.getPollOnce(pollId)
                .map(poll -> new Poll(poll, FacebookUsers.getCurrentUser(this).facebookId))
                .subscribe(this::viewPollProgress);
    }

    private void handleNewPoll(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        PollStub pollStub = data.getParcelableExtra(NEW_POLL_RESPONSE_KEY);

        // add poll to firebase, once added it should vote views of all users involved
        // and show notifications to them
        FirebasePollController.addPoll(FacebookUsers.getCurrentUser(this).facebookId, pollStub);
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
    public void onEventClicked(FirebasePoll item) {
        Intent intent = new Intent(this, VoteResultActivity.class);
        intent.putExtra(VoteResultStage.POLL_ID_KEY, item.getPollId());
        startActivity(intent);
    }
}
