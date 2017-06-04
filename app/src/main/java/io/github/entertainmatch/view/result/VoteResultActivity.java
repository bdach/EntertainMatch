package io.github.entertainmatch.view.result;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.github.entertainmatch.DaggerApplication;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookInitializer;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.*;
import io.github.entertainmatch.model.VoteResultStage;
import io.github.entertainmatch.utils.CalendarUtils;
import io.github.entertainmatch.view.LoginActivity;
import io.github.entertainmatch.view.MainActivity;
import io.github.entertainmatch.view.ParticipantList;

public class VoteResultActivity extends AppCompatActivity {
    @Inject
    FacebookUsers FacebookUsers;

    private static final int ASK_CALENDAR = 2137;

    private CoordinatorLayout coordinatorLayout;

    private ImageView eventImage;
    private TextView eventName;
    private TextView eventPlace;
    private TextView eventDate;

    private Button buttonYes;
    private Button buttonNo;

    private String pollId;
    private String facebookId;

    private RelativeLayout footer;

    private ParticipantList participantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerApplication.getApp().getFacebookComponent().inject(this);

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

        facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();
        pollId = getIntent().getStringExtra(VoteResultStage.POLL_ID_KEY);
        FirebaseCompletedPollController.getCompletedPollOnce(pollId).subscribe(poll -> {
            footer.setVisibility(poll.votingComplete(facebookId) ? View.GONE : View.VISIBLE);

            participantList = new ParticipantList(this, poll);
            participantList.fetchNames();

            eventPlace.setText(poll.getLocation().getPlace());
            String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                    .format(poll.getEventDate().getDate());
            eventDate.setText(date);

            Picasso.with(this)
                    .load(poll.getEvent().getDrawableUri())
                    .into(eventImage);
            eventName.setText(poll.getEvent().getTitle());
        });

        buttonYes.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_CALENDAR}, ASK_CALENDAR);
                return;
            }

            addPollToCalendar();
            buttonListener(true);
        });

        buttonNo.setOnClickListener(v -> {
            buttonListener(false);
        });
    }

    /**
     * Adds poll to a calendar after handling stuff related to permissions
     */
    private void addPollToCalendar() {
        FirebaseCompletedPollController.getCompletedPollOnce(pollId).subscribe(poll -> {
            CalendarUtils.addEventToCalendar(this, poll);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPollToCalendar();
                    buttonListener(true);
                } else {
                    Snackbar.make(coordinatorLayout, R.string.no_calendar_permissions, Snackbar.LENGTH_LONG)
                            .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            buttonListener(true);
                        }
                    }).show();
                }
            }
        }
    }

    private void buttonListener(boolean going) {
        FirebaseCompletedPollController.getCompletedPoll(pollId).subscribe(poll -> {
            Map<String, Boolean> goingMap = poll.getGoing();
            if (goingMap != null && goingMap.containsKey(facebookId))
                return;

            FirebaseCompletedPollController.setIsGoing(pollId, facebookId, going);
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
            participantList.showDialog();
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
