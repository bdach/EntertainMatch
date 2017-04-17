package io.github.entertainmatch.view.date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.EventDate;

import java.util.ArrayList;

/**
 * The activity responsible for voting on a date for the selected event.
 */
public class VoteDateActivity extends AppCompatActivity implements DateFragment.OnDateSelectedListener {

    /**
     * The activity toolbar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_date);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DateFragment dateFragment = DateFragment.newInstance(new ArrayList<>(EventDate.mockData()));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.vote_date_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
