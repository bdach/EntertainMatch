package io.github.entertainmatch.view.poll;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.facebook.GraphRequest;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.facebook.FriendsProvider;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.model.Person;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.PollStub;
import io.github.entertainmatch.view.LocationChecker;
import io.github.entertainmatch.view.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Activity used to create new polls.
 */
public class CreatePollActivity extends AppCompatActivity implements PersonFragment.OnPersonSelectedListener {

    public static final int RESULT_NO_CITY_SET = 2;
    /**
     * Fragment used to display list of people.
     */
    private PersonFragment personFragment;

    /**
     * Coordinator layout. Used to display snackbars.
     */
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    /**
     * View toolbar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Layout for the poll name input. Responsible for displaying errors.
     */
    @BindView(R.id.new_poll_input_layout)
    TextInputLayout inputLayout;

    /**
     * Text view for user input. Contains the new poll name.
     */
    @BindView(R.id.new_poll_name)
    TextView pollName;

    private final HashSet<Person> selectedPeople = new HashSet<>();
    private LocationChecker locationChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        personFragment = new PersonFragment();
        GraphRequest friendsListRequest = FriendsProvider.getFriendsList((array, response) -> {
            ArrayList<Person> people = FriendsProvider.arrayFromJson(array);
            personFragment.setItems(people);
        });
        friendsListRequest.executeAsync();

        locationChecker = new LocationChecker(
                this,
                coordinatorLayout,
                this::removeAddOption,
                this::goToSettings
        );

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.person_list, personFragment)
                .commit();
    }

    private void goToSettings() {
        setResult(RESULT_NO_CITY_SET);
        finish();
    }

    private void removeAddOption() {
        View addPollMenuItem = findViewById(R.id.action_create_poll);
        addPollMenuItem.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_poll_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_poll:
                inputLayout.setError("");
                if (!dataCorrect()) break;
                setResult(RESULT_OK, constructPoll());
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent constructPoll() {
        PollStub poll = new PollStub(
                pollName.getText().toString(),
                selectedPeople.toArray(new Person[selectedPeople.size()])
        );

        Intent intent = new Intent();
        intent.putExtra(MainActivity.NEW_POLL_RESPONSE_KEY, poll);
        return intent;
    }

    public boolean dataCorrect() {
        boolean textLength = pollName.getText().length() > 0;
        boolean participants = selectedPeople.size() > 0;
        if (!textLength) {
            inputLayout.setError(getString(R.string.create_poll_empty_name_error));
        }
        if (!participants) {
            Snackbar.make(coordinatorLayout, R.string.create_poll_no_people_selected, Snackbar.LENGTH_LONG)
                    .show();
        }
        return textLength && participants;
    }

    @Override
    public void onPersonToggled(Person item, boolean checked) {
        if (checked) {
            selectedPeople.add(item);
        } else {
            selectedPeople.remove(item);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}
