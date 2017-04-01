package io.github.entertainmatch.view.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.view.category.CategoryFragment.OnListFragmentInteractionListener;
import io.github.entertainmatch.view.category.dummy.DummyContent.DummyItem;

import java.util.Collection;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Category> categories;
    private final OnListFragmentInteractionListener listener;

    public CategoryRecyclerViewAdapter(List<Category> categories, OnListFragmentInteractionListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setCategory(categories.get(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private Button button;
        private ImageView imageView;
        private Category category;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.button = (Button) view.findViewById(R.id.category_vote_button);
            this.imageView = (ImageView) view.findViewById(R.id.category_background);
        }

        public void setCategory(Category category) {
            button.setText(category.getName());
            imageView.setBackgroundResource(category.getImageId());
        }
    }
}
