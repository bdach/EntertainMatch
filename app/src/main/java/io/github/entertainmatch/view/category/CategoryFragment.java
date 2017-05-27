package io.github.entertainmatch.view.category;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.utils.HashMapExt;
import io.github.entertainmatch.utils.ListExt;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A fragment used to display a list of {@link Category} items to select from.
 *
 * Activities containing this fragment must implement the {@link OnCategorySelectedListener} interface.
 */
@NoArgsConstructor
public class CategoryFragment extends Fragment {

    /**
     * The key used to store and fetch {@link Category} items to be displayed on the list.
     */
    public static final String CATEGORIES_KEY = "categories";
    /**
     * The list of displayed {@link Category} items.
     */
    private ArrayList<Category> categories;
    /**
     * The listener to notify upon the user selecting a {@link Category}.
     */
    private OnCategorySelectedListener listener;
    /**
     * The {@link RecyclerView.Adapter} containing the items.
     */
    private CategoryRecyclerViewAdapter adapter;

    /**
     * Factory method used to pass a list of categories to a new instance of this fragment.
     * @param categories An {@link ArrayList} of categories to pass to the fragment.
     * @return New instance of the fragment containing the categories.
     */
    public static CategoryFragment newInstance(ArrayList<Category> categories) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CATEGORIES_KEY, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            categories = getArguments().getParcelableArrayList(CATEGORIES_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new CategoryRecyclerViewAdapter(categories, listener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategorySelectedListener) {
            listener = (OnCategorySelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Registers a vote for the supplied {@link Category}.
     * @param item
     */
    public void registerVote(Category item) {
        item.registerVote();
        adapter.disableVoting();
        adapter.sortItemsByVotes();
        adapter.notifyDataSetChanged();
    }

    /**
     * Updates category counts with new data supplied by Firebase
     * @param categoryToCount
     * @param votedFor
     */
    public void updateCategories(Map<String, Integer> categoryToCount, Map<String, String> votedFor) {
        String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();

        for (Category category : categories) {
            category.setVoteCount(categoryToCount.get(category.getId()));
            // A vote can never be undone. This should prevent object graph updates
            // from other users losing user selection
            if (!category.isVotedFor()) {
                category.setVotedFor(category.getId().equals(votedFor.get(facebookId)));
            }
        }

        if (!ListExt.any(categories, Category::isVotedFor)) {
            adapter.setCanVote(true);
        }

        Set<String> existingIds = categoryToCount.keySet();
        if (ListExt.removeIf(categories, category -> !existingIds.contains(category.getId()))) {
            listener.onCategoryReduce();
        }

        adapter.notifyDataSetChanged();
    }

    public void categoriesChanged() {
        adapter.notifyDataSetChanged();
    }

    /**
     * Interface allowing activities containing this fragment to be notified of a {@link Category} selection.
     */
    public interface OnCategorySelectedListener {
        /**
         * Called when a {@link Category} is selected by the user.
         * @param item The selected {@link Category} item.
         */
        void onCategorySelected(Category item);

        /**
         * Called when one of the categories was removed from poll.
         * This happens due to a tie after voting was finished.
         */
        void onCategoryReduce();
    }
}
