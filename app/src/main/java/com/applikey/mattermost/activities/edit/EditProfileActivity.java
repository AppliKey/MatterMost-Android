package com.applikey.mattermost.activities.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.BaseMvpActivity;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.edit.EditProfilePresenter;
import com.applikey.mattermost.mvp.views.edit.EditProfileView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditProfileActivity extends BaseMvpActivity implements EditProfileView {

    @InjectPresenter EditProfilePresenter mPresenter;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.iv_avatar) ImageView mIvAvatar;
    @Bind(R.id.et_first_name) EditText mEtFirstName;
    @Bind(R.id.et_last_name) EditText mEtLastName;
    @Bind(R.id.et_username) EditText mEtUsername;
    @Bind(R.id.et_email) EditText mEtEmail;

    public static Intent getIntent(Context context) {
        return new Intent(context, EditProfileActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_apply:
                EditProfilePresenter.UserModel userModel = new EditProfilePresenter.UserModel(
                        mEtFirstName.getText().toString(),
                        mEtLastName.getText().toString(),
                        mEtUsername.getText().toString(),
                        mEtEmail.getText().toString());
                mPresenter.commitChanges(userModel);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserAttached(User user) {
        mImageLoader.displayCircularImage(user.getProfileImage(), mIvAvatar);
        mEtFirstName.setText(user.getFirstName());
        mEtLastName.setText(user.getLastName());
        mEtUsername.setText(user.getUsername());
        mEtEmail.setText(user.getEmail());
    }

    @Override
    public void onImageChosen(File file) {
        mImageLoader.displayCircularImage(file, mIvAvatar);
    }

    @Override
    public void showUsernameValidationError(@Nullable String cause) {
        mEtUsername.setError(cause != null ? cause : getString(R.string.error_username_invalid));
    }

    @Override
    public void showEmailValidationError(@Nullable String cause) {
        mEtEmail.setError(cause != null ? cause : getString(R.string.error_email_invalid));
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    private void initListeners() {
        mIvAvatar.setOnClickListener(v -> mPresenter.chooseImage(this));
    }
}
