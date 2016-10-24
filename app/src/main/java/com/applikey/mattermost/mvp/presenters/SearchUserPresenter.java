package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.views.SearchUserView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Anatoliy Chub
 */
@InjectViewState
public class SearchUserPresenter extends BasePresenter<SearchUserView> {

    @Inject
    UserStorage mUserStorage;

    public SearchUserPresenter() {
        App.getComponent().inject(this);
    }

    public void getData(String text) {
        final SearchUserView view = getViewState();
        mSubscription.add(
                mUserStorage.searchUsers(text)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::displayData, ErrorHandler::handleError));
    }

    public void handleUserClick(User user){

    }

}
