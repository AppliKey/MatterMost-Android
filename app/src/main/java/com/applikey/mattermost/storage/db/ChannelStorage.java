package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.groups.Channel;
import com.applikey.mattermost.models.groups.ChannelResponse;
import com.applikey.mattermost.models.groups.Membership;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class ChannelStorage {

    @Inject
    Db mDb;

    public ChannelStorage() {
        App.getComponent().inject(this);
    }

    public Observable<List<Channel>> list() {
        return mDb.listRealmObjects(Channel.class);
    }

    public Observable<List<Membership>> listMembership() {
        return mDb.listRealmObjects(Membership.class);
    }

    public void saveChannelResponse(ChannelResponse response) {
        mDb.saveTransactionalWithRemoval(response.getChannels());
        mDb.saveTransactionalWithRemoval(response.getMembershipEntries().values());
    }
}
