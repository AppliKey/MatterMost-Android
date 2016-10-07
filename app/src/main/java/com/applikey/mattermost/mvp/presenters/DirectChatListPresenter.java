package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelsWithMetadata;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@InjectViewState
public class DirectChatListPresenter extends BaseChatListPresenter {

    @Inject
    UserStorage mUserStorage;

    public DirectChatListPresenter() {
        super();
        App.getComponent().inject(this);
    }

    public void getInitialData() {
        final ChatListView view = getViewState();
        // TODO Introduce DTO object
        mSubscription.add(
                Observable.zip(
                        mChannelStorage.listDirect(),
                        mChannelStorage.listMembership(),
                        mUserStorage.listDirectProfiles(),
                        this::transform)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayInitialData, ErrorHandler::handleError));
    }

    private ChannelsWithMetadata transform(List<Channel> channels,
                                           List<Membership> memberships,
                                           List<User> directContacts) {
        return transform(channels, memberships, directContacts, false);
    }
}
