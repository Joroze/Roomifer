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
    private String id;

    private String author;
    private String groupName;

    private Map<String, Boolean> members = new HashMap<>();
    private int memberCount = 0;

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
    public Group(String id, String groupName, User user) {
        this.id = id;
        this.groupName = groupName;
        this.author = user.getUserName();
        this.members.put(user.getFb_uid(), true);
        this.memberCount = this.members.size();

        user.addToGroup(this);
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("groupName", groupName);
        result.put("memberCount", memberCount);
        result.put("members", members);

        return result;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

}
