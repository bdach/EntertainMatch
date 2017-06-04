package io.github.entertainmatch.view.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.utils.ListExt;
import io.github.entertainmatch.view.category.CategoryFragment.OnCategorySelectedListener;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link RecyclerView.Adapter} for {@link Category} items.
 */
public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of all {@link Category} items.
     */
    private List<Category> categories;
    /**
     * The {@link OnCategorySelectedListener} to be notified of {@link Category} selections.
     */
    private final OnCategorySelectedListener listener;

    @Setter
    private boolean canVote = true;

    public CategoryRecyclerViewAdapter(List<Category> categories, OnCategorySelectedListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);

        if (ListExt.any(categories, Category::isVotedFor))
            setCanVote(false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setCategory(categories.get(position));
        holder.setVoting(canVote);

        holder.view.setOnClickListener(v -> {
            if (null != listener && canVote) {
                listener.onCategorySelected(holder.category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void sortItemsByVotes() {
        ArrayList<Category> sorted = new ArrayList<>(categories);
        Collections.sort(sorted, (o1, o2) -> o2.getVoteCount().compareTo(o1.getVoteCount()));
        List<Category> old = categories;
        for (int newIndex = 0; newIndex < categories.size(); ++newIndex) {
            int oldIndex = old.indexOf(categories.get(newIndex));
            notifyItemMoved(oldIndex, newIndex);
        }
        categories.clear();
        categories.addAll(sorted);
    }

    /**
     * The {@link RecyclerView.ViewHolder} for {@link Category} items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        /**
         * The {@link RelativeLayout} containing the category labels.
         */
        @BindView(R.id.category_label)
        RelativeLayout labelLayout;
        /**
         * The text view containing the category name.
         */
        @BindView(R.id.category_name)
        TextView titleView;
        /**
         * The text view containing the vote count for the given category.
         */
        @BindView(R.id.category_vote_count)
        TextView voteCountView;
        /**
         * The {@link ImageView} used to display the category image.
         */
        @BindView(R.id.category_background)
        ImageView imageView;
        /**
         * The backing {@link Category} model object.
         */
        private Category category;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        /**
         * Sets the {@link Category} backing model.
         *
         * @param category The {@link Category} object to associate this view holder with.
         */
        public void setCategory(Category category) {
            this.category = category;
            Picasso.with(imageView.getContext())
                .load(category.getImageUrl())
                .into(imageView);
            titleView.setText(category.getName());
            if (category.isVotedFor()) {
                labelLayout.setBackgroundResource(R.color.colorAccentShade);
            } else {
                labelLayout.setBackgroundResource(R.color.colorShade);
            }
        }

        /**
         * Sets the view appearance according to whether or not voting is permitted.
         *
         * @param canVote True if the user should be able to vote, false otherwise.
         */
        public void setVoting(boolean canVote) {
            if (canVote) {
                voteCountView.setVisibility(View.INVISIBLE);
            } else {
                voteCountView.setVisibility(View.VISIBLE);
                voteCountView.setText(category.getVoteCount().toString());
            }
        }
    }
}
