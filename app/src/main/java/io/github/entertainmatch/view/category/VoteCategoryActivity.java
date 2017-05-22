package io.github.entertainmatch.view.category;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseCategoriesTemplatesController;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.view.LoginActivity;
import io.github.entertainmatch.view.NavigationHelper;
import io.github.entertainmatch.view.ParticipantList;
import rx.Subscription;

import java.util.ArrayList;

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
    private ParticipantList participantList;

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
        if (item.getItemId() == R.id.show_participants && participantList != null) {
            AlertDialog dialog = participantList.getDialog();
            dialog.show();
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
        String facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();
        Intent intent = getIntent();
        VoteCategoryData data = intent.getParcelableExtra(CATEGORIES_KEY);
        pollId = data.getPollId();

        ArrayList<Category> categories = new ArrayList<>();

        fragment = CategoryFragment.newInstance(categories);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vote_category_content, fragment)
                .commit();

        subscription = FirebasePollController.getPoll(pollId).subscribe(this::subscribeCallback);

        FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                for (String category : poll.getVoteCounts().keySet()) {
                    Category template = FirebaseCategoriesTemplatesController.getCachedMap().get(category);
                    categories.add(new Category(
                            template.getName(),
                            poll.getVoteCounts().get(template.getId()),
                            poll.getVotedFor().get(facebookId).equals(template.getId()),
                            template.getImageUrl(),
                            template.getId()
                        ));
                }
                fragment.categoriesChanged();
        });
    }

    private void subscribeCallback(FirebasePoll poll) {
        participantList = new ParticipantList(this, poll);
        participantList.fetchNames();
        if (poll.getStage().equals(VoteCategoryStage.class.toString())) {
            fragment.updateCategories(poll.getVoteCounts(), poll.getVotedFor());
        } else {
            subscription.unsubscribe();
            Snackbar.make(layout, R.string.voting_finished, BaseTransientBottomBar.LENGTH_LONG)
                .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        Log.e("XDDD", "Test");
                        NavigationHelper.back(VoteCategoryActivity.this, pollId);
                    }
                })
                .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    public void back() {
        if (getParent() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
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

        Snackbar.make(layout, R.string.vote_category_snackbar, BaseTransientBottomBar.LENGTH_LONG)
            .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                        poll.voteCategory(item);
                    });
                    super.onDismissed(transientBottomBar, event);
                }
            })
            .show();
    }

    /**
     * Called when one of the categories was removed from poll.
     * This happens due to a tie after voting was finished.
     */
    @Override
    public void onCategoryReduce() {
        Snackbar.make(layout, R.string.vote_category_tie, Snackbar.LENGTH_LONG)
                .show();
    }
}
