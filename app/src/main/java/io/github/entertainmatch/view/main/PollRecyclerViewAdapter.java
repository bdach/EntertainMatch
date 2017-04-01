package io.github.entertainmatch.view.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Poll;
import io.github.entertainmatch.view.main.PollFragment.OnListFragmentInteractionListener;
import io.github.entertainmatch.view.main.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PollRecyclerViewAdapter extends RecyclerView.Adapter<PollRecyclerViewAdapter.ViewHolder> {

    private final List<Poll> polls;
    private final OnListFragmentInteractionListener listener;

    public PollRecyclerViewAdapter(List<Poll> polls, OnListFragmentInteractionListener listener) {
        this.polls = polls;
        this.listener = listener;
    }

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

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.poll);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return polls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView nameView;
        private final TextView statusView;
        public Poll poll;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            nameView = (TextView) view.findViewById(R.id.poll_name);
            statusView = (TextView) view.findViewById(R.id.poll_status);
        }

        public void setPoll(Poll poll) {
            this.poll = poll;
            nameView.setText(poll.getName());
            statusView.setText(poll.getStatus().getStringId());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + statusView.getText() + "'";
        }
    }
}
