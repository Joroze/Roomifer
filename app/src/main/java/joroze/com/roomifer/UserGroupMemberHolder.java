package joroze.com.roomifer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by roseje57 on 4/21/2017.
 */

public class UserGroupMemberHolder extends RecyclerView.ViewHolder {

    private final View mView;
    private final TextView userName;
    private final ImageView userProfileImg;

    public UserGroupMemberHolder(View view) {
        super(view);
        mView = view;
        userName = (TextView) view.findViewById(R.id.memberListItemUserName);
        userProfileImg = (ImageView) view.findViewById(R.id.memberListItemUserProfileImg);
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public void setUserProfileImg(String userProfileImg) {
        Glide.with(this.userProfileImg.getContext()).load(userProfileImg).into(this.userProfileImg);
    }

}

