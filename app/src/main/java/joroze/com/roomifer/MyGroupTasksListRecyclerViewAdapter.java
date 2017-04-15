package joroze.com.roomifer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import joroze.com.roomifer.GroupTasksListFragment.OnGroupTasksListItemFragmentInteractionListener;

import java.util.List;

public class MyGroupTasksListRecyclerViewAdapter extends RecyclerView.Adapter<MyGroupTasksListRecyclerViewAdapter.ViewHolder> {

    private final List<Task> tasks;
    private final OnGroupTasksListItemFragmentInteractionListener mListener;

    public MyGroupTasksListRecyclerViewAdapter(List<Task> items, OnGroupTasksListItemFragmentInteractionListener listener) {
        tasks = items;
        mListener = listener;
    }

    public void swap(List<Task> items){
        tasks.clear();
        tasks.addAll(items);

        notifyItemInserted(items.size() - 1);
        notifyDataSetChanged();
    }

    public void removeGroupItem(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
        // Add whatever you want to do when removing an Item
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.task = tasks.get(position);

        //holder.tv_cardGroupName.setText(tasks.get(position).getGroupName());
        //holder.iv_cardAuthorProfileImg.setImage(tasks.get(position).getAuthor());

        //Glide.with(holder.iv_cardAuthorProfileImg.getContext()).load(tasks.get(position).getAuthorProfilePictureUrl()).into(holder.iv_cardAuthorProfileImg);
        //holder.tv_groupMemberCount.setText("Members: " + Integer.toString(tasks.get(position).getMembers().size()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.OnGroupTasksListItemFragmentInteraction(holder.task);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView tv_cardGroupName;
        //public final ImageView iv_cardAuthorProfileImg;
        //public final TextView tv_groupMemberCount;

        public Task task;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //tv_cardGroupName = (TextView) view.findViewById(R.id.cardGroupName);
            //iv_cardAuthorProfileImg = (ImageView) view.findViewById(R.id.groupAuthorProfileImg);
           // tv_groupMemberCount = (TextView) view.findViewById(R.id.cardMemberCount);
        }

    }
}
