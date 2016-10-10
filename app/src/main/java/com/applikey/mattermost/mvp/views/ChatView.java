package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.post.Post;
import com.arellomobile.mvp.MvpView;

import java.util.List;

public interface ChatView extends MvpView {

    void displayData(List<Post> posts);

    void onFailure(Throwable cause);
}
