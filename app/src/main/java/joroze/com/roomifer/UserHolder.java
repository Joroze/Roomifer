package joroze.com.roomifer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

/**
 * Created by jorda on 4/15/2017.
 */

public class UserHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView userName;
    private final ImageView userProfileImg;

    public UserHolder(View view) {
        super(view);
        mView = view;
        userName = (TextView) view.findViewById(R.id.searchDisplayName);
        userProfileImg = (ImageView) view.findViewById(R.id.searchUserProfileImg);
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
    public void setUserProfileImg(String userProfileImg) {
        Glide.with(this.userProfileImg.getContext()).load(userProfileImg).into(this.userProfileImg);
    }
}