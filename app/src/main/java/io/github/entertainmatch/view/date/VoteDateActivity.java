package io.github.entertainmatch.view.date;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.EventDate;

import java.util.ArrayList;

public class VoteDateActivity extends AppCompatActivity implements DateFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DateFragment dateFragment = DateFragment.newInstance(new ArrayList<>(EventDate.mockData()));

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.date_list, dateFragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(EventDate date) {
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
