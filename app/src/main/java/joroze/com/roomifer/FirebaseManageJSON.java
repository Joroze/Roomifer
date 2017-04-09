package joroze.com.roomifer;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static joroze.com.roomifer.LoginActivity.user;

/**
 * Created by jorda on 4/9/2017.
 */

public class FirebaseManageJSON {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public static void writeNewUser(User user) {

        mDatabase.child("users").child(user.id).setValue(user);
        //mDatabase.child("users").child(userId).child()
        //mDatabase.child("users").child(userId).child("username").setValue(name);

        //mDatabase.child("users").child(userId).child("groups").child("group_name").setValue(true);
    }

    public static void writeUpdateUser(User user) {

        mDatabase.child("users").child(user.id).setValue(user);
        //mDatabase.child("users").child(userId).child()
        //mDatabase.child("users").child(userId).child("username").setValue(name);

        //mDatabase.child("users").child(userId).child("groups").child("group_name").setValue(true);
    }


    public static void writeNewGroup(Group group) {
        // Create the group data
        mDatabase.child("groups").child(group.id).setValue(group);
        mDatabase.child("users").child(user.id).setValue(user.groupNames);
        //mDatabase.child("users").child(user.id).setValue(group.userNames.get(0));

        // Create the user's group data
        //mDatabase.child("users").child(user.id).child("groups").setValue(user.g);
    }

    public static void deleteAccount(User user)
    {
        mDatabase.child("users").child(user.id).removeValue();
    }


}
