package joroze.com.roomifer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by roseje57 on 4/21/2017.
 */

public class GroupNotificationsFragment extends Fragment{

    private static final String TAG = "SignInActivity";

    private DatabaseReference mGroupReference;

    private FirebaseUser mCurrentUser;
    private User clientUser;

    String group_id;
    Group currentGroup;

    private ValueEventListener mGroupListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group_notifications, container, false);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (mCurrentUser == null) {
            Log.d(TAG, "USER SIGNED OUT AND DOES NOT EXIST HERE, RETURNING TO MAIN ACTIVITY");
            getActivity().finish();
        } else {
            initializeClientUser();
        }

        group_id = this.getArguments().getString("group_id");

        mGroupReference = FirebaseDatabase.getInstance().getReference().child("groups").child(group_id);

        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Group object and use the values to update the UI

                Log.d(TAG, "Updating currentGroup!");
                currentGroup = dataSnapshot.getValue(Group.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadGroup:onCancelled", databaseError.toException());
                // ...
            }
        };
        mGroupReference.addValueEventListener(groupListener);

        // Save the reference to the groupListener
        mGroupListener = groupListener;



        return rootView;
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


    @Override
    public void onStop() {
        super.onStop();

        // Remove group value event listener
        if (mGroupListener != null) {
            mGroupReference.removeEventListener(mGroupListener);
        }

    }

}
