package joroze.com.roomifer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static joroze.com.roomifer.MainActivity.fbUpdateUserAndGroup;
import static joroze.com.roomifer.User.clientUser;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGroupListItemFragmentInteractionListener}
 * interface.
 */
public class GroupListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private OnGroupListItemFragmentInteractionListener mListener;
    GroupRecyclerViewAdapter groupRecyclerViewAdapter;

    private ItemTouchHelper itemTouchHelper;

    Group selectedGroup;
    int selectedGroupPosition;
    Map<String, Object> childUpdates;

    boolean fullGroupDelete = false;

    // Using StringBuilder for String optimization
    StringBuilder dialogTitle;
    StringBuilder dialogMsg;
    StringBuilder snackbarDeleteResultMsg;


    private static final String TAG = "SignInActivity";

    public interface MyFragInterface {


        public void hideGroupList();

        public void showGroupList();

    }

    public interface UpdateGroupListInterface {

        public void updateGroupList();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroupListFragment() {
    }


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GroupListFragment newInstance(int columnCount) {
        GroupListFragment fragment = new GroupListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Making sure Main activity implemented interface
        try {
            if (true) {
                ((MyFragInterface) this.getActivity()).hideGroupList();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement MyFragInterface");
        }

        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            groupRecyclerViewAdapter = new GroupRecyclerViewAdapter(new ArrayList<Group>(), mListener);
            recyclerView.setAdapter(groupRecyclerViewAdapter);

            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        return view;
    }

    public void updateGroupList() {
        groupRecyclerViewAdapter.swap(clientUser.getGroups());
    }


    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            /*
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();

            Group prev = groups.remove(fromPosition);
            groups.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            groupRecyclerViewAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
            */
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            selectedGroupPosition = viewHolder.getAdapterPosition();
            selectedGroup = clientUser.getGroups().get(selectedGroupPosition);
            childUpdates = new HashMap<>();

            dialogTitle = new StringBuilder("Confirm to Leave");

            dialogMsg = new StringBuilder("Are you sure you want to leave the group: \"");
            dialogMsg.append(selectedGroup.getGroupName());
            dialogMsg.append("?");

            if (selectedGroup.getAuthor_id().equals(clientUser.getFb_uid()))
            {
                fullGroupDelete = true;
                dialogTitle.append(" and Delete");
                dialogMsg.append("\n\n(Since you\'re the owner, the group will also be deleted)");
            }

            snackbarDeleteResultMsg = new StringBuilder("You\'ve");

            // ========================================================================
            // Programmatically create an Alert Dialog box "pop-up"
            // DIALOG LOGIC STARTS HERE ===============================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

            // set title of the alert dialog
            alertDialogBuilder.setTitle(dialogTitle);

            // set dialog message
            alertDialogBuilder

                    // TODO: Use custom text with HTML design
                    // http://stackoverflow.com/questions/9935692/how-to-set-part-of-text-to-bold-when-using-alertdialog-setmessage-in-android
                    // Html.fromHtml(String source, int flags)
                    .setMessage(dialogMsg)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // If clientUser deletes a group that it is the author of, then delete it from database
                            if (fullGroupDelete == true)
                            {
                                // TODO: MAKE IT SO ANDROID APP CONTINOUSLY CHECKS FOR GROUP CHANGES.... Think about it!
                                childUpdates.put("/groups/" + selectedGroup.getId(), null);

                                snackbarDeleteResultMsg.append(" deleted the group: \"");
                            }

                            // If clientUser deletes a group that it is not the author of, then remove clientUser from group in the database
                            else {
                                selectedGroup.getMembers().remove(clientUser.getFb_uid());
                                Map<String, Object> groupValues = selectedGroup.toMap();
                                childUpdates.put("/groups/" + selectedGroup.getId(), groupValues);

                                snackbarDeleteResultMsg.append(" left the group: \"");
                            }

                            snackbarDeleteResultMsg.append(selectedGroup.getGroupName());
                            snackbarDeleteResultMsg.append("\"");

                            // Remove group item from the Recycler List
                            groupRecyclerViewAdapter.removeGroupItem(selectedGroupPosition);

                            // Remove group from client user object
                            clientUser.getGroups().remove(selectedGroupPosition);

                            Map<String, Object> userValues = clientUser.toMap();
                            childUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

                            mDatabase.updateChildren(childUpdates);

                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.mainSnackBarView), snackbarDeleteResultMsg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close the dialog box

                            // also reset the view adapter to put back in place the slided row
                            groupRecyclerViewAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    })
            .setCancelable(false);

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //DIALOG LOGIC ENDS HERE =============================================================
            //====================================================================================

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupListItemFragmentInteractionListener) {
            mListener = (OnGroupListItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGroupListItemFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGroupListItemFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGroupListFragmentInteraction(Group item);
    }
}
