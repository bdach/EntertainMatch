package io.github.entertainmatch.view.category;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Category;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

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
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
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
    }
}
