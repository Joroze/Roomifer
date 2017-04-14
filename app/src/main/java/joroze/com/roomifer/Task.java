package joroze.com.roomifer;

/**
 * Created by roseje57 on 4/10/2017.
 */

public class Task {

    private String title = "";
    private String description = "";
    private String author = "";
    private String assignedTo = "";

    // Time time...
    // We need to use Google API Calendar here most likely...
    // ...Why? ...For when a task is due by!

    public Task(String title, String description, String author)
    {
        this.title = title;
        this.description = description;
        this.author = author;
    }

    public Task(String title, String description, String author, String assignedTo)
    {
        this.title = title;
        this.description = description;
        this.author = author;
        this.assignedTo = assignedTo;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
