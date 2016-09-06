package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.LogInPresenter;
import com.applikey.mattermost.mvp.views.LogInView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends BaseMvpActivity implements LogInView {

    @Bind(R.id.et_login)
    EditText etLogin;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.b_authorize)
    Button bAuthorize;

    @InjectPresenter
    LogInPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.b_authorize)
    void onAuthorize() {
        showLoading();
        final String login = etLogin.getText().toString();
        final String password = etPassword.getText().toString();
        presenter.authorize(this, login, password);
    }

    @Override
    public void showLoading() {
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void onSuccessfulAuth() {
        hideLoading();
        Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnsuccessfulAuth(Throwable throwable) {
        hideLoading();
        Toast.makeText(this, "Authentication unsuccessful : " + throwable.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
}
