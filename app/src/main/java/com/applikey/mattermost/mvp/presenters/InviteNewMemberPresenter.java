package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.commands.Invite;
import com.applikey.mattermost.models.commands.InviteNewMembersRequest;
import com.applikey.mattermost.mvp.views.InviteNewMemberView;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class InviteNewMemberPresenter extends BasePresenter<InviteNewMemberView> {

    @Inject
    Api mApi;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    ErrorHandler mErrorHandler;

    public InviteNewMemberPresenter() {
        App.getComponent().inject(this);
    }

    public void inviteNewMember(String email, String firstName, String lastName) {
        final InviteNewMemberView view = getViewState();
        if (email == null || email.trim().isEmpty()) {
            view.showEmptyEmailError();
            return;
        }
        if (!StringUtil.isEmail(email)) {
            view.showInvalidEmailError();
        }

        final Invite invite = new Invite(email, firstName, lastName);
        final InviteNewMembersRequest request = new InviteNewMembersRequest(invite);
        final Subscription subscription = mTeamStorage.getChosenTeam()
                .first()
                .flatMap(team -> mApi.inviteNewMember(team.getId(), request)
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((v) -> view.onSuccessfulInvitationSent(),
                           this::handleError);

        mSubscription.add(subscription);
    }

    private void handleError(Throwable throwable) {
        getViewState().onHttpError();
        mErrorHandler.handleError(throwable);
    }
}
