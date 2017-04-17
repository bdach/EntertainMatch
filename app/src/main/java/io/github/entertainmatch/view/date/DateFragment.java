package io.github.entertainmatch.view.date;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.EventDate;
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
    public static final String DATES_KEY = "dates";
    /**
     * The list of all items.
     */
    private ArrayList<EventDate> dates;
    /**
     * A {@link OnDateSelectedListener} to notify upon selection.
     */
    private OnDateSelectedListener listener;


    /**
     * Factory method responsible for creating new instances of this fragment, containing the supplied dates.
     * @param dates An {@link ArrayList} of {@link EventDate}s to pass to the new fragment.
     * @return New fragment instance.
     */
    public static DateFragment newInstance(ArrayList<EventDate> dates) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATES_KEY, dates);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            dates = getArguments().getParcelableArrayList(DATES_KEY);
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
            recyclerView.setAdapter(new DateRecyclerViewAdapter(dates, listener));
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
     * Interface used to notify the activity of an item selection.
     */
    public interface OnDateSelectedListener {
        void onDateSelected(EventDate date);
    }
}
