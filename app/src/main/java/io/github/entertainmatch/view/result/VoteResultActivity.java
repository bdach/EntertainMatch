package io.github.entertainmatch.view.result;

import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.firebase.FirebaseEventDateController;
import io.github.entertainmatch.firebase.FirebaseLocationsController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.VoteResultStage;

public class VoteResultActivity extends AppCompatActivity {

    public static final String EVENT_KEY = "event";
    public static final String DATE_KEY = "date";

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

        //Intent intent = getIntent();
        //event = intent.getParcelableExtra(EVENT_KEY);
        //date = intent.getParcelableExtra(DATE_KEY);

        pollId = getIntent().getStringExtra(VoteResultStage.POLL_ID_KEY);
        FirebasePoll poll = FirebasePollController.polls.get(pollId);
        FirebaseLocationsController.getLocationOnce(poll.getChosenLocationId()).subscribe(location -> {
            eventPlace.setText(location.getPlace());
        });

        FirebaseEventDateController.getEventSingle(poll.getChosenCategory(), poll.getVictoriousEvent(), poll.getChosenLocationId()).subscribe(eventDate -> {
            String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                    .format(eventDate.getDate());
            this.eventDate.setText(date);
        });

        FirebaseController.getEventSingle(
                poll.getChosenCategory(),
                poll.getVictoriousEvent()
                        .substring(poll.getChosenCategory().length())
        ).subscribe(firebaseEvent -> {
            Picasso.with(this)
                    .load(firebaseEvent.getDrawableUri())
                    .into(eventImage);
            eventName.setText(firebaseEvent.getTitle());
        });

        facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
        buttonYes.setOnClickListener(v -> {
            buttonListener(true);
        });

        buttonNo.setOnClickListener(v -> {
            buttonListener(false);
        });

        //bindData();
    }

    private void buttonListener(boolean going) {
        Map<String, Boolean> goingMap = FirebasePollController.polls.get(pollId).getGoing();
        if (goingMap != null && goingMap.containsKey(facebookId))
            return;

        FirebasePollController.setIsGoing(pollId, facebookId, going);
        Snackbar.make(coordinatorLayout, going ? getString(R.string.going_positive) : getString(R.string.going_negative), Snackbar.LENGTH_LONG)
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                finish();
            }
        }).show();
    }

    private void bindData() {
//        Picasso.with(this)
//                .load(event.getDrawableUri())
//                .into(eventImage);

//        eventName.setText(event.getTitle());
//        eventPlace.setText(date.getPlace());
//        String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
//                .format(this.date.getDate());
//        eventDate.setText(date);
    }

}
