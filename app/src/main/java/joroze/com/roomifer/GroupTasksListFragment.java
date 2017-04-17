
package joroze.com.roomifer;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/*
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGroupTasksListItemFragmentInteractionListener}
 * interface.
*/



public class GroupTasksListFragment extends DialogFragment {

    private static final String TAG = "SignInActivity";

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference mTaskReference;

    FirebaseUser mCurrentUser;
    User clientUser;


    RecyclerView.Adapter tasksRecyclerViewAdapter;
    RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;


    /* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */
    public interface CreateTaskDialogListener {
        public void onDialogPositiveClick(GroupTasksListFragment dialog);
        public void onDialogNegativeClick(GroupTasksListFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    CreateTaskDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateTaskDialogListener) {
            mListener = (CreateTaskDialogListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CreateTaskDialogFragment.CreateTaskDialogListener");
        }
    }


    public GroupTasksListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (mCurrentUser == null)
        {
            Log.d(TAG, "USER SIGNED OUT AND DOES NOT EXIST HERE, RETURNING TO MAIN ACTIVITY");
            getActivity().finish();
        }

        Log.d(TAG, mCurrentUser.getDisplayName() + "GROUPID TEEEEST - USER IS SIGNED IN IN TASKLIST FRAGMENT");




        String group_id = this.getArguments().getString("message");

        Log.d(TAG, group_id + "GROUPID TEEEEST");


        mTaskReference = FirebaseDatabase.getInstance().getReference()
                .child("groups").child(group_id).child("tasks");


        View rootView = inflater.inflate(R.layout.fragment_group_tasks_list, container, false);


        //itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.grouptaskslist);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        itemTouchHelper.attachToRecyclerView(recyclerView);



        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabaddtask);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Add Task CLICKED!");
                showCreateTaskDialog();
            }
        });


        return rootView;
    }


    @Override
    public void onStart()
    {
        super.onStart();


        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI


                tasksRecyclerViewAdapter = new FirebaseRecyclerAdapter<Task, TaskHolder>(Task.class, R.layout.group_tasks_item, TaskHolder.class, mTaskReference) {
                    @Override
                    protected void populateViewHolder(TaskHolder viewHolder, final Task task, int position) {

                        //clientUser.addToGroup(group);

                        viewHolder.setTaskTitle(task.getTitle());
                        viewHolder.setTaskAssigneeName(task.getAssigneeName());
                        viewHolder.setTaskAssigneeProfileImg(task.getAssigneeProfilePicUrl());

                        Log.d(TAG, "TASK UPDATE FOUND: " + task.getAuthor_id() + task.getTitle());


                        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                    }



                };






                //Post post = dataSnapshot.getValue(Post.class);
                // [START_EXCLUDE]
               // mAuthorView.setText(post.author);
               // mTitleView.setText(post.title);
               // mBodyView.setText(post.body);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(getActivity(), "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mTaskReference.addValueEventListener(postListener);


        recyclerView.setAdapter(tasksRecyclerViewAdapter);

    }


    AlertDialog.Builder builder;


    public void showCreateTaskDialog() {
        //Create an instance of the dialog fragment and show it
        //CreateTaskDialogFragment dialog = new CreateTaskDialogFragment();
        //dialog.show(view.getContext().getApplicationContext().get)
        //dialog.show(view.getContext().getSupportFragmentManager(), "CreateTaskDialogFragment");

        builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.create_task_dialog_layout, new LinearLayout(getActivity()), false));

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                mListener.onDialogPositiveClick(GroupTasksListFragment.this);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mListener.onDialogNegativeClick(GroupTasksListFragment.this);

                getDialog().cancel();
            }
        });

        // Create the AlertDialog object and return it


        builder.create();
        builder.show();
    }



    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

}


    //Task selectedTask;
    //int selectedTaskPosition;

    //Map<String, Object> deleteTaskChildUpdates;

    //StringBuilder snackbarDeleteResultMsg;


    /*
    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


            // Using StringBuilder for String optimization
            StringBuilder dialogTitle;
            StringBuilder dialogMsg;


            selectedTaskPosition = viewHolder.getAdapterPosition();
            selectedTask = clientUser.getGroups().get(selectedTaskPosition);
            deleteTaskChildUpdates = new HashMap<>();

            dialogTitle = new StringBuilder("Confirm to Leave");

            dialogMsg = new StringBuilder("Are you sure you want to leave the group: \"");
            dialogMsg.append(selectedTask.getGroupName());
            dialogMsg.append("?");

            if (selectedTask.getAuthor_id().equals(clientUser.getFb_uid()))
            {
                fullGroupDelete = true;
                dialogTitle.append(" and Delete");
                dialogMsg.append("\n\n(Since you\'re the owner, the group will also be deleted)");
            }

            snackbarDeleteResultMsg = new StringBuilder("You\'ve");

            // ========================================================================
            // Programmatically create an Alert Dialog box "pop-up"
            // DIALOG LOGIC STARTS HERE ===============================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

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
                            if (fullGroupDelete)
                            {
                                // TODO: MAKE IT SO ANDROID APP CONTINOUSLY CHECKS FOR GROUP CHANGES.... Think about it!
                                deleteTaskChildUpdates.put("/groups/" + selectedTask.getId(), null);

                                snackbarDeleteResultMsg.append(" deleted the group: \"");
                            }

                            // If clientUser deletes a group that it is not the author of, then remove clientUser from group in the database
                            else {
                                selectedTask.getMembers().remove(clientUser.getFb_uid());
                                Map<String, Object> groupValues = selectedTask.toMap();
                                deleteTaskChildUpdates.put("/groups/" + selectedTask.getId(), groupValues);

                                snackbarDeleteResultMsg.append(" left the group: \"");
                            }

                            snackbarDeleteResultMsg.append(selectedTask.getGroupName());
                            snackbarDeleteResultMsg.append("\"");

                            // Remove group item from the Recycler List
                            //groupRecyclerViewAdapter.removeGroupItem(selectedTaskPosition);
                            groupRecyclerViewAdapter.notifyItemRemoved(selectedTaskPosition);

                            // Remove group from client user object
                            clientUser.getGroups().remove(selectedTaskPosition);

                            Map<String, Object> userValues = clientUser.toMap();
                            deleteTaskChildUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

                            mDatabase.updateChildren(deleteTaskChildUpdates);

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.mainSnackBarView), snackbarDeleteResultMsg, Snackbar.LENGTH_LONG);
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

}
*/
