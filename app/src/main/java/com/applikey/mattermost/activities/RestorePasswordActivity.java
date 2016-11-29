package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.RestorePasswordPresenter;
import com.applikey.mattermost.mvp.views.RestorePasswordView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestorePasswordActivity extends BaseMvpActivity implements RestorePasswordView {

    @Bind(R.id.et_login)
    EditText mEtLogin;

    @InjectPresenter
    RestorePasswordPresenter mPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, RestorePasswordActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.b_restore_password)
    void onRestoreClicked() {
        mPresenter.sendRestorePasswordRequest(mEtLogin.getText().toString());
    }

    @Override
    public void onPasswordRestoreSent() {
        Toast.makeText(this, getString(R.string.password_request_sent), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFailure(@Nullable String cause) {
        mEtLogin.setError(cause != null ? cause : getString(R.string.error_email_invalid));
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }
}
