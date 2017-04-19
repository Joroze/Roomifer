
package joroze.com.roomifer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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


public class GroupTasksListFragment extends Fragment {

    private static final String TAG = "SignInActivity";

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference mTaskReference;

    FirebaseUser mCurrentUser;
    User clientUser;
    LinearLayoutManager linearLayoutManager;


    ImageView noTasksAvailableSunImage;
    TextView noTasksAvailableInfo;

    RecyclerView.Adapter tasksRecyclerViewAdapter;
    RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;

    String group_id;
    int group_index;

    public GroupTasksListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (mCurrentUser == null) {
            Log.d(TAG, "USER SIGNED OUT AND DOES NOT EXIST HERE, RETURNING TO MAIN ACTIVITY");
            getActivity().finish();
        } else {
            initializeClientUser();
        }

        group_id = this.getArguments().getString("group_id");
        group_index = this.getArguments().getInt("group_index");


        mTaskReference = FirebaseDatabase.getInstance().getReference()
                .child("groups").child(group_id).child("tasks");


        View rootView = inflater.inflate(R.layout.fragment_group_tasks_list, container, false);


        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.grouptaskslist);
        recyclerView.setHasFixedSize(false);


        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(false);

        recyclerView.setLayoutManager(linearLayoutManager);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

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
    public void onStart() {
        super.onStart();


        noTasksAvailableSunImage = (ImageView)getActivity().findViewById(R.id.notasks);
        noTasksAvailableInfo = (TextView) getActivity().findViewById(R.id.notaskstextview);

        tasksRecyclerViewAdapter = new FirebaseRecyclerAdapter<Task, TaskHolder>(Task.class, R.layout.group_tasks_item, TaskHolder.class, mTaskReference) {
            @Override
            protected void populateViewHolder(TaskHolder viewHolder, final Task task, int position) {

                viewHolder.setTaskTitle(task.getTitle());
                //viewHolder.setTaskAssigneeName();
                //viewHolder.setTaskAssigneeProfileImg();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

            };


                @Override
                protected void onDataChanged() {

                    if (tasksRecyclerViewAdapter.getItemCount() == 0) {
                        noTasksAvailableSunImage.setVisibility(View.VISIBLE);
                        noTasksAvailableInfo.setVisibility(View.VISIBLE);
                    }

                    else
                    {
                        noTasksAvailableSunImage.setVisibility(View.GONE);
                        noTasksAvailableInfo.setVisibility(View.GONE);
                    }
                   //recyclerView.setVisibility(tasksRecyclerViewAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                }
            };

            // Scroll to bottom on new messages
        tasksRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    linearLayoutManager.smoothScrollToPosition(recyclerView, null, tasksRecyclerViewAdapter.getItemCount());
                }
            });

        recyclerView.setAdapter(tasksRecyclerViewAdapter);

    }


    Dialog createTaskDialog;
    TextView taskTime;
    TextView taskDate;

    TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(android.widget.TimePicker view,
                                      int hourOfDay, int minute) {

                    taskTime = (TextView) createTaskDialog.findViewById(R.id.createTaskTimeTextView);
                    //Display the user changed time on TextView
                    taskTime.setText("Hour : " + String.valueOf(hourOfDay)
                            + "\nMinute : " + String.valueOf(minute));
                }
            };

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    taskDate = (TextView) createTaskDialog.findViewById(R.id.createTaskDayTextView);
                    //Display the user changed time on TextView
                    taskDate.setText("Day : " + String.valueOf(day)
                            + "\nMonth : " + String.valueOf(month) + "\nYear : " + String.valueOf(year));

                }
            };




    public void showCreateTaskDialog() {

        // Custom Dialog
        createTaskDialog = new Dialog(getContext());
        createTaskDialog.setContentView(R.layout.create_task_dialog_layout);

        Button btnSetTaskTime = (Button) createTaskDialog.findViewById(R.id.buttonSetTaskTime);
        // if button is clicked, close the custom dialog
        btnSetTaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment(mTimeSetListener);
                newFragment.show(getFragmentManager(),"TimePicker");
            }
        });

        Button btnSetTaskDay = (Button) createTaskDialog.findViewById(R.id.buttonSetTaskDay);
        // if button is clicked, close the custom dialog
        btnSetTaskDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(mDateSetListener);
                newFragment.show(getFragmentManager(),"DatePicker");


            }
        });

        Button btnTaskDone = (Button) createTaskDialog.findViewById(R.id.buttonSetTaskOk);
        // if button is clicked, close the custom dialog
        btnTaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView taskTitle = (TextView)createTaskDialog.findViewById(R.id.createTaskTitleTextEntry);
                TextView taskDescription = (TextView)createTaskDialog.findViewById(R.id.createTaskDescriptionTextEntry);
                fbWriteNewTask(taskTitle.getText().toString(), taskDescription.getText().toString());
                createTaskDialog.dismiss();
            }
        });

        createTaskDialog.show();


    }


    Task selectedTask;
    int selectedTaskPosition;

    Map<String, Object> deleteTaskChildUpdates;

    StringBuilder snackbarDeleteResultMsg;


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
            //clientUser.getGroups().get()
            selectedTask = clientUser.getGroups().get(group_index).getTasks().get(selectedTaskPosition);
            deleteTaskChildUpdates = new HashMap<>();

            dialogTitle = new StringBuilder("Delete Task");

            dialogMsg = new StringBuilder("Are you sure you want to delete the following task: \"");
            dialogMsg.append(selectedTask.getTitle());
            dialogMsg.append("\"?\n\nDescription: ");
            dialogMsg.append(selectedTask.getDescription());

            snackbarDeleteResultMsg = new StringBuilder("You\'ve");

            // ========================================================================
            // Programmatically create an Alert Dialog box "pop-up"
            // DIALOG LOGIC STARTS HERE ===============================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

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

                            // TODO: MAKE IT SO ANDROID APP CONTINOUSLY CHECKS FOR GROUP CHANGES.... Think about it!

                            // Deletes the target task
                            deleteTaskChildUpdates.put("/groups/" + group_id + "/tasks/" + selectedTask.getId(), null);

                            snackbarDeleteResultMsg.append(" deleted the task: \"");
                            snackbarDeleteResultMsg.append(selectedTask.getTitle());
                            snackbarDeleteResultMsg.append("\"");

                            // Remove group item from the Recycler List
                            //groupRecyclerViewAdapter.removeGroupItem(selectedTaskPosition);
                            tasksRecyclerViewAdapter.notifyItemRemoved(selectedTaskPosition);

                            // Remove group from client user object
                            clientUser.getGroups().get(group_index).getTasks().remove(selectedTaskPosition);

                            Map<String, Object> userValues = clientUser.toMap();
                            deleteTaskChildUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

                            mDatabase.updateChildren(deleteTaskChildUpdates);

                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.GroupContentSnackBarView), snackbarDeleteResultMsg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close the dialog box

                            // also reset the view adapter to put back in place the slided row
                            tasksRecyclerViewAdapter.notifyDataSetChanged();
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


    public void fbWriteNewTask(final String taskName, final String taskDescription) {

        DatabaseReference mTaskReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(clientUser.getFb_uid()).child("groups");

        ValueEventListener taskListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI



                    // If the check was passed, we update the user values to the client android app
                clientUser.getGroups().clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Group group = postSnapshot.getValue(Group.class);
                    clientUser.getGroups().add(group);
                }



                    //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Group group = postSnapshot.getValue(Group.class);
                    //  clientUser.getGroups().put(group.getId(),group);
                    //}




                String taskKey = mDatabase.child("groups").child(group_id).child("tasks").push().getKey();

                Task task = new Task(taskKey, taskName, taskDescription, clientUser);


               clientUser.getGroups().get(group_index).getTasks().add(task);

                //clientUser.getGroups().get(group_id).getTasks().add(task);

                Map<String, Object> taskValues = task.toMap();
                Map<String, Object> userValues = clientUser.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/groups/" + group_id + "/tasks/" + taskKey, taskValues);
                childUpdates.put("/users/" + clientUser.getFb_uid(), userValues);


                mDatabase.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mTaskReference.addListenerForSingleValueEvent(taskListener);
        mTaskReference.removeEventListener(taskListener);


        // On success
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.GroupContentSnackBarView), "Task created!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    public void initializeClientUser() {

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mCurrentUser.getUid());

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //User checkUser = dataSnapshot.getValue(User.class);

                if (dataSnapshot.exists()) {
                    // if this user exists, create a user with existing information from Firebase database
                    Log.d(TAG, dataSnapshot.toString());
                    User savedUser = dataSnapshot.getValue(User.class);
                    clientUser = new User(savedUser.getFb_uid(), savedUser.getDisplayName(), savedUser.getEmail(), savedUser.getProfilePictureUrl(), savedUser.getGroups());
                } else {
                    // otherwise, create a new user with default information
                    Log.d(TAG, "User does not exist. Going back to Main activity! ");
                    getActivity().finish();
                }

                Map<String, Object> userValues = clientUser.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

                // update any information to the database
                mDatabase.updateChildren(childUpdates);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mUserReference.addListenerForSingleValueEvent(userListener);

        mUserReference.removeEventListener(userListener);

    }

}

