package joroze.com.roomifer;

import android.util.Log;

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
    private static final String TAG = "SignInActivity";


    @Exclude
    protected static User clientUser;

    //public String fbUserKey;

    public String g_uid;
    ArrayList<String> groupNames = new ArrayList<String>();

    public String userName;
    public String email;

    @Exclude
    public ArrayList<Group> groups = new ArrayList<Group>();

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

    public User(String g_uid, String userName, String email, ArrayList<String> groupNames) {
        //this.fbUserKey = fbUserKey;
        this.g_uid = g_uid;
        this.userName = userName;
        this.email = email;
        this.groupNames = groupNames;

        for (String names: groupNames) {
            groupNames.add(names);
        }

        this.groupCount = groups.size();

    }

    @Exclude
    public void addToGroup(Group group)
    {
        groups.add(group);
        groupCount = groups.size();

        groupNames.add(group.getGroupName());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        groupNames = new ArrayList<String>();

        for (Group group: groups) {
            groupNames.add(group.getGroupName());
        }

        result.put("g_uid", g_uid);
        result.put("userName", userName);
        result.put("email", email);
        result.put("groupCount", groupCount);
        result.put("groupNames", groupNames);

        return result;
    }

    public String getG_uid() {
        return g_uid;
    }

    public void setG_uid(String g_uid) {
        this.g_uid = g_uid;
    }

    public ArrayList<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(ArrayList<String> groupNames) {
        this.groupNames = groupNames;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

}
