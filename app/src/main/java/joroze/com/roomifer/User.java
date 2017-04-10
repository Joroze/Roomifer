package joroze.com.roomifer;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class User {

    @Exclude
    protected static User clientUser;

    //public String fbUserKey;

    @Exclude
    public String g_uid;

    public String userName;
    public String email;

    public ArrayList<String> groups = new ArrayList<>();
    public int groupCount = 0;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String g_uid, String userName, String email) {
        //this.fbUserKey = fbUserKey;
        this.g_uid = g_uid;
        this.userName = userName;
        this.email = email;
    }

    public User(String g_uid, String userName, String email, ArrayList<String> groups) {
        //this.fbUserKey = fbUserKey;
        this.g_uid = g_uid;
        this.userName = userName;
        this.email = email;
        this.groups = groups;
        this.groupCount = groups.size();
    }

    @Exclude
    public void addToGroup(String groupName)
    {
        groups.add(groupName);
        groupCount = groups.size();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("g_uid", g_uid);
        result.put("userName", userName);
        result.put("email", email);
        result.put("groupCount", groupCount);
        result.put("groups", groups);

        return result;
    }


}
