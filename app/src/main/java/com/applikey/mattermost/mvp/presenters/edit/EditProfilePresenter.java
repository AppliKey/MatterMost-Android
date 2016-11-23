package com.applikey.mattermost.mvp.presenters.edit;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.BasePresenter;
import com.applikey.mattermost.mvp.views.edit.EditProfileView;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;
import com.fuck_boilerplate.rx_paparazzo.RxPaparazzo;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
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
        //TODO add validation
        //TODO add progress

        mUser.setFirstName(userModel.getFirstName());
        mUser.setLastName(userModel.getLastName());
        mUser.setUsername(userModel.getUsername());
        mUser.setEmail(userModel.getEmail());

        uploadUser();
    }

    private void uploadUser() {
        final Subscription subscription = mApi.editUser(mUser)
                .flatMap(user -> mImage != null ? uploadImage() : Observable.just(user))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(user -> {
                    mUserStorage.saveUser(user);
                }, throwable -> getViewState().showError(mErrorHandler.getErrorMessage(throwable)));

        mSubscription.add(subscription);
    }

    private Observable<User> uploadImage() {
        MultipartBody.Part imagePart = MultipartBody.
                Part.createFormData(Api.MULTIPART_IMAGE_TAG, mImage.getName(),
                                    RequestBody.create(MediaType.parse(Constants.MIME_TYPE_IMAGE), mImage));

        return mApi.uploadImage(imagePart)
                .flatMap(v -> mApi.getMe());
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