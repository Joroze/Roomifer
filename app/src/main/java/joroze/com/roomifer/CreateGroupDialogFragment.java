package joroze.com.roomifer;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * Created by jorda on 4/10/2017.
 */

public class CreateGroupDialogFragment extends DialogFragment {

    private static final String TAG = "SignInActivity";


    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CreateGroupDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    CreateGroupDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateGroupDialogListener) {
            mListener = (CreateGroupDialogListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CreateGroupDialogFragment.CreateGroupDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.create_group_dialog_layout, null));

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                mListener.onDialogPositiveClick(CreateGroupDialogFragment.this);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mListener.onDialogNegativeClick(CreateGroupDialogFragment.this);

                CreateGroupDialogFragment.this.getDialog().cancel();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

}
