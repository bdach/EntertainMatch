package io.github.entertainmatch.view.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.view.category.CategoryFragment.OnCategorySelectedListener;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A {@link RecyclerView.Adapter} for {@link Category} items.
 */
@RequiredArgsConstructor
public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of all {@link Category} items.
     */
    private final List<Category> categories;
    /**
     * The {@link OnCategorySelectedListener} to be notified of {@link Category} selections.
     */
    private final OnCategorySelectedListener listener;
    private boolean canVote = true;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setCategory(categories.get(position));
        holder.setVoting(canVote);

        holder.voteButton.setOnClickListener(v -> {
            if (null != listener) {
                listener.onCategorySelected(holder.category);
            }
        });
    }

    /**
     * Disables the user's ability to vote for a category.
     */
    public void disableVoting() {
        canVote = false;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * The {@link RecyclerView.ViewHolder} for {@link Category} items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
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
         * The button used to vote for the category.
         */
        @BindView(R.id.category_vote_button)
        ImageButton voteButton;
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
            ButterKnife.bind(this, view);
        }

        /**
         * Sets the {@link Category} backing model.
         *
         * @param category The {@link Category} object to associate this view holder with.
         */
        public void setCategory(Category category) {
            this.category = category;
            imageView.setImageResource(category.getImageId());
            titleView.setText(category.getName());
            if (category.isVotedFor()) {
                labelLayout.setBackgroundResource(R.color.colorAccentShade);
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
                voteButton.setVisibility(View.VISIBLE);
            } else {
                voteCountView.setVisibility(View.VISIBLE);
                voteCountView.setText(category.getVoteCount().toString());
                voteButton.setVisibility(View.INVISIBLE);
            }
        }
    }
}
