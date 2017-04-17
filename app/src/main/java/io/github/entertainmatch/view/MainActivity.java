package io.github.entertainmatch.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebaseController;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.view.main.PollFragment;
import io.github.entertainmatch.view.poll.CreatePollActivity;

/**
 * The main screen of the application. Displays lists of events and polls.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PollFragment.OnPollSelectedListener {

    /**
     * The fragment used to display the list of ongoing polls.
     */
    private PollFragment pollFragment;

    /**
     * The view toolbar, containing the menu toggle and options bar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * The floating action button used to add new polls.
     */
    @BindView(R.id.fab)
    FloatingActionButton fab;

    /**
     * Drawer layout used to navigate between current polls and upcoming events.
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    /**
     * The navigation view within the drawer layout.
     */
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    static {
        FirebaseController.init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        pollFragment = new PollFragment();
        setContentFragment(pollFragment);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: handle options here
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // TODO: handle content switch here
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_ongoing_polls:

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Navigates to the appropriate view upon the user's request to view a {@link Poll} status.
     *
     * @param poll The selected {@link Poll}.
     */
    @Override
    public void onPollSelected(Poll poll) {
        PollStage stage = poll.getPollStage();
        Intent intent = stage.getViewStageIntent(this);
        startActivity(intent);
    }

    /**
     * Navigates to the {@link CreatePollActivity} activity in order to create a new poll.
     *
     * @param view The view that started the navigation interaction.
     */
    public void createNewPoll(View view) {
        Intent intent = new Intent(MainActivity.this, CreatePollActivity.class);
        MainActivity.this.startActivity(intent);
    }

    /**
     * Sets the contents of the main content {@link android.widget.FrameLayout} to the supplied {@link Fragment}.
     *
     * @param fragment The {@link Fragment} to set as the contents of the {@link android.widget.FrameLayout}.
     */
    private void setContentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

}
