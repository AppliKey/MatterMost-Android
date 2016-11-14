package com.applikey.mattermost.adapters.channel.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.Bind;

public class UserChatListViewHolder extends BaseChatListViewHolder {

    @Bind(R.id.iv_preview_image)
    ImageView mPreviewImage;

    @Bind(R.id.iv_channel_icon)
    ImageView mChannelIcon;

    @Bind(R.id.iv_status_bg)
    ImageView mStatusBackground;

    @Bind(R.id.iv_status)
    ImageView mStatus;

    public UserChatListViewHolder(View itemView, String userId) {
        super(itemView, userId);
    }

    public ImageView getStatus() {
        return mStatus;
    }

    @Override
    public void bind(ImageLoader imageLoader, Channel channel) {
        super.bind(imageLoader, channel);

        setStatusIcon(channel);
        setChannelIcon(imageLoader, channel);
        setChannelIconVisibility(channel);
    }

    private void setChannelIconVisibility(Channel channel) {
        final String type = channel.getType();
        if (Channel.ChannelType.PRIVATE.getRepresentation().equals(type)) {
            mChannelIcon.setVisibility(View.VISIBLE);
        } else {
            mChannelIcon.setVisibility(View.GONE);
        }
    }

    private void setChannelIcon(ImageLoader imageLoader, Channel element) {
        final User member = element.getDirectCollocutor();
        final String previewImagePath = member != null ? member.getProfileImage() : null;
        final ImageView previewImage = mPreviewImage;
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            imageLoader.displayCircularImage(previewImagePath, previewImage);
        } else {
            previewImage.setImageResource(R.drawable.no_resource);
        }
    }

    private void setStatusIcon(Channel channel) {
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(channel.getType())) {
            final User member = channel.getDirectCollocutor();
            final User.Status status = member != null ? User.Status.from(member.getStatus()) : null;
            if (status != null) {
                getStatus().setImageResource(status.getDrawableId());
            }
            mStatusBackground.setVisibility(View.VISIBLE);
            getStatus().setVisibility(View.VISIBLE);
        } else {
            mStatusBackground.setVisibility(View.GONE);
            getStatus().setVisibility(View.GONE);
        }
    }
}
