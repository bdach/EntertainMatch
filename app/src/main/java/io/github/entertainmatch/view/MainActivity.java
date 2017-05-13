package io.github.entertainmatch.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.FirebaseUserController;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.notifications.NotificationService;
import io.github.entertainmatch.utils.PollStageFactory;
import io.github.entertainmatch.view.main.PollFragment;
import io.github.entertainmatch.view.poll.CreatePollActivity;
import rx.Subscription;

/**
 * The main screen of the application. Displays lists of events and polls.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PollFragment.OnPollSelectedListener {

    /**
     * Identification number for the request to start a new poll.
     */
    public final static int NEW_POLL_REQUEST = 1;

    /**
     * Key indicating that a poll has been created from within another activity.
     */
    public final static String NEW_POLL_RESPONSE_KEY = "new_poll";

    /**
     * Name of the fragment back stack used to display settings.
     */
    private final static String SETTINGS_STACK_NAME = "settings_stack";

    /**
     * The fragment used to display the list of ongoing polls.
     */
    private PollFragment pollFragment;
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

    /**
     * Drawer layout used to navigate between current polls and upcoming events.
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    /**
     * The navigation view within the drawer layout.
     */
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private List<Subscription> subscriptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        FirebaseController.init();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        settingsFragment = new SettingsFragment();
        pollFragment = new PollFragment();
        setContentFragment(pollFragment);
        populateUserData(FacebookUsers.getCurrentUser(this));

        // grab user from firebase (initially to fetch polls)
        subscriptions.add(FirebaseUserController.getPollsForUser(FacebookUsers.getCurrentUser(this).facebookId)
            .subscribe(firebasePoll -> {
                pollFragment.updatePoll(new Poll(
                    firebasePoll.getName(),
                    PollStageFactory.get(firebasePoll.getStage(), firebasePoll.getPollId()),
                    firebasePoll.getParticipants(),
                    firebasePoll.getPollId()));
            })
        );

        if (!isMyServiceRunning(NotificationService.class))
            startService(new Intent(this, NotificationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.forEach(Subscription::unsubscribe);
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

    private void populateUserData(Person currentUser) {
        LinearLayout headerView = (LinearLayout) navigationView.getHeaderView(0);
        TextView userNameView = (TextView) headerView.findViewById(R.id.user_name);
        ImageView avatarView = (ImageView) headerView.findViewById(R.id.user_avatar);
        userNameView.setText(currentUser.getName());
        Picasso.with(this)
                .load(currentUser.getProfilePictureUrl())
                .fit()
                .into(avatarView);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            fab.show();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            super.onBackPressed();
        }
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
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.content_frame, settingsFragment)
                    .addToBackStack(SETTINGS_STACK_NAME)
                    .commit();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            fab.hide();
            return true;
        }
        else if (id == R.id.logout) {
            FacebookUsers.removeCurrentUser(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // TODO: handle content switch here
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_ongoing_polls:

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Navigates to the appropriate view upon the user's request to view a {@link Poll} status.
     *
     * @param poll The selected {@link Poll}.
     */
    @Override
    public void onPollSelected(Poll poll) {
        PollStage stage = poll.getPollStage();
        Intent intent = stage.getViewStageIntent(this);
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_POLL_REQUEST:
                handleNewPoll(resultCode, data);
                break;
        }
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

    /**
     * Sets the contents of the main content {@link android.widget.FrameLayout} to the supplied {@link Fragment}.
     *
     * @param fragment The {@link Fragment} to set as the contents of the {@link android.widget.FrameLayout}.
     */
    private void setContentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
