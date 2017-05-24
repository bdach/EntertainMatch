package io.github.entertainmatch.view.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.Poll;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class EventFragment extends Fragment {

    private OnEventSelectedListener listener;
    private Map<String, Pair<Integer, FirebaseCompletedPoll>> pollMap = new HashMap<>();
    private ArrayList<FirebaseCompletedPoll> polls = new ArrayList<>();
    private EventRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new EventRecyclerViewAdapter(polls, listener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventSelectedListener) {
            listener = (OnEventSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updatePoll(FirebaseCompletedPoll poll) {
        if (pollMap.containsKey(poll.getId())) {
            Pair<Integer, FirebaseCompletedPoll> pair = pollMap.get(poll.getId());
            pair.second.update(poll);
            adapter.notifyItemChanged(pair.first);
        } else {
            Pair<Integer, FirebaseCompletedPoll> pair = Pair.create(polls.size(), poll);
            pollMap.put(poll.getId(), pair);
            polls.add(poll);
            adapter.notifyItemInserted(polls.size() - 1);
        }
    }

    public interface OnEventSelectedListener {
        void onEventClicked(FirebaseCompletedPoll item);
        Context getContext();
    }
}
