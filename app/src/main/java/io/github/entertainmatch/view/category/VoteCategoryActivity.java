package io.github.entertainmatch.view.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.model.Poll;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_category);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateCategoryList();
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

        fragment = CategoryFragment.newInstance(categories);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vote_category_content, fragment)
                .commit();
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
        poll.update(item);

        Snackbar.make(layout, R.string.vote_category_snackbar, BaseTransientBottomBar.LENGTH_LONG)
                .show();
    }
}
