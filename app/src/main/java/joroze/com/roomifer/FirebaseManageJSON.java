package joroze.com.roomifer;

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

    //private static int;

    private static int checkGroupCount;
    private static ArrayList<String> checkGroups;

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    public static int writeNewUser(final String g_uid, final String userName, final String email) {

        if (g_uid == null) {
            throw new IllegalArgumentException("ERROR: User must have a g_uid (a Google ID) assigned!");
        }

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(g_uid);




        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //User checkUser = dataSnapshot.getValue(User.class);

                if (dataSnapshot.exists()) {
                    User checkUser = dataSnapshot.getValue(User.class);
                    clientUser = new User(g_uid, checkUser.userName, checkUser.email, checkUser.groups);
                } else {
                    clientUser = new User(g_uid, userName, email);
                }

                Map<String, Object> userValues = clientUser.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + g_uid, userValues);

                mDatabase.updateChildren(childUpdates);

                //checkGroups = new ArrayList<String>((ArrayList)dataSnapshot.getValue());
                //Log.d(TAG, Integer.toString(checkGroups.size()) + "HELLO TESTTTTTTTTTTTTT");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });


        //Log.d(TAG, Integer.toString(checkGroups.size()) + "HELLO TESTTTTTTTTTTTTT");

        //Log.d(TAG, Integer.toString(checkGroups.size()));




        return 1;
    }

    public static int writeNewGroup(String groupName, User user) {

        if (user.g_uid == null) {
            throw new IllegalArgumentException("ERROR: User must have a g_uid (a Google ID) assigned!");
        }

       DatabaseReference mGroupReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.g_uid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User checkUser = dataSnapshot.getValue(User.class);
                checkGroupCount = checkUser.groupCount + 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mGroupReference.addListenerForSingleValueEvent(postListener);

        // Remove post value event listener
        if (postListener != null) {
            mGroupReference.removeEventListener(postListener);
        }


        Log.d(TAG, Integer.toString(checkGroupCount));

        // If user attempts to create more groups than 3, don't proceed
        if (checkGroupCount < 0 || checkGroupCount > 3)
        {
            return -1;
        }

        String groupKey = mDatabase.child("groups").push().getKey();

        Group group = new Group(groupName, user);

        Map<String, Object> groupValues = group.toMap();
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + groupKey, groupValues);
        childUpdates.put("/users/" + user.g_uid, userValues);

        mDatabase.updateChildren(childUpdates);

        return 1;
    }


    public static void deleteAccount(User user)
    {
        mDatabase.child("users").child(user.g_uid).removeValue();
    }


}
