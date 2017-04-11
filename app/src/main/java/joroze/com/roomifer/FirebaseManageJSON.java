package joroze.com.roomifer;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorda on 4/9/2017.
 */

import static joroze.com.roomifer.User.clientUser;


public class FirebaseManageJSON {

    private static final String TAG = "SignInActivity";

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    public static final int GROUP_COUNT_MAX_LIMIT = 3;

    static Context context;

    public FirebaseManageJSON(Context context)
    {
        FirebaseManageJSON.context = context;
    }

    public static void updateSnackbarResult(int errorCode) {

        // Variable to store the correct message to be shown by a snackbar to the MainActivity layout
        String str_errMsg;

        if (errorCode == 1)
            str_errMsg = "Successfully created your group!";
        else if (errorCode == -1)
            str_errMsg = "You've reached the max group limit! (" + GROUP_COUNT_MAX_LIMIT + ")";
        else
            str_errMsg = "An unknown error has occured!";

        Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(R.id.mainSnackBarView), str_errMsg, Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    public static void writeNewUser(final User user) {

        if (user.getFb_uid() == null) {
            throw new IllegalArgumentException("ERROR: User must have a g_uid (a Google ID) assigned!");
        }

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getFb_uid());



        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //User checkUser = dataSnapshot.getValue(User.class);

                if (dataSnapshot.exists()) {
                    // if this user exists, create a user with existing information from Firebase database
                    Log.d(TAG, dataSnapshot.toString());
                    User savedUser = dataSnapshot.getValue(User.class);
                    clientUser = new User(savedUser.getFb_uid(), savedUser.getUserName(), savedUser.getEmail(), savedUser.groupNames);
                } else {
                    // otherwise, create a new user with default information
                    Log.d(TAG, "New user detected... Creating new user");
                    clientUser = new User(user.getFb_uid(), user.getUserName(), user.getEmail());
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

    public static void writeNewGroup(final String groupName, final User user) {

        if (user.getFb_uid() == null) {
            throw new IllegalArgumentException("ERROR: User must have a g_uid (a Google ID) assigned!");
        }

       DatabaseReference mGroupReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getFb_uid());

        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                if (dataSnapshot.exists()) {

                    // if this user exists, create a user with existing information from Firebase database
                    int checkGroupCount = dataSnapshot.getValue(User.class).getGroupCount();

                    // If user attempts to create more groups than 3, don't proceed
                    if (checkGroupCount < 0 || checkGroupCount > GROUP_COUNT_MAX_LIMIT - 1)
                    {
                        Log.d(TAG, "User has reached the max amount of groups to be in: " + GROUP_COUNT_MAX_LIMIT);
                        // On Failure
                        updateSnackbarResult(-1);
                        return;
                    }
                }

                else {
                    // otherwise, create a new user with default information
                    //writeNewUser(user);
                }

                String groupKey = mDatabase.child("groups").push().getKey();

                Group group = new Group(groupKey, groupName, user);

                Map<String, Object> groupValues = group.toMap();
                Map<String, Object> userValues = user.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/groups/" + groupKey, groupValues);
                childUpdates.put("/users/" + user.getFb_uid(), userValues);

                mDatabase.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mGroupReference.addListenerForSingleValueEvent(groupListener);
        mGroupReference.removeEventListener(groupListener);


        // On Success
        updateSnackbarResult(1);
    }


    public static void deleteAccount(User user)
    {
        Map<String, Object> childUpdates = new HashMap<>();

        for (Group group: user.getGroups()) {

            if (user.getUserName() == group.getAuthor()) {
                childUpdates.put("/groups/" + group.getId(), null);

                //TODO Find a way to remove each user from the group that was deleted...
            }
        }

        childUpdates.put("/users/" + user.getFb_uid(), null);

        /*Group group = new Group(groupId, groupName, user);

        Map<String, Object> groupValues = group.toMap();
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + groupId, groupValues);
        childUpdates.put("/users/" + user.g_uid, userValues);
        */

        mDatabase.updateChildren(childUpdates);
    }


}
