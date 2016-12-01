package com.applikey.mattermost.adapters.viewholders;

import android.text.TextUtils;
import android.view.View;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.adapters.channel.viewholder.GroupChatListViewHolder;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.utils.RecyclerItemClickListener;
import com.applikey.mattermost.utils.SpanUtils;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.ButterKnife;

public class MessageChannelViewHolder extends GroupChatListViewHolder {


    public MessageChannelViewHolder(View itemView, String userId) {
        super(itemView, userId);

        ButterKnife.bind(this, itemView);
    }

    public void bind(ImageLoader imageLoader, RecyclerItemClickListener.OnItemClickListener listener, Message message, String searchText) {
        super.bind(imageLoader, message.getChannel());

        final Post post = message.getPost();
        final String messageText = post.getMessage();

        final CharSequence messageWithAuthorText = TextUtils.concat(getAuthorPrefix(itemView.getContext(), message), Constants.SPACE,
                                                               SpanUtils.createSpannableBoldString(messageText, searchText));

        getMessagePreview().setText(messageWithAuthorText);
        setClickListener(listener);
    }

}
