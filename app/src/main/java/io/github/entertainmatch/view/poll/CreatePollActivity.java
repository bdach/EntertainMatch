package io.github.entertainmatch.view.poll;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;

public class CreatePollActivity extends AppCompatActivity implements PersonFragment.OnListFragmentInteractionListener {

    private PersonFragment personFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personFragment = PersonFragment.newInstance(Person.mockData(), this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.person_list, personFragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(Person item) {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
