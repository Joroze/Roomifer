package joroze.com.roomifer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

/**
 * Created by jorda on 4/15/2017.
 */

public class TaskHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private final TextView taskTitle;
    private final TextView taskAssigneeName;
    private final ImageView taskAssigneeProfileImg;

    public TaskHolder(View view) {
        super(view);
        mView = view;
        taskTitle = (TextView) view.findViewById(R.id.taskTitle);
        taskAssigneeName = (TextView) view.findViewById(R.id.taskAssigneeName);
        taskAssigneeProfileImg = (ImageView) view.findViewById(R.id.taskAssigneeProfileImg);
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle.setText(taskTitle);
    }
    public void setTaskAssigneeName(String taskAssigneeName) {
        this.taskAssigneeName.setText(taskAssigneeName);
    }
    public void setTaskAssigneeProfileImg(String taskAssigneeProfileImg) {
        Glide.with(this.taskAssigneeProfileImg.getContext()).load(taskAssigneeProfileImg).into(this.taskAssigneeProfileImg);
    }
}