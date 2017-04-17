package io.github.entertainmatch.view.event;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.MovieEvent;

/**
 * An activity representing a single Event detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * event details are presented side-by-side with a list of items
 * in a {@link EventListActivity}.
 */
public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            MovieEvent event = getIntent().getParcelableExtra(EventDetailFragment.EVENTS_KEY);
            arguments.putParcelable(EventDetailFragment.EVENTS_KEY, event);
            EventDetailFragment fragment = new EventDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_detail_container, fragment)
                    .commit();
            setFabListener(Uri.parse(event.getYoutubeTrailerUrl()));
        }

    }

    private void setFabListener(final Uri youtubeTrailerUrl) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.youtube_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, youtubeTrailerUrl);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, EventListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
