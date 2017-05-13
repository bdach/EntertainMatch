package io.github.entertainmatch.view.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.view.LoginActivity;
import io.github.entertainmatch.view.MainActivity;
import io.github.entertainmatch.view.date.VoteDateActivity;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity used for voting on an event category.
 */
public class VoteCategoryActivity extends AppCompatActivity
        implements CategoryFragment.OnCategorySelectedListener {

    /**
     * The key used to store and fetch the {@link ArrayList} of {@link Category} items to display.
     */
    public static final String CATEGORIES_KEY = "categoriesTemplates";

    /**
     * The {@link Toolbar} displayed in the window as the action bar.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /**
     * The {@link CoordinatorLayout} of the activity. Used to display {@link Snackbar}s.
     */
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout layout;
    /**
     * The {@link CategoryFragment} containing the list of available {@link Category} items.
     */
    private CategoryFragment fragment;
    /**
     * Identifier of current poll
     */
    private String pollId;
    /**
     * Subscription object used to notify view about poll changes.
     */
    private Subscription subscription;
    private List<String> participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_category);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateCategoryList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.default_vote_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_participants) {
            View menuItemView = findViewById(R.id.show_participants);
            PopupMenu popupMenu = new PopupMenu(this, menuItemView);
            Menu menu = popupMenu.getMenu();
            for (String participantId : participants) {
                menu.add(participantId);
            }
            popupMenu.show();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches the list of categoriesTemplates from the calling {@link Intent} and displays it in the activity as a
     * {@link CategoryFragment}.
     */
    private void populateCategoryList() {
        Intent intent = getIntent();
        VoteCategoryData data = intent.getParcelableExtra(CATEGORIES_KEY);

        ArrayList<Category> categories = data.getCategories();
        pollId = data.getPollId();

        subscription = FirebasePollController.getPoll(pollId).subscribe(this::subscribeCallback);

        fragment = CategoryFragment.newInstance(categories);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vote_category_content, fragment)
                .commit();
    }

    private void subscribeCallback(FirebasePoll poll) {
        participants = poll.getParticipants();
        if (poll.getStage().equals(VoteCategoryStage.class.toString())) {
            fragment.updateCategories(poll.getVoteCounts());
        } else {
            Snackbar.make(layout, R.string.voting_finished, BaseTransientBottomBar.LENGTH_LONG)
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        onBackPressed();
                    }
                })
                .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (getParent() == null)
            startActivity(new Intent(VoteCategoryActivity.this, LoginActivity.class));

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (subscription != null)
            subscription.unsubscribe();
    }

    /**
     * Called when a {@link Category} is selected in the {@link CategoryFragment}.
     *
     * @param item The {@link Category} selected by the user.
     */
    @Override
    public void onCategorySelected(Category item) {
        if (item == null) {
            return;
        }
        fragment.registerVote(item);
        FirebasePoll poll = FirebasePollController.polls.get(pollId);
        poll.voteCategory(item);

        Snackbar.make(layout, R.string.vote_category_snackbar, BaseTransientBottomBar.LENGTH_LONG)
                .show();
    }
}
