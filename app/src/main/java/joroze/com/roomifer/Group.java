package joroze.com.roomifer;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class Group {

    @Exclude
    private String id;

    private String author;

    private String author_id;
    private String authorProfilePictureUrl;

    private String groupName;

    private ArrayList<Task> tasks = new ArrayList<Task>();

    private Map<String, Boolean> members = new HashMap<>();

    public Group() {

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public Group(String id, String groupName, User user) {
        this.id = id;
        this.groupName = groupName;
        this.author = user.getDisplayName();
        this.author_id = user.getFb_uid();
        this.authorProfilePictureUrl = user.getProfilePictureUrl();
        this.members.put(user.getFb_uid(), true);

        user.addToGroup(this);
    }

    public Group(String id, String groupName, User user, ArrayList<Task> tasks) {
        this.id = id;
        this.groupName = groupName;
        this.author = user.getDisplayName();
        this.author_id = user.getFb_uid();
        this.authorProfilePictureUrl = user.getProfilePictureUrl();
        this.tasks = tasks;
        this.members.put(user.getFb_uid(), true);

        user.addToGroup(this);
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("author", author);
        result.put("author_id", author_id);
        result.put("groupName", groupName);
        result.put("members", members);
        result.put("authorProfilePictureUrl", authorProfilePictureUrl);
        result.put("tasks", tasks);

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

    public String getAuthorProfilePictureUrl() {
        return authorProfilePictureUrl;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public String getAuthor_id() {
        return author_id;
    }
}
