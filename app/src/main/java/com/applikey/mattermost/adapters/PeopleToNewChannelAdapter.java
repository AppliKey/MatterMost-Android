package com.applikey.mattermost.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleToNewChannelAdapter extends RecyclerView.Adapter<PeopleToNewChannelAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private List<User> mUsers;
    private List<User> mAlreadyAddedUsers = new ArrayList<>();
    private final OnUserChosenListener mChosenListener;

    public interface OnUserChosenListener {
        void onChosen(User user);
    }

    public PeopleToNewChannelAdapter(OnUserChosenListener listener, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mChosenListener = listener;
    }

    public void addUsers(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    public void addAlreadyAddedUsers(@Nullable List<User> alreadyAddedUsers) {
        mAlreadyAddedUsers = alreadyAddedUsers;
        notifyDataSetChanged();
    }

    public void addAlreadyAddedUser(User alreadyAddedUser) {
        mAlreadyAddedUsers.add(alreadyAddedUser);
    }

    public void removeAlreadyAddedUser(User removedUser) {
        mAlreadyAddedUsers.remove(removedUser);
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.people_new_channel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final boolean isUserAlreadyAdded = !(mAlreadyAddedUsers == null || mAlreadyAddedUsers.size() == 0 || !mAlreadyAddedUsers.contains(user));
        holder.mTvAddedMember.setChecked(isUserAlreadyAdded);
        holder.mTvAddedMember.setText(User.getDisplayableName(user));
        mImageLoader.displayCircularImage(user.getProfileImage(), holder.mAddedPeopleAvatar);
        holder.itemView.setOnClickListener(button -> {
            holder.mTvAddedMember.setChecked(!holder.mTvAddedMember.isChecked());
            mChosenListener.onChosen(user);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_pending_people_avatar)
        ImageView mAddedPeopleAvatar;

        @Bind(R.id.tv_added_member)
        CheckedTextView mTvAddedMember;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
