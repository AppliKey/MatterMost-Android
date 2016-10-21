package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class PeopleToNewChannelAdapter extends RealmRecyclerViewAdapter<User, PeopleToNewChannelAdapter.VH> {

    private final ImageLoader mImageLoader;
    private final OnUserChosenListener mChosenListener;

    public interface OnUserChosenListener {
        void onChosen(User user);
    }

    public PeopleToNewChannelAdapter(@NonNull Context context, @Nullable RealmResults<User> data, OnUserChosenListener listener, ImageLoader imageLoader, boolean autoUpdate) {
        super(context, data, autoUpdate);
        mImageLoader = imageLoader;
        mChosenListener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.people_new_channel_item, parent, false);
        return new VH(view);

    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final User user = getItem(position);
        holder.mAddedPeopleName.setText(User.getDisplayableName(user));
        mImageLoader.displayCircularImage(user.getProfileImage(), holder.mAddedPeopleAvatar);
        holder.mAddPeopleButton.setOnClickListener(button -> mChosenListener.onChosen(getItem(position)));
    }

    static class VH extends RecyclerView.ViewHolder {

        @Bind(R.id.added_people_avatar)
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
