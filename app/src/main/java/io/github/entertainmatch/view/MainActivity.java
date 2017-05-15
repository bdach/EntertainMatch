package io.github.entertainmatch.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebaseUserController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.utils.PollStageFactory;
import io.github.entertainmatch.view.main.EventFragment;
import io.github.entertainmatch.view.main.PollFragment;
import io.github.entertainmatch.view.main.dummy.DummyContent;
import io.github.entertainmatch.view.main.dummy.MainActivityPagerAdapter;
import io.github.entertainmatch.view.poll.CreatePollActivity;
import rx.Observable;

import java.util.Arrays;

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

    /**
     * Name of the fragment back stack used to display settings.
     */
    private final static String SETTINGS_STACK_NAME = "settings_stack";

    /**
     * The fragment used to display the list of ongoing polls.
     */
    private PollFragment pollFragment;
    /**
     * The fragment used to display the list of upcoming events.
     */
    private EventFragment eventFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        FirebaseController.init();

        pollFragment = new PollFragment();
        eventFragment = new EventFragment();
        settingsFragment = new SettingsFragment();

        pagerAdapter = new MainActivityPagerAdapter(
                getSupportFragmentManager(),
                Arrays.asList(pollFragment, eventFragment)
        );
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        String facebookId = FacebookUsers.getCurrentUser(this).facebookId;
        // grab user from firebase (initially to fetch polls)
        FirebaseUserController.getUserOnce(facebookId)
            .subscribe(firebasePerson -> {
                if (firebasePerson == null) return;

                for (Observable<FirebasePoll> poll : FirebasePollController.getPollsOnceForUser(firebasePerson)) {
                    poll.subscribe(firebasePoll -> {
                        FirebasePollController.polls.put(firebasePoll.getPollId(), firebasePoll);
                        pollFragment.addPoll(new Poll(
                                firebasePoll.getName(),
                                PollStageFactory.get(firebasePoll.getStage(), firebasePoll.getPollId()),
                                firebasePoll.getParticipants(), // decide if we should store every person or just ids
                                firebasePoll.getPollId(),
                                firebasePoll.votingComplete(facebookId)));

                        FirebasePollController
                            .getPoll(firebasePoll.getPollId())
                            .subscribe(updatedPoll -> {
                                // TODO: poll vote strategy, we should probably keep poll id in Poll object too
                                Log.d("PollUpdate", "Updated!");

                                FirebasePollController.polls.get(updatedPoll.getPollId()).update(updatedPoll);
                            });
                    });
                }
            });
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
                    .hide(pollFragment)
                    .hide(eventFragment)
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
        startActivity(intent);
    }

    @Override
    public void deletePoll(Poll poll) {
        FirebaseUserController.removePoll(poll.getPollId(), FacebookUsers.getCurrentUser(this).facebookId);
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
        }
    }

    private void handleNewPoll(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        PollStub pollStub = data.getParcelableExtra(NEW_POLL_RESPONSE_KEY);

        // add poll to firebase, once added it should vote views of all users involved
        // and show notifications to them
        Poll poll = FirebasePollController.addPoll(FacebookUsers.getCurrentUser(this).facebookId, pollStub);
        String pollId = poll.getPollId();

        FirebasePollController.getPollOnce(pollId).subscribe(x -> {
            FirebasePollController.polls.put(x.getPollId(), x);
            pollFragment.addPoll(new Poll(
                x.getName(),
                new VoteCategoryStage(x.getPollId()),
                poll.getMembers(),
                x.getPollId(),
                false));
        });

        // subscribe for poll changes
        FirebasePollController.getPoll(pollId).subscribe(updatedPoll -> {
            Log.d("PollUpdate", "Updated!");
            FirebasePollController.polls.get(updatedPoll.getPollId()).update(updatedPoll);
        });
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
    protected void onResume() {
        super.onResume();

        pollFragment.updatePolls();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        // no-op
    }
}
