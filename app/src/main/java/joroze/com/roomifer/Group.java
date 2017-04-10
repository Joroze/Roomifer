package joroze.com.roomifer;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static joroze.com.roomifer.FirebaseManageJSON.writeNewGroup;
import static joroze.com.roomifer.FirebaseManageJSON.writeNewUser;

/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class Group {

    @Exclude
    public String id = Integer.toString(this.hashCode());

    public String author;
    public String groupName;

    public Map<String, Boolean> userNames = new HashMap<>();
    public int memberCount = 0;

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
            writeNewUser(new User(user.g_uid, aUserName, user.email, groupName));
        }

    }

    */
    public Group(String groupName, User user) {
        this.groupName = groupName;
        this.author = user.userName;
        this.userNames.put(user.userName, true);
        memberCount = this.userNames.size();

        user.addToGroup(groupName);
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("groupName", groupName);
        result.put("memberCount", memberCount);
        result.put("members", userNames);

        return result;
    }



}
