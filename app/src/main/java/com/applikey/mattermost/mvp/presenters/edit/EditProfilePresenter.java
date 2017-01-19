package com.applikey.mattermost.mvp.presenters.edit;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.web.RequestError;
import com.applikey.mattermost.mvp.presenters.BasePresenter;
import com.applikey.mattermost.mvp.views.edit.EditProfileView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.rx.RxUtils;
import com.applikey.mattermost.utils.validation.ValidationUtil;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.MattermostErrorIds;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.InjectViewState;
import com.fuck_boilerplate.rx_paparazzo.RxPaparazzo;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//TODO add avatar upload logic
@InjectViewState
public class EditProfilePresenter extends BasePresenter<EditProfileView> {

    @Inject Api mApi;
    @Inject UserStorage mUserStorage;
    @Inject ErrorHandler mErrorHandler;
    @Inject Prefs mPrefs;
    @Inject ImageLoader mImageLoader;

    @Nullable private File mImage;

    private User mUser;

    public EditProfilePresenter() {
        App.getUserComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        final EditProfileView view = getViewState();
        final Subscription subscription =
                mUserStorage.getMe()
                        .doOnSuccess(user -> mUser = user)
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
                    mImage = new File(response.data().get(0));
                    view.onImageChosen(mImage);
                }, mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    public void commitChanges(UserModel userModel) {
        final EditProfileView view = getViewState();

        final String firstName = userModel.getFirstName().trim();
        final String lastName = userModel.getFirstName().trim();
        final String email = userModel.getEmail().trim();
        final String username = userModel.getUsername().trim();

        final Subscription subscription =
                Single.zip(ValidationUtil.validateEmail(email)
                                   .doOnSuccess(emailResult -> {
                                       if (!emailResult) {
                                           view.showEmailValidationError(null);
                                       }
                                   }),
                           ValidationUtil.validateUsername(username)
                                   .doOnSuccess(usernameResult -> {
                                       if (!usernameResult) {
                                           view.showUsernameValidationError(null);
                                       }
                                   }), (emailResult, usernameResult) -> emailResult && usernameResult)
                        .toObservable()
                        .filter(result -> result)
                        .doOnNext(ignored -> {
                            mUser.setFirstName(firstName);
                            mUser.setLastName(lastName);
                            mUser.setUsername(username);
                            mUser.setEmail(email);
                        })
                        .subscribe(ignored -> uploadUser(), mErrorHandler::handleError);

        mSubscription.add(subscription);
    }

    private void uploadUser() {
        final EditProfileView view = getViewState();

        final Subscription subscription = mApi.editUser(mUser)
                .flatMap(user -> mImage != null ? uploadImage() : Single.just(user))
                .flatMap(o -> mApi.getMe())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(RxUtils.applyProgressSingle(view::showLoading, view::hideLoading))
                .subscribe(mUserStorage::save, throwable -> {
                    final RequestError requestError = mErrorHandler.getRequestError(throwable);
                    if (requestError != null) {

                        if (requestError.getId().equals(MattermostErrorIds.USERNAME_INVALID)) {
                            view.showUsernameValidationError(requestError.getMessage());
                        }
                        if (requestError.getId().equals(MattermostErrorIds.EMAIL_INVALID)) {
                            view.showEmailValidationError(requestError.getMessage());
                        }
                    } else {
                        mErrorHandler.handleError(throwable);
                    }
                });

        mSubscription.add(subscription);
    }

    private Single<Void> uploadImage() {
        final MultipartBody.Part imagePart = MultipartBody.
                Part.createFormData(Api.MULTIPART_IMAGE_TAG, mImage.getName(),
                                    RequestBody.create(MediaType.parse(Constants.MIME_TYPE_IMAGE), mImage));

        return mApi.uploadImage(imagePart)
                .doOnSuccess(ignored -> mImageLoader.dropMemoryCache())
                .doOnSuccess(ignored -> mImageLoader.invalidateCache(mUser.getProfileImage()));
    }

    public static class UserModel {

        private String firstName;
        private String lastName;
        private String username;
        private String email;

        public UserModel(String firstName, String lastName, String username, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }
}
