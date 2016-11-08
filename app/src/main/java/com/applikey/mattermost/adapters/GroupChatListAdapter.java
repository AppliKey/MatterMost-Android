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
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

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

        mImageLoader.displayCircularImage("https://pixabay.com/static/uploads/photo/2014/03/29/09/17/cat-300572_960_720.jpg", vh.mIvFirst);
        mImageLoader.displayCircularImage("https://pixabay.com/static/uploads/photo/2014/03/29/09/17/cat-300572_960_720.jpg", vh.mIvSecond);
        mImageLoader.displayCircularImage("https://pixabay.com/static/uploads/photo/2014/03/29/09/17/cat-300572_960_720.jpg", vh.mIvThird);
        mImageLoader.displayCircularImage("https://pixabay.com/static/uploads/photo/2014/03/29/09/17/cat-300572_960_720.jpg", vh.mIvFourth);


        vh.getRoot().setTag(position);


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
