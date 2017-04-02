package io.github.entertainmatch.view.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Category;
import io.github.entertainmatch.view.category.CategoryFragment.OnListFragmentInteractionListener;
import org.w3c.dom.Text;

import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Category> categories;
    private final OnListFragmentInteractionListener listener;
    private boolean canVote = true;

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
        holder.setVoting(canVote);

        holder.voteButton.setOnClickListener(new View.OnClickListener() {
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

    public void disableVoting() {
        canVote = false;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;

        private RelativeLayout labelLayout;
        private TextView titleView;
        private TextView voteCountView;
        private ImageButton voteButton;
        private ImageView imageView;
        private Category category;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.labelLayout = (RelativeLayout) view.findViewById(R.id.category_label);
            this.imageView = (ImageView) view.findViewById(R.id.category_background);
            this.titleView = (TextView) view.findViewById(R.id.category_name);
            this.voteButton = (ImageButton) view.findViewById(R.id.category_vote_button);
            this.voteCountView = (TextView) view.findViewById(R.id.category_vote_count);
        }

        public void setCategory(Category category) {
            this.category = category;
            imageView.setImageResource(category.getImageId());
            titleView.setText(category.getName());
            if (category.isVotedFor()) {
                labelLayout.setBackgroundResource(R.color.colorAccentShade);
            }
        }

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
