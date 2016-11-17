package com.applikey.mattermost.adapters.viewholders;

import android.view.View;

import com.applikey.mattermost.adapters.channel.viewholder.GroupChatListViewHolder;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.utils.RecyclerItemClickListener;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.ButterKnife;

public class MessageChannelViewHolder extends GroupChatListViewHolder {

    public MessageChannelViewHolder(View itemView, String userId) {
        super(itemView, userId);

        ButterKnife.bind(this, itemView);
    }

    public void bind(ImageLoader imageLoader, RecyclerItemClickListener.OnItemClickListener listener, Message message) {
        super.bind(imageLoader, message.getChannel());

        final Post post = message.getPost();

        final String messageText = getAuthorPrefix(itemView.getContext(), message)
                + post.getMessage();

        getMessagePreview().setText(messageText);
        setClickListener(listener);
    }

}
