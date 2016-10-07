package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.Membership;

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

    public Observable<List<Channel>> listOpen() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PUBLIC.getRepresentation());
    }

    public Observable<List<Channel>> listClosed() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.PRIVATE.getRepresentation());
    }

    public Observable<List<Channel>> listDirect() {
        return mDb.listRealmObjectsFiltered(Channel.class, Channel.FIELD_NAME_TYPE,
                Channel.ChannelType.DIRECT.getRepresentation());
    }

    public Observable<List<Channel>> listAll() {
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
