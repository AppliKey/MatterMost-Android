package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.post.Message;
import com.arellomobile.mvp.MvpView;

public interface MessageDetailsView extends MvpView {

    public void initView(Message message);
}
