package joroze.com.roomifer;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;

import static joroze.com.roomifer.FirebaseManageJSON.writeNewGroup;
import static joroze.com.roomifer.FirebaseManageJSON.writeNewUser;
import static joroze.com.roomifer.LoginActivity.user;


/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class Group {

    @Exclude
    public String id = Integer.toString(this.hashCode());

    public int memberCount = 0;

    public String groupName;
    public ArrayList<String> userNames;

    public Group() {

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Each group object will contain a name of the group, and the name of all of the members in it
    /*public Group(String groupName, String... userNames) {
        this.groupName = groupName;
        this.userNames = new ArrayList<String>(Arrays.asList(userNames));
        memberCount = this.userNames.size();

        writeNewGroup(this);

        for (String aUserName : userNames){
            writeNewUser(new User(user.id, aUserName, user.email, groupName));
        }

    }
    */
    public Group(String groupName, User user) {
        this.groupName = groupName;
        this.userNames = new ArrayList<String>();
        this.userNames.add(user.userName);
        memberCount = this.userNames.size();

        writeNewGroup(this);
    }



}
