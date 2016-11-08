package com.applikey.mattermost.adapters;

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
import timber.log.Timber;

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

    public void addAlreadyAddedUsers(List<User> alreadyAddedUsers) {
        mAlreadyAddedUsers = new ArrayList<>(alreadyAddedUsers);
        notifyDataSetChanged();
    }

    public void addAlreadyAddedUser(User alreadyAddedUser) {
        mAlreadyAddedUsers.add(alreadyAddedUser);
        notifyItemChanged(mUsers.indexOf(alreadyAddedUser));
    }

    public void removeAlreadyAddedUser(User removedUser) {
        mAlreadyAddedUsers.remove(removedUser);
        notifyItemChanged(mUsers.indexOf(removedUser));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.people_new_channel_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(button -> {
            viewHolder.mTvAddedMember.setChecked(!viewHolder.mTvAddedMember.isChecked());
            final int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                mChosenListener.onChosen(mUsers.get(adapterPosition));
            }
        });
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        final boolean isUserAlreadyAdded = mAlreadyAddedUsers.contains(user);
        Timber.d("is already added: %b", isUserAlreadyAdded);
        holder.mTvAddedMember.setChecked(isUserAlreadyAdded);
        holder.mTvAddedMember.setText(User.getDisplayableName(user));
        mImageLoader.displayCircularImage(user.getProfileImage(), holder.mAddedPeopleAvatar);
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
