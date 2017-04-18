package joroze.com.roomifer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

/**
 * Created by jorda on 4/15/2017.
 */

public class GroupHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView cardGroupName;
    private final TextView cardMemberCount;
    private final ImageView groupAuthorProfileImg;

    public GroupHolder(View view) {
        super(view);
        mView = view;
        cardGroupName = (TextView) view.findViewById(R.id.cardGroupName);
        cardMemberCount = (TextView) view.findViewById(R.id.cardMemberCount);
        groupAuthorProfileImg = (ImageView) view.findViewById(R.id.groupAuthorProfileImg);
    }

    public void setCardGroupName(String cardGroupName) {
        this.cardGroupName.setText(cardGroupName);
    }
    public void setGroupMemberCount(int cardMemberCount) {
        this.cardMemberCount.setText(Integer.toString(cardMemberCount));
    }
    public void setCardAuthorProfileImg(String groupAuthorProfileImg) {
        Glide.with(this.groupAuthorProfileImg.getContext()).load(groupAuthorProfileImg).into(this.groupAuthorProfileImg);
    }
}