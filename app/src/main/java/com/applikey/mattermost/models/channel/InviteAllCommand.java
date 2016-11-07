package com.applikey.mattermost.models.channel;

import com.applikey.mattermost.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class InviteAllCommand implements Command {

    private final List<User> mAlreadyAddedUsers;
    private final List<User> mTeamMembers;

    private List<User> mNotAddedUsersAtCallMoment;

    public InviteAllCommand(List<User> teamMembers, List<User> alreadyAddedUsers) {
        mTeamMembers = teamMembers;
        mAlreadyAddedUsers = alreadyAddedUsers;
    }

    @Override
    public void execute() {
        List<User> copyMembers = new ArrayList<>(mTeamMembers);
        copyMembers.removeAll(mAlreadyAddedUsers);
        mNotAddedUsersAtCallMoment = copyMembers;
        mAlreadyAddedUsers.addAll(mNotAddedUsersAtCallMoment);
    }

    @Override
    public void revert() {
        if (mNotAddedUsersAtCallMoment != null) {
            mAlreadyAddedUsers.removeAll(mNotAddedUsersAtCallMoment);
        }
    }
}
