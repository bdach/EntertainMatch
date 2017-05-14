package io.github.entertainmatch.view.date;

import android.app.AlertDialog;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseEventDateController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.EventDate;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.VoteResultStage;
import io.github.entertainmatch.utils.ListExt;
import io.github.entertainmatch.view.ParticipantList;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * The activity responsible for voting on a date for the selected event.
 */
public class VoteDateActivity extends AppCompatActivity implements DateFragment.OnDateSelectedListener {

    /**
     * The activity toolbar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private String pollId;
    private DateFragment dateFragment;
    private ParticipantList participantList;

    /**
     * Keeps track whether next stage is ready.
     * On such scenario we finish this activity showing Snackbar.
     */
    private Subscription changesSubscription;

    @Override
    protected void onStart() {
        super.onStart();

        changesSubscription = FirebasePollController.getPoll(pollId).subscribe(poll -> {
            participantList = new ParticipantList(this, poll);
            participantList.fetchNames();
            if (poll.getStage().equals(VoteResultStage.class.toString())) {
                Snackbar.make(coordinatorLayout, R.string.results_stage_message, Snackbar.LENGTH_LONG)
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        finish();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (changesSubscription != null)
            changesSubscription.unsubscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_date);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pollId = getIntent().getStringExtra(PollStage.POLL_ID_KEY);

        dateFragment = DateFragment.newInstance(pollId);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.date_list, dateFragment)
            .commit();
    }

    /**
     * Opens a Google Maps URI when a location for one of the {@link EventDate}s is requested.
     * @param date The selected {@link EventDate} object.
     */
    @Override
    public void onDateSelected(EventDate date) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, date.getGoogleMapsUri());
        startActivity(mapIntent);
    }

    /**
     * Toggles selection state of an item
     * @param date Pressed event date
     * @param status Current checkbox status
     */
    @Override
    public void onDateToggle(EventDate date, boolean status) {
        date.setSelected(status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vote_date_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_confirm_vote) {
            FirebasePoll poll = FirebasePollController.polls.get(pollId);
            String facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();
            dateFragment.disallowEdition();

            if (poll.getEventDatesStatus().get("voted").get(facebookId)) {
                Snackbar.make(coordinatorLayout, R.string.already_voted, Snackbar.LENGTH_LONG).show();
                return true;
            }

            List<EventDate> dates = dateFragment.getDates();
            poll.chooseDate(
                ListExt.map(dates, EventDate::getLocationId),
                ListExt.map(dates, EventDate::isSelected)
            );

            Snackbar.make(coordinatorLayout, R.string.date_notification, Snackbar.LENGTH_LONG).show();
            return true;
        }
        if (item.getItemId() == R.id.show_participants && participantList != null) {
            AlertDialog dialog = participantList.getDialog();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
