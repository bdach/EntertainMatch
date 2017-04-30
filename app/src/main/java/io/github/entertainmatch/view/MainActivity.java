package io.github.entertainmatch.view;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebasePersonController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.view.main.PollFragment;
import io.github.entertainmatch.view.poll.CreatePollActivity;
import rx.Observable;

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

    public final static String NEW_POLL_RESPONSE_KEY = "new_poll";

    /**
     * The fragment used to display the list of ongoing polls.
     */
    private PollFragment pollFragment;

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

    static {
        FirebaseController.init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        pollFragment = new PollFragment();
        setContentFragment(pollFragment);
        populateUserData(FacebookUsers.getCurrentUser(this));

        // grab user from firebase (initially to fetch polls)
        FirebasePersonController.getUser(FacebookUsers.getCurrentUser(this).facebookId)
            .subscribe(firebasePerson -> {
                if (firebasePerson == null) return;

                for (Observable<FirebasePoll> poll : FirebasePollController.getPollsForUser(firebasePerson)) {
                    poll.subscribe(firebasePoll -> {
                        pollFragment.addPoll(new Poll(
                                firebasePoll.getName(),
                                new VoteCategoryStage(),
                                null)); // decide if we should store every person or just ids
                    });
                }
            });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_POLL_REQUEST:
                //handleNewPoll(resultCode, data);
                break;
        }
    }

    private void handleNewPoll(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Poll poll = data.getParcelableExtra(NEW_POLL_RESPONSE_KEY);
        pollFragment.addPoll(poll);
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
