package io.github.entertainmatch.view.result;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookInitializer;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.*;
import io.github.entertainmatch.model.VoteResultStage;
import io.github.entertainmatch.view.LoginActivity;
import io.github.entertainmatch.view.MainActivity;
import io.github.entertainmatch.view.ParticipantList;

public class VoteResultActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;

    private ImageView eventImage;
    private TextView eventName;
    private TextView eventPlace;
    private TextView eventDate;

    //private Event event;
    //private EventDate date;

    private Button buttonYes;
    private Button buttonNo;

    private String pollId;
    private String facebookId;

    private RelativeLayout footer;

    private ParticipantList participantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        setContentView(R.layout.activity_vote_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        eventImage = (ImageView) findViewById(R.id.event_image);
        eventName = (TextView) findViewById(R.id.result_event_name);
        eventPlace = (TextView) findViewById(R.id.result_event_place);
        eventDate = (TextView) findViewById(R.id.result_event_date);

        buttonYes = (Button) findViewById(R.id.result_yes);
        buttonNo = (Button) findViewById(R.id.result_no);

        footer = (RelativeLayout) findViewById(R.id.result_footer);

        //Intent intent = getIntent();
        //event = intent.getParcelableExtra(EVENT_KEY);
        //date = intent.getParcelableExtra(DATE_KEY);

        facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();
        pollId = getIntent().getStringExtra(VoteResultStage.POLL_ID_KEY);
        FirebasePollController.getPollOnce(pollId).subscribe(poll -> {

            footer.setVisibility(poll.votingComplete(facebookId) ? View.GONE : View.VISIBLE);
            participantList = new ParticipantList(this, poll);
            participantList.fetchNames();

            FirebaseLocationsController.getLocationOnce(poll.getChosenLocationId()).subscribe(location -> {
                eventPlace.setText(location.getPlace());
            });

            FirebaseEventDateController.getEventSingle(poll.getChosenCategory(), poll.getVictoriousEvent(), poll.getChosenLocationId()).subscribe(eventDate -> {
                String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                        .format(eventDate.getDate());
                this.eventDate.setText(date);
            });

            FirebaseEventController.getEventSingle(
                    poll.getChosenCategory(),
                    poll.getVictoriousEvent()
                            .substring(poll.getChosenCategory().length())
            ).subscribe(firebaseEvent -> {
                Picasso.with(this)
                        .load(firebaseEvent.getDrawableUri())
                        .into(eventImage);
                eventName.setText(firebaseEvent.getTitle());
            });
        });

        buttonYes.setOnClickListener(v -> {
            buttonListener(true);
        });

        buttonNo.setOnClickListener(v -> {
            buttonListener(false);
        });

        //bindData();
    }

    private void buttonListener(boolean going) {
        FirebasePollController.getPoll(pollId).subscribe(poll -> {
            Map<String, Boolean> goingMap = poll.getGoing();
            if (goingMap != null && goingMap.containsKey(facebookId))
                return;

            FirebasePollController.setIsGoing(pollId, facebookId, going);
            FirebaseUserController.removePollForUser(pollId, facebookId);
            FirebaseUserEventController.addEventForUser(pollId, facebookId);
            notifyPollEnded();
            Snackbar.make(coordinatorLayout, going ? getString(R.string.going_positive) : getString(R.string.going_negative), Snackbar.LENGTH_LONG)
                    .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);

                    onBackPressed();
                }
            }).show();
        });
    }

    private void notifyPollEnded() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.POLL_FINISHED_ID_KEY, pollId);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.default_vote_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_participants && participantList != null) {
            AlertDialog dialog = participantList.getDialog();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FacebookInitializer.init(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backCleanup();
    }

    public void backCleanup() {
        if (getCallingActivity() != null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        // no actions

        finish();
    }
}
