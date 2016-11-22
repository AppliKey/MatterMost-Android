package com.applikey.mattermost.mvp.presenters.edit;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.applikey.mattermost.App;
import com.applikey.mattermost.mvp.presenters.BasePresenter;
import com.applikey.mattermost.mvp.views.edit.EditProfileView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;
import com.fuck_boilerplate.rx_paparazzo.RxPaparazzo;

import javax.inject.Inject;

import rx.Subscription;

@InjectViewState
public class EditProfilePresenter extends BasePresenter<EditProfileView> {

    @Inject Api mApi;
    @Inject UserStorage mUserStorage;
    @Inject ErrorHandler mErrorHandler;

    @Nullable
    private String mImageUri;

    public EditProfilePresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        final EditProfileView view = getViewState();
        final Subscription subscription =
                mUserStorage.getMe()
                        .subscribe(view::onUserAttached, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    public void chooseImage(Activity activity) {
        final EditProfileView view = getViewState();
        final Subscription subscription = RxPaparazzo.takeImages(activity)
                .usingGallery()
                .subscribe(response -> {
                    if (response.resultCode() != Activity.RESULT_OK) {
                        return;
                    }

                    mImageUri = response.data().get(0);
                    view.onImageChosen(mImageUri);
                });

        mSubscription.add(subscription);
    }
}
