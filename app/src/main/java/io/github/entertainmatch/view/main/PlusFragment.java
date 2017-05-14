package io.github.entertainmatch.view.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.entertainmatch.R;
import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlusFragment extends Fragment {
    private static final String PLUS_TEXT = "param1";

    private String plusText;
    @BindView(R.id.plus_count)
    TextView plusTextView;

    public PlusFragment() {
    }

    public static PlusFragment newInstance(Integer plusCount) {
        PlusFragment fragment = new PlusFragment();
        Bundle args = new Bundle();
        args.putString(PLUS_TEXT, "+" + plusCount.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plusText = getArguments().getString(PLUS_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_plus, container, false);
        ButterKnife.bind(this, inflated);
        plusTextView.setText(plusText);
        return inflated;
    }
}
