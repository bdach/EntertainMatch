package io.github.entertainmatch.view.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.entertainmatch.R;
import io.github.entertainmatch.model.EventDate;
import io.github.entertainmatch.model.MovieEvent;

import java.text.DateFormat;
import java.util.Locale;

public class VoteResultActivity extends AppCompatActivity {

    public static final String EVENT_KEY = "event";
    public static final String DATE_KEY = "date";

    private ImageView eventImage;
    private TextView eventName;
    private TextView eventPlace;
    private TextView eventDate;

    private MovieEvent event;
    private EventDate date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventImage = (ImageView) findViewById(R.id.event_image);
        eventName = (TextView) findViewById(R.id.result_event_name);
        eventPlace = (TextView) findViewById(R.id.result_event_place);
        eventDate = (TextView) findViewById(R.id.result_event_date);

        Intent intent = getIntent();
        event = intent.getParcelableExtra(EVENT_KEY);
        date = intent.getParcelableExtra(DATE_KEY);

        bindData();
    }

    private void bindData() {
        Picasso.with(this)
                .load(event.getDrawableUri())
                .into(eventImage);

        eventName.setText(event.getTitle());
        eventPlace.setText(date.getPlace());
        String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.ENGLISH)
                .format(this.date.getDate());
        eventDate.setText(date);
    }

}
