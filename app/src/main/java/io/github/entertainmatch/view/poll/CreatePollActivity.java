package io.github.entertainmatch.view.poll;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;

/**
 * Activity used to create new polls.
 */
public class CreatePollActivity extends AppCompatActivity implements PersonFragment.OnPersonSelectedListener {

    /**
     * Fragment used to display list of people.
     */
    private PersonFragment personFragment;
    /**
     * View toolbar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personFragment = PersonFragment.newInstance(Person.mockData(), this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.person_list, personFragment)
                .commit();
    }

    @Override
    public void onPersonSelected(Person item) {

    }

    @Override
    public Context getContext() {
        return this;
    }
}
