package joroze.com.roomifer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;


/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class Group {

    public String groupName;
    public User users[];
    public int memberCount = 0;


    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Group() {

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Each group object will contain a name of the group, and the name of all of the members in it
    public Group(String groupName, User... users) {
        this.groupName = groupName;
        this.users = users;
    }

    private void writeNewGroup(String groupId, String groupName, User... users) {
        Group group = new Group(groupName, users);

        //mDatabase.child("groups").child(groupId).setValue();
        //mDatabase.child("groups").child(groupId).child("name").setValue(groupName);
        //mDatabase.child("groups").child(groupId).child("name").child(groupName).child("members").child(users);
        //mDatabase.child("users").child(userId).child("groups").child("group_name").setValue(true);
    }

}
