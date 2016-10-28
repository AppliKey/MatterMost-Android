package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.user.User;
import com.arellomobile.mvp.MvpView;

import java.util.List;


public interface AddedMembersView extends MvpView {

    void showAddedMembers(List<User> users);
}
