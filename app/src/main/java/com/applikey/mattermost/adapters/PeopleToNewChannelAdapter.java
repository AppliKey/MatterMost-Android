package com.applikey.mattermost.adapters;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PeopleToNewChannelAdapter extends RecyclerView.Adapter<PeopleToNewChannelAdapter.ViewHolder> {

    private final ImageLoader mImageLoader;
    private List<UserPendingInvitation> mUsers;
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

    public void setAllChecked(boolean checked) {
        for (UserPendingInvitation user : mUsers) {
            user.setInvited(checked);
        }
        notifyDataSetChanged();
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
        final UserPendingInvitation user = mUsers.get(position);
        holder.mAddedPeopleName.setText(User.getDisplayableName(user.getUser()));
        @DrawableRes final int iconRes = user.isInvited() ? R.drawable.ic_check : R.drawable.ic_add;
        holder.mAddPeopleImage.setBackgroundResource(iconRes);
        mImageLoader.displayCircularImage(user.getUser().getProfileImage(), holder.mAddedPeopleAvatar);
        holder.rootView.setOnClickListener(button -> {
            final boolean isUserInvited = user.isInvited();
            user.setInvited(!isUserInvited);
            @DrawableRes final int userButtonDrawableRes = isUserInvited ?  R.drawable.ic_add :R.drawable.ic_check;
            holder.mAddPeopleImage.setBackgroundResource(userButtonDrawableRes);
            mChosenListener.onChosen(user.getUser(), user.isInvited());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_pending_people_avatar)
        ImageView mAddedPeopleAvatar;

        @Bind(R.id.tv_added_people_name)
        TextView mAddedPeopleName;

        @Bind(R.id.add_people_image)
        View mAddPeopleImage;

        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
