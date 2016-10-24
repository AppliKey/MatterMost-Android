package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleToNewChannelAdapter extends RecyclerView.Adapter<PeopleToNewChannelAdapter.VH> {

    private final ImageLoader mImageLoader;
    private List<UserPendingInvitation> mUsers = new ArrayList<>(0);
    private final OnUserChosenListener mChosenListener;

    public interface OnUserChosenListener {
        void onChosen(User user, boolean isInvited);
    }

    public PeopleToNewChannelAdapter(OnUserChosenListener listener, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mChosenListener = listener;
    }

    public void addUsers(List<UserPendingInvitation> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.people_new_channel_item, parent, false);
        return new VH(view);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final UserPendingInvitation user = mUsers.get(position);
        holder.mAddedPeopleName.setText(User.getDisplayableName(user.getUser()));
        if (user.isInvited() == false) {
            holder.mAddPeopleButton.setBackgroundResource(R.drawable.ic_add);
        } else {
            holder.mAddPeopleButton.setBackgroundResource(R.drawable.ic_check);
        }
        mImageLoader.displayCircularImage(user.getUser().getProfileImage(), holder.mAddedPeopleAvatar);
        holder.mAddPeopleButton.setOnClickListener(button -> {
            if (user.isInvited() == false) {
                user.setInvited(true);
                holder.mAddPeopleButton.setBackgroundResource(R.drawable.ic_check);
            } else {
                user.setInvited(false);
                holder.mAddPeopleButton.setBackgroundResource(R.drawable.ic_add);
            }
            mChosenListener.onChosen(user.getUser(), user.isInvited());
        });
    }

    static class VH extends RecyclerView.ViewHolder {

        @Bind(R.id.pending_people_avatar)
        ImageView mAddedPeopleAvatar;

        @Bind(R.id.added_people_name)
        TextView mAddedPeopleName;

        @Bind(R.id.add_people_button)
        Button mAddPeopleButton;



        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
