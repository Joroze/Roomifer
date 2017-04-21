package joroze.com.roomifer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roseje57 on 4/19/2017.
 */

public class GroupSettingsFragment extends Fragment {

    private static final String TAG = "SignInActivity";

    private DatabaseReference mGroupReference;

    private FirebaseUser mCurrentUser;

    RecyclerView groupMemberRosterRecyclerView;
    FirebaseIndexRecyclerAdapter groupMemberRosterRecyclerViewAdapter;


    private ValueEventListener mGroupListener;

    private static DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();


    TextView renameGroupTextEntry;
    TextView GroupMemberCountTextView;

    String group_id;
    Group currentGroup;

    Map<String, Object> deleteTaskChildUpdates;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_settings, container, false);



        renameGroupTextEntry = (TextView) rootView.findViewById(R.id.renameGroupNameEntry);
        GroupMemberCountTextView = (TextView) rootView.findViewById(R.id.groupSettingsMemberCountTextView);

        groupMemberRosterRecyclerView = (RecyclerView) rootView.findViewById(R.id.listGroupMemberUsers);
        groupMemberRosterRecyclerView.setHasFixedSize(false);

        Button updateGroupSettingsButton = (Button) rootView.findViewById(R.id.updateGroupSettingsButton);

        updateGroupSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentUser.getUid().equals(currentGroup.getAuthor_id())) {

                    if (!renameGroupTextEntry.getText().toString().equals(currentGroup.getGroupName()))
                    {
                        mDatabaseRef.child("groups").child(group_id).child("groupName").setValue(renameGroupTextEntry.getText().toString());
                    }

                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.GroupContentSnackBarView), "Group settings updated!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

                else
                {
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.GroupContentSnackBarView), "Only the Group Owner can do that!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });


        groupMemberRosterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        group_id = this.getArguments().getString("group_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();



        if (mCurrentUser == null) {
            Log.d(TAG, "USER SIGNED OUT AND DOES NOT EXIST HERE, RETURNING TO MAIN ACTIVITY");
            getActivity().finish();
        } else {
            initializeCurrentGroup();
        }




        DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference().child("groups")
                .child(group_id).child("members");

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("users");

        // mDatabase.child("users").child(mCurrentUser.getUid()).child("groups")
        groupMemberRosterRecyclerViewAdapter = new FirebaseIndexRecyclerAdapter<User, UserGroupMemberHolder>(User.class, R.layout.group_member_list_item, UserGroupMemberHolder.class, keyRef, dataRef) {
            @Override
            protected void populateViewHolder(UserGroupMemberHolder viewHolder, final User user, final int position) {

                viewHolder.setUserName(user.getDisplayName());
                viewHolder.setUserProfileImg(user.getProfilePictureUrl());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!mCurrentUser.getUid().equals(user.getFb_uid())) {

                            if (mCurrentUser.getUid().equals(currentGroup.getAuthor_id())) {


                                // Using StringBuilder for String optimization
                                StringBuilder dialogTitle;
                                StringBuilder dialogMsg;


                                dialogTitle = new StringBuilder("Kick Member");

                                dialogMsg = new StringBuilder("Are you sure you want to kick \"");
                                dialogMsg.append(user.getDisplayName());
                                dialogMsg.append("\" out of the group?");

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

                                                // Remove group item from the Recycler List


                                                // Add the selected user's id to the group index
                                                mDatabaseRef.child("groups").child(group_id).child("members").child(user.getFb_uid()).setValue(null);

                                                // Go to the added user's index and add the current group's id
                                                mDatabaseRef.child("users").child(user.getFb_uid()).child("groups").child(group_id).setValue(null);
                                                // Remove group from client user object
                                                //currentGroup.getTasks().remove(selectedTaskPosition);

                                                //Map<String, Object> groupValues = currentGroup.toMap();
                                                //deleteTaskChildUpdates.put("/groups/" + currentGroup.getGroup_id(), groupValues);

                                                //mDatabase.updateChildren(deleteTaskChildUpdates);

                                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.GroupContentSnackBarView), "Kicked member.", Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, close the dialog box

                                                dialog.cancel();
                                            }
                                        })
                                        .setCancelable(false);

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                //DIALOG LOGIC ENDS HERE =============================================================
                                //====================================================================================


                            } else {
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.GroupContentSnackBarView), "Only the Group Owner can do that.", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }

                        }
                    }
                });

            }

            @Override
            protected void onDataChanged() {

            }

        };


        groupMemberRosterRecyclerView.setAdapter(groupMemberRosterRecyclerViewAdapter);

        return rootView;
    }


    public void initializeCurrentGroup() {

        mGroupReference = FirebaseDatabase.getInstance().getReference()
                .child("groups").child(group_id);

        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // if this group exists, create a group instance with existing information from Firebase database
                    currentGroup = dataSnapshot.getValue(Group.class);

                    renameGroupTextEntry.setText(currentGroup.getGroupName());
                    GroupMemberCountTextView.setText(String.valueOf(currentGroup.getMembers().size()));


                } else {
                    Log.d(TAG, "Group does not exist. Going back to Main activity! ");
                    getActivity().finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mGroupReference.addValueEventListener(groupListener);

        // Hold onto the reference!
        mGroupListener = groupListener;

    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Remove group value event listener
        if (mGroupListener != null) {
            mGroupReference.removeEventListener(mGroupListener);
        }

    }


}
