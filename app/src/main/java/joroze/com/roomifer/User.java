package joroze.com.roomifer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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
    private String fb_uid;


    private String profilePictureUrl;

    private String displayName;
    private String email;


    private HashMap<String, Boolean> groups = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String fb_uid, String displayName, String email, String profilePictureUrl) {
        //this.fbUserKey = fbUserKey;
        this.fb_uid = fb_uid;
        this.displayName = displayName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public User(String fb_uid, String displayName, String email, String profilePictureUrl, HashMap<String, Boolean> groups) {
        //this.fbUserKey = fbUserKey;
        this.fb_uid = fb_uid;
        this.displayName = displayName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.groups = groups;
    }

    @Exclude
    public void addToGroup(Group group)
    {
        groups.put(group.getGroup_id(), true);
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("fb_uid", fb_uid);
        result.put("displayName", displayName);
        result.put("email", email);
        result.put("profilePictureUrl", profilePictureUrl);
        result.put("groups", groups);

        return result;
    }


    public String getFb_uid() {
        return fb_uid;
    }

    public void setFb_uid(String fb_uid) {
        this.fb_uid = fb_uid;
    }

    public String getG_uid() {
        return fb_uid;
    }

    public void setG_uid(String g_uid) {
        this.fb_uid = fb_uid;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUri) {
        this.profilePictureUrl = profilePictureUri;
    }

    public HashMap<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, Boolean> groups) {
        this.groups = groups;
    }

}
