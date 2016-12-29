package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.commands.Invite;
import com.applikey.mattermost.models.commands.InviteNewMembersRequest;
import com.applikey.mattermost.mvp.views.InviteNewMemberView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class InviteNewMemberPresenter extends BasePresenter<InviteNewMemberView> {

    @Inject
    Api mApi;

    @Inject
    Prefs mPrefs;

    @Inject
    UserStorage mUserStorage;

    @Inject
    ErrorHandler mErrorHandler;

    public InviteNewMemberPresenter() {
        App.getUserComponent().inject(this);
    }

    public void inviteNewMember(String email, String firstName, String lastName) {
        if (email == null || email.trim().isEmpty()) {
            getViewState().showEmptyEmailError();
            return;
        }
        if (!StringUtil.isEmail(email)) {
            getViewState().showInvalidEmailError();
            return;
        }

        final Invite invite = new Invite(email, firstName, lastName);
        final InviteNewMembersRequest request = new InviteNewMembersRequest(invite);

        mUserStorage.getUserByEmail(email)
                .compose(bindToLifecycle())
                .doOnNext(user -> {
                    if (user != null) {
                        getViewState().showUserExistError();
                    }
                })
                .filter(user -> user == null)
                .observeOn(Schedulers.io())
                .flatMap(user -> mApi.inviteNewMember(mPrefs.getCurrentTeamId(), request).toObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((v) -> getViewState().onSuccessfulInvitationSent(), this::handleError);
    }

    private void handleError(Throwable throwable) {
        getViewState().onHttpError();
        mErrorHandler.handleError(throwable);
    }
}
