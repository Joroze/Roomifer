package joroze.com.roomifer;

/**
 * Created by roseje57 on 4/10/2017.
 */

public class Task {

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

    public Task(String title, String description, User authorUser)
    {
        this.title = title;
        this.description = description;
        this.authorName = authorUser.getDisplayName();
        this.author_id = authorUser.getFb_uid();
    }

    public Task(String title, String description, User authorUser, User assigneeUser)
    {
        this.title = title;
        this.description = description;
        this.authorName = authorUser.getDisplayName();
        this.author_id = authorUser.getFb_uid();
        this.assigneeName = assigneeUser.getDisplayName();
        this.assignee_id = assigneeUser.getG_uid();
        this.assigneeProfilePicUrl = assigneeUser.getProfilePictureUrl();

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

}
