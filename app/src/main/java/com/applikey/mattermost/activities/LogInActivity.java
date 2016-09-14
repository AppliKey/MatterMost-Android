package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.TeamAdapter;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.presenters.LogInPresenter;
import com.applikey.mattermost.mvp.views.LogInView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class LogInActivity extends BaseMvpActivity implements LogInView {

    private static final String TAG = "LogInActivity";

    @Bind(R.id.sp_team)
    Spinner spTeam;
    @Bind(R.id.et_login)
    EditText etLogin;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.b_authorize)
    Button bAuthorize;

    @InjectPresenter
    LogInPresenter presenter;

    private TeamAdapter mTeamAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);

        ButterKnife.bind(this);
        mTeamAdapter = new TeamAdapter(this);
        spTeam.setAdapter(mTeamAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        initUi();
    }

    @Override
    protected void onDestroy() {
        presenter.unSubscribe();

        super.onDestroy();
    }

    @OnClick(R.id.b_authorize)
    void onAuthorize() {
        showLoading();
        final String login = etLogin.getText().toString();
        final String password = etPassword.getText().toString();

        final Team selectedTeam = (Team) spTeam.getSelectedItem();

        presenter.authorize(this, selectedTeam.getId(), login, password);
    }

    @Override
    public void displayTeams(List<Team> teams) {
        mTeamAdapter.addAll(teams);
        mTeamAdapter.notifyDataSetChanged();
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

    private void initUi() {
        presenter.getInitialData();
    }
}
