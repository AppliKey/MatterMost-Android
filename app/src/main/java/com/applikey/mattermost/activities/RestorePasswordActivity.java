package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    public void onPasswordRestoreSent() {
        Toast.makeText(this, getString(R.string.password_request_sent), Toast.LENGTH_SHORT).show();

        hideLoadingDialog();
        finish();
    }

    @Override
    public void onFailure(String message) {
        hideLoadingDialog();
        mEtLogin.setError(message);
    }

    @OnClick(R.id.back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.b_restore_password)
    void onRestoreClicked() {
        showLoadingDialog();
        mPresenter.sendRestorePasswordRequest(mEtLogin.getText().toString());
    }
}
