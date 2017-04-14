package joroze.com.roomifer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import joroze.com.roomifer.GroupListFragment.OnGroupListItemFragmentInteractionListener;

import java.util.List;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private final List<Group> mValues;
    private final OnGroupListItemFragmentInteractionListener mListener;

    public GroupRecyclerViewAdapter(List<Group> items, OnGroupListItemFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void swap(List<Group> items){
        mValues.clear();
        mValues.addAll(items);

        notifyItemInserted(items.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tv_cardGroupName.setText(mValues.get(position).getGroupName());
        //holder.iv_cardAuthorProfileImg.setImage(mValues.get(position).getAuthor());

        Glide.with(holder.iv_cardAuthorProfileImg.getContext()).load(mValues.get(position).getAuthorProfilePictureUrl()).into(holder.iv_cardAuthorProfileImg);
        holder.tv_groupMemberCount.setText("Members: " + Integer.toString(mValues.get(position).getMembers().size()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onGroupListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_cardGroupName;
        public final ImageView iv_cardAuthorProfileImg;
        public final TextView tv_groupMemberCount;

        public Group mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_cardGroupName = (TextView) view.findViewById(R.id.cardGroupName);
            iv_cardAuthorProfileImg = (ImageView) view.findViewById(R.id.groupAuthorProfileImg);
            tv_groupMemberCount = (TextView) view.findViewById(R.id.cardMemberCount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tv_groupMemberCount.getText() + "'";
        }
    }
}