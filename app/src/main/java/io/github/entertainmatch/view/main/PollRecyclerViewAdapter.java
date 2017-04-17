package io.github.entertainmatch.view.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Poll;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A {@link RecyclerView.Adapter} for {@link Poll} objects.
 */
@RequiredArgsConstructor
public class PollRecyclerViewAdapter extends RecyclerView.Adapter<PollRecyclerViewAdapter.ViewHolder> {

    /**
     * The list of polls to be displayed in the list.
     */
    private final List<Poll> polls;

    /**
     * A {@link PollFragment.OnPollSelectedListener} to be notified of item selections.
     */
    private final PollFragment.OnPollSelectedListener listener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_poll, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Poll poll = polls.get(position);
        holder.setPoll(poll);

        holder.view.setOnClickListener(v -> {
            if (null != listener) {
                listener.onPollSelected(holder.poll);
            }
        });
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    /**
     * The {@link RecyclerView.ViewHolder} for {@link Poll} items.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The root view of the item.
         */
        private final View view;
        /**
         * Text label containing the poll name.
         */
        @BindView(R.id.poll_name)
        TextView nameView;
        /**
         * Text label containing the poll status.
         */
        @BindView(R.id.poll_status)
        TextView statusView;
        /**
         * The backing {@link Poll} item.
         */
        private Poll poll;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        public void setPoll(Poll poll) {
            this.poll = poll;
            nameView.setText(poll.getName());
            statusView.setText(poll.getPollStage().getStageStringId());
        }
    }
}
