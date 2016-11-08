package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

import java.util.List;

public class InviteSingleCommand implements Command {

    private final List<User> mAlreadyAdded;
    private final User mUserPendingInvitation;

    public InviteSingleCommand(List<User> alreadyAdded, User userPendingInvitation) {
        mAlreadyAdded = alreadyAdded;
        mUserPendingInvitation = userPendingInvitation;
    }

    @Override
    public void execute() {
        mAlreadyAdded.add(mUserPendingInvitation);
    }

    @Override
    public void revert() {
        mAlreadyAdded.remove(mUserPendingInvitation);
    }
}
