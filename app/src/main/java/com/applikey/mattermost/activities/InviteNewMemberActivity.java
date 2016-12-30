package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.InviteNewMemberPresenter;
import com.applikey.mattermost.mvp.views.InviteNewMemberView;
import com.applikey.mattermost.utils.TextViewUtils;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteNewMemberActivity extends BaseMvpActivity implements InviteNewMemberView {

    @BindView(R.id.tv_invite_info)
    TextView mTvInfo;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.et_email)
    EditText mEtEmail;

    @BindView(R.id.et_first_name)
    EditText mEtFirstName;

    @BindView(R.id.et_last_name)
    EditText mEtLastName;

    @InjectPresenter
    InviteNewMemberPresenter mPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, InviteNewMemberActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_new_member);

        ButterKnife.bind(this);

        initView();
    }

    @Override
    public void showEmptyEmailError() {
        hideLoadingDialog();

        mEtEmail.setError(getString(R.string.error_email_empty));
    }

    @Override
    public void showInvalidEmailError() {
        hideLoadingDialog();

        mEtEmail.setError(getString(R.string.error_email_invalid));
    }

    @Override
    public void onSuccessfulInvitationSent() {
        hideLoadingDialog();
        Toast.makeText(this, R.string.invitation_sent, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHttpError() {
        hideLoadingDialog();
        mEtEmail.setError(getString(R.string.error_invitation_sending_failed));
    }

    @Override
    public void showUserExistError() {
        hideLoadingDialog();
        showToast(R.string.error_user_invited);
    }

    @OnClick(R.id.b_invite_new_member)
    public void onInviteNewMemberClicked() {
        showLoadingDialog();

        final String email = mEtEmail.getText().toString();
        final String firstName = mEtFirstName.getText().toString();
        final String lastName = mEtLastName.getText().toString();

        mPresenter.inviteNewMember(email, firstName, lastName);
    }

    private void initView() {
        TextViewUtils.setHtmlText(mTvInfo, getString(R.string.invitation_info));

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.invite_new_member_capt);
        }

        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
