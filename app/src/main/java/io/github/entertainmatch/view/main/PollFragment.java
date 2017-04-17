package io.github.entertainmatch.view.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Poll;
import lombok.NoArgsConstructor;

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
            recyclerView.setAdapter(new PollRecyclerViewAdapter(Poll.mockData(), listener));
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
     * Interface allowing activities containing this fragment type to be notified of a selection.
     */
    public interface OnPollSelectedListener {
        /**
         * Called when a {@link Poll} is selected from the list by the user.
         *
         * @param poll The selected {@link Poll}.
         */
        void onPollSelected(Poll poll);
    }
}
