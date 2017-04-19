package io.github.entertainmatch.view.poll;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.github.entertainmatch.R;
import io.github.entertainmatch.model.Person;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPersonSelectedListener}
 * interface.
 */
public class PersonFragment extends Fragment {

    private static final String PERSON_LIST = "person_list";
    private ArrayList<Person> people = new ArrayList<>();
    private OnPersonSelectedListener listener;
    @Setter
    private AppCompatActivity activity;
    private PersonRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonFragment() {
    }

    @SuppressWarnings("unused")
    public static PersonFragment newInstance(List<Person> people, AppCompatActivity activity) {
        PersonFragment fragment = new PersonFragment();
        fragment.setActivity(activity);
        Bundle args = new Bundle();
        args.putParcelableArrayList(PERSON_LIST, new ArrayList<>(people));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            people = getArguments().getParcelableArrayList(PERSON_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new PersonRecyclerViewAdapter(people, listener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPersonSelectedListener) {
            listener = (OnPersonSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPersonSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * Interface used to notify of selection events.
     */
    public interface OnPersonSelectedListener {
        /**
         * Called when one of the items in the list is toggled using the check box.
         *
         * @param item The toggled {@link Person} item.
         * @param state The state of the item (true if checked, false otherwise).
         */
        void onPersonToggled(Person item, boolean state);
        Context getContext();
    }

    public void setItems(ArrayList<Person> people) {
        adapter.setPeople(people);
        adapter.notifyDataSetChanged();
    }
}
