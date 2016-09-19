package com.applikey.mattermost.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.RestorePasswordPresenter;
import com.applikey.mattermost.mvp.views.RestorePasswordView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestorePasswordActivity extends BaseMvpActivity implements RestorePasswordView {

    @Bind(R.id.et_login)
    EditText etLogin;

    @InjectPresenter
    RestorePasswordPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onBack() {
        finish();
    }

    @OnClick(R.id.b_restore_password)
    public void onRestoreClicked() {
        showLoadingDialog();
        mPresenter.sendRestorePasswordRequest(etLogin.getText().toString());
    }

    @Override
    public void onPasswordRestoreSent() {
        hideLoadingDialog();
        finish();
    }

    @Override
    public void onFailure(String message) {
        hideLoadingDialog();
        etLogin.setError(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }
}
