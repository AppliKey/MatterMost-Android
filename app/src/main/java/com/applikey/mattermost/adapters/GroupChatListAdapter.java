package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

public class GroupChatListAdapter extends BaseChatListAdapter<GroupChatListAdapter.ViewHolder> {

    public GroupChatListAdapter(@NonNull Context context, RealmResults<Channel> data,
                                ImageLoader imageLoader, String currentUserId) {
        super(context, data, imageLoader, currentUserId);
    }

    @Override
    public GroupChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_group_chat, parent, false);

        final GroupChatListAdapter.ViewHolder vh = new GroupChatListAdapter.ViewHolder(v);
        vh.getRoot().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(GroupChatListAdapter.ViewHolder vh, int position) { //TODO
        super.onBindViewHolder(vh, position);

        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final Channel channel = data.get(position);

        final long lastPostAt = channel.getLastPostAt();

        vh.mChannelName.setText(channel.getDisplayName());

        final String messagePreview = getMessagePreview(channel, mContext);

        vh.mMessagePreview.setText(messagePreview);
        vh.mLastMessageTime.setText(
                TimeUtil.formatTimeOrDateOnlyChannel(lastPostAt != 0 ? lastPostAt :
                        channel.getCreatedAt()));

        //TODO replace to some base adapter for unread/group chats
        final Iterator<User> iterator = channel.getUsers().iterator();

        setGroupImage(iterator, vh.mIvFirst);
        setGroupImage(iterator, vh.mIvSecond);
        setGroupImage(iterator, vh.mIvThird);
        setGroupImage(iterator, vh.mIvFourth);

        vh.getRoot().setTag(position);
    }

    private void setGroupImage(Iterator<User> iterator, ImageView imageView) {
        if (iterator.hasNext()) {
            imageView.setVisibility(View.VISIBLE);
            mImageLoader.displayCircularImage(iterator.next().getProfileImage(), imageView);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_channel_name)
        TextView mChannelName;

        @Bind(R.id.tv_last_message_time)
        TextView mLastMessageTime;

        @Bind(R.id.tv_message_preview)
        TextView mMessagePreview;

        @Bind(R.id.container)
        LinearLayout mContainer;

        @Bind(R.id.iv_first)
        ImageView mIvFirst;

        @Bind(R.id.iv_second)
        ImageView mIvSecond;

        @Bind(R.id.iv_third)
        ImageView mIvThird;

        @Bind(R.id.iv_fourth)
        ImageView mIvFourth;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        View getRoot() {
            return itemView;
        }
    }
}
