package joroze.com.roomifer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by roseje57 on 4/8/2017.
 */

@IgnoreExtraProperties
public class User {

    @Exclude
    public String id;

    public String username;
    public String email;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public User() {

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }


}
