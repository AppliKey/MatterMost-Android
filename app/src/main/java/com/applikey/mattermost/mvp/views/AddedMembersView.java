package com.applikey.mattermost.mvp.views;

import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.arellomobile.mvp.MvpView;

import java.util.List;


public interface AddedMembersView extends MvpView {

    void showAddedMembers(List<UserPendingInvitation> users);
}
