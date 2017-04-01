package io.github.entertainmatch.view.category;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Category;

import java.util.ArrayList;

public class VoteCategoryActivity extends AppCompatActivity implements CategoryFragment.OnListFragmentInteractionListener {

    public static final String CATEGORIES_KEY = "categories";

    private CategoryFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ArrayList<Category> categories = intent.getParcelableArrayListExtra(CATEGORIES_KEY);
        fragment = CategoryFragment.newInstance(categories);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.vote_category_content, fragment)
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onListFragmentInteraction(Category item) {
        if (item == null) {
            Log.d("VoteCategoryActivity", "Item was null");
            return;
        }
        item.registerVote();
    }
}
