package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.views.InviteNewMemberView;
import com.applikey.mattermost.utils.TextViewUtils;

public class InviteNewMemberActivity extends BaseMvpActivity implements InviteNewMemberView {

    @Bind(R.id.tv_invite_info)
    TextView mTvInfo;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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

    private void initView() {
        TextViewUtils.setHtmlText(mTvInfo, getString(R.string.invitation_info));

        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.invite_new_member_capt));
        }
    }
}
