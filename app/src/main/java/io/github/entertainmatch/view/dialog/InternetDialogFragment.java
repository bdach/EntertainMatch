package io.github.entertainmatch.view.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import io.github.entertainmatch.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InternetDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InternetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InternetDialogFragment extends DialogFragment {
    private OnFragmentInteractionListener mListener;

    public InternetDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InternetDialogFragment.
     */
    public static InternetDialogFragment newInstance() {
        return new InternetDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.internet_dialog_message)
            .setPositiveButton(R.string.retry, (dialog, id) -> {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(InternetDialogFragment.this);
            })
            .setNegativeButton(R.string.cancel, (dialog, id) -> {
                // Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(InternetDialogFragment.this);
            });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onDialogPositiveClick(DialogFragment fragment);
        void onDialogNegativeClick(DialogFragment fragment);
    }
}
