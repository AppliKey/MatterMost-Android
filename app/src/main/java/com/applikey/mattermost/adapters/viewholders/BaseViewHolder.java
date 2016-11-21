package com.applikey.mattermost.adapters.viewholders;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    private String mCurrentUserId;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(View itemView, String userId) {
        this(itemView);
        mCurrentUserId = userId;
    }

    public View getContainer() {
        return itemView;
    }

    public abstract TextView getName();


    @CallSuper
    public void bind(Channel channel) {
        getName().setText(channel.getDisplayName());
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }
}
