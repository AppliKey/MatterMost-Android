package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class InvitedUsersManager {

    private final List<User> mTeamMembers;
    private List<User> mInvitedUsers;
    private Command mInviteAllCommand;
    private final OnInvitedListener mOnInvitedListener;

    public void operateWithUser(User user) {
        if (mInvitedUsers.contains(user)) {
            revertInvite(user);
        } else {
            invite(user);
        }
    }

    public interface OnInvitedListener {
        void onInvited(User user);

        void onInvitedAll(List<User> users);

        void onRevertedAll(List<User> users);

        void onRevertInvite(User user);
    }

    public InvitedUsersManager(OnInvitedListener listener, List<User> teamMembers) {
        mTeamMembers = teamMembers;
        mInvitedUsers = new ArrayList<>();
        mOnInvitedListener = listener;
    }

    private void invite(User user) {
        mInvitedUsers.add(user);
        mOnInvitedListener.onInvited(user);
    }

    private void revertInvite(User user) {
        mInvitedUsers.remove(user);
        mOnInvitedListener.onRevertInvite(user);
    }

    public void inviteAll() {
        mInviteAllCommand = new InviteAllCommand(mTeamMembers, mInvitedUsers);
        mInviteAllCommand.execute();
        mOnInvitedListener.onInvitedAll(mInvitedUsers);
    }

    public void revertInvitingAll() {
        if (mInviteAllCommand != null) {
            mInviteAllCommand.revert();
            mOnInvitedListener.onRevertedAll(mInvitedUsers);
        }
    }

    public List<User> getInvitedUsers() {
        return mInvitedUsers;
    }

    public List<User> getTeamMembers() {
        return mTeamMembers;
    }

    public void setAlreadyInvitedUsers(List<User> alreadyInvitedUsers) {
        mInvitedUsers = alreadyInvitedUsers;
        mOnInvitedListener.onInvitedAll(mInvitedUsers);
    }
}
