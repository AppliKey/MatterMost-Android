package com.applikey.mattermost.models.channel;

public interface Command {
    void execute();
    void revert();
}
