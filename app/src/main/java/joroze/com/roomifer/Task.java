package joroze.com.roomifer;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roseje57 on 4/10/2017.
 */

public class Task {

    @Exclude
    private String id;

    private String title = "";
    private String description = "";
    private String authorName = "";
    private String author_id = "";
    private String assigneeName = "";
    private String assignee_id = "";
    private String assigneeProfilePicUrl = "";


    // Time time...
    // We need to use Google API Calendar here most likely...
    // ...Why? ...For when a task is due by!

    public Task()
    {

    }


    public Task(String id, String title, String description, User authorUser)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorName = authorUser.getDisplayName();
        this.author_id = authorUser.getFb_uid();
    }

    public Task(String id, String title, String description, User authorUser, User assigneeUser)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorName = authorUser.getDisplayName();
        this.author_id = authorUser.getFb_uid();
        this.assigneeName = assigneeUser.getDisplayName();
        this.assignee_id = assigneeUser.getFb_uid();
        this.assigneeProfilePicUrl = assigneeUser.getProfilePictureUrl();

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("title", title);
        result.put("description", description);
        result.put("authorName", authorName);
        result.put("author_id", author_id);
        result.put("assigneeName", assigneeName);
        result.put("assignee_id", assignee_id);
        result.put("assigneeProfilePicUrl", assigneeProfilePicUrl);

        return result;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAssigneeProfilePicUrl() {
        return assigneeProfilePicUrl;
    }

    public void setAssigneeProfilePicUrl(String assigneeProfilePicUrl) {
        this.assigneeProfilePicUrl = assigneeProfilePicUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }


    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssignee_id() {
        return assignee_id;
    }

    public void setAssignee_id(String assignee_id) {
        this.assignee_id = assignee_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
