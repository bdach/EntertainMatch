package io.github.entertainmatch.view.date;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebasePollController;
import io.github.entertainmatch.firebase.models.FirebasePoll;
import io.github.entertainmatch.model.EventDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * Fragment containing a list of dates for a given event.
 *
 * Activities containing this fragment MUST implement the {@link OnDateSelectedListener}
 * interface.
 */
@NoArgsConstructor
public class DateFragment extends Fragment {

    /**
     * The key used to store and fetch {@link EventDate} items.
     */
    public static final String POLL_KEY = "dates";
    /**
     * The list of all items.
     */
    @Getter
    private ArrayList<EventDate> dates;
    /**
     * Current poll
     */
    private String pollId;
    /**
     * A {@link OnDateSelectedListener} to notify upon selection.
     */
    private OnDateSelectedListener listener;
    /**
     * Reference to fragment's adapter
     */
    private DateRecyclerViewAdapter adapter;


    /**
     * Factory method responsible for creating new instances of this fragment, containing the supplied dates.
     * @param pollId A pollId to pass to the new fragment.
     * @return New fragment instance.
     */
    public static DateFragment newInstance(String pollId) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putString(POLL_KEY, pollId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            dates = new ArrayList<>();
            pollId = getArguments().getString(POLL_KEY);
            FirebasePollController.getLocations(pollId).subscribe(locations -> {
                dates.clear();
                dates.addAll(locations);
                adapter.notifyDataSetChanged();
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            FirebasePollController.getPollOnce(pollId).subscribe(poll -> {
                String facebookId = FacebookUsers.getCurrentUser(null).getFacebookId();
                adapter = new DateRecyclerViewAdapter(dates, listener, !poll.getEventDatesStatus().get("voted").get(facebookId));
                recyclerView.setAdapter(adapter);
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDateSelectedListener) {
            listener = (OnDateSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Method used to prevent elements from being edited (checkboxes)
     */
    public void disallowEdition() {
        adapter.setEditable(false);
    }

    /**
     * Interface used to notify the activity of an item selection.
     */
    public interface OnDateSelectedListener {
        void onDateSelected(EventDate date);
        void onDateToggle(EventDate date, boolean status);
    }
}
