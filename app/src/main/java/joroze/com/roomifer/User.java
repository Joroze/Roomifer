package joroze.com.roomifer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;

import static joroze.com.roomifer.FirebaseManageJSON.writeNewGroup;
import static joroze.com.roomifer.FirebaseManageJSON.writeNewUser;

/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class User {

    @Exclude
    public String id;
    public int numOfGroups = 0;

    public String userName;
    public String email;

    public ArrayList<String> groupNames;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String userName, String email) {
        this.id = id;
        this.userName = userName;
        this.email = email;

        writeNewUser(this);
    }

    public User(String id, String userName, String email, String groupName) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.groupNames = new ArrayList<>();
        this.groupNames.add(groupName);
        numOfGroups = this.groupNames.size();

        writeNewUser(this);
        //writeNewGroup(new Group(groupName, userName));
    }

    //TODO Group limit... You can belong up to 3 groups?
    public User(String id, String userName, String email, String... groupNames) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.groupNames = new ArrayList<String>(Arrays.asList(groupNames));
        this.numOfGroups = this.groupNames.size();

        writeNewUser(this);

        //for (String aGroupName: groupNames) {
        //    writeNewGroup(new Group(aGroupName, userName));
        //}

    }


}
