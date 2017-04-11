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

    public String getFb_uid() {
        return fb_uid;
    }

    public void setFb_uid(String fb_uid) {
        this.fb_uid = fb_uid;
    }

    @Exclude
    private String fb_uid;

    ArrayList<String> groupNames = new ArrayList<String>();

    private String userName;
    private String email;

    @Exclude
    private ArrayList<Group> groups = new ArrayList<Group>();

    private int groupCount = 0;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fb_uid, String userName, String email) {
        //this.fbUserKey = fbUserKey;
        this.fb_uid = fb_uid;
        this.userName = userName;
        this.email = email;
    }

    public User(String fb_uid, String userName, String email, ArrayList<String> groupNames) {
        //this.fbUserKey = fbUserKey;
        this.fb_uid = fb_uid;
        this.userName = userName;
        this.email = email;
        this.groupNames = groupNames;

        for (String names: groupNames) {
            this.groups.add(new Group(g_uid, name, new User(g_uid, userName, email)));
        }

        this.groupCount = this.groups.size();

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

        result.put("fb_uid", fb_uid);
        result.put("userName", userName);
        result.put("email", email);
        result.put("groupCount", groupCount);
        result.put("groupNames", groupNames);

        return result;
    }

    public String getG_uid() {
        return fb_uid;
    }

    public void setG_uid(String g_uid) {
        this.fb_uid = fb_uid;
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
