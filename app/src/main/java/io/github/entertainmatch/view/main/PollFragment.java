package io.github.entertainmatch.view.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Poll;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment containing the list of polls.
 *
 * Activities with this fragment must implement the {@link OnPollSelectedListener} interface.
 */
@NoArgsConstructor
public class PollFragment extends Fragment {

    /**
     * A {@link OnPollSelectedListener} to be notified of user selection.
     */
    private OnPollSelectedListener listener;

    private Map<String, Pair<Integer, Poll>> pollMap = new HashMap<>();
    private ArrayList<Poll> polls = new ArrayList<>();
    private PollRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new PollRecyclerViewAdapter(polls, listener);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPollSelectedListener) {
            listener = (OnPollSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPollSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Either adds poll to the view or updates existing
     * @param poll Updated or new poll
     */
    public void updatePoll(Poll poll) {
        if (pollMap.containsKey(poll.getPollId())) {
            Pair<Integer, Poll> pair = pollMap.get(poll.getPollId());
            pair.second.update(poll);
            adapter.notifyItemChanged(pair.first);
        } else {
            Pair<Integer, Poll> pair = Pair.create(polls.size(), poll);
            pollMap.put(poll.getPollId(), pair);
            polls.add(poll);
            adapter.notifyItemInserted(polls.size() - 1);
        }
    }

    public void deletePoll(String pollId) {
        Poll poll = pollMap.get(pollId).second;
        polls.remove(poll);
        pollMap.remove(pollId);
    }

    /**
     * Interface allowing activities containing this fragment type to be notified of a selection.
     */
    public interface OnPollSelectedListener {
        /**
         * Called when a {@link Poll} is selected from the list by the user.
         *
         * @param poll The selected {@link Poll}.
         */
        void viewPollProgress(Poll poll);
        Context getContext();
    }
}
