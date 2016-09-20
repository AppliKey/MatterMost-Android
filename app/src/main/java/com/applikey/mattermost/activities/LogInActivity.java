package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.presenters.LogInPresenter;
import com.applikey.mattermost.mvp.views.LogInView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends BaseMvpActivity implements LogInView {

    private static final String TAG = "LogInActivity";

    @Bind(R.id.et_login)
    EditText etLogin;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.b_authorize)
    Button bAuthorize;

    @Bind(R.id.back)
    View vBack;

    @InjectPresenter
    LogInPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        mPresenter.unSubscribe();

        super.onDestroy();
    }

    @OnClick(R.id.b_authorize)
    void onAuthorize() {
        showLoading();
        final String login = etLogin.getText().toString();
        final String password = etPassword.getText().toString();

        mPresenter.authorize(this, login, password);
    }

    @OnClick(R.id.back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.b_restore_password)
    void onRestoreClicked() {
        startActivity(RestorePasswordActivity.getIntent(this));
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
        loadTeams();
    }

    @Override
    public void onUnsuccessfulAuth(String message) {
        hideLoading();
        etPassword.setError(message);
    }

    @Override
    public void onTeamsRetrieved(Map<String, Team> teams) {
        hideLoading();

        if (teams.size() == 0) {
            etPassword.setError(getResources().getString(R.string.no_teams_received));
            return;
        }

        startActivity(ChooseTeamActivity.getIntent(this));
    }

    @Override
    public void onTeamsReceiveFailed(Throwable cause) {
        hideLoading();
        etLogin.setError(cause.getMessage());
    }

    private void loadTeams() {
        mPresenter.loadTeams();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, LogInActivity.class);
    }
}
