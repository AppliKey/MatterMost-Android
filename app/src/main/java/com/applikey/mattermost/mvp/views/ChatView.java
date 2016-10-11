package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.post.Post;
import com.arellomobile.mvp.MvpView;

import java.util.List;

public interface ChatView extends MvpView {

    /**
     * Displays data from db on activity entering. Leaves loading bar at the top
     */
    void displayDataFirstTime(List<Post> posts);

    /**
     * Displays fetched or changed data. Hides loading bar at the top
     */
    void displayData(List<Post> posts);

    void onFailure(Throwable cause);
}
