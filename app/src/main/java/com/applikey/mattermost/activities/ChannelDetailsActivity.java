package com.applikey.mattermost.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.ChannelDetailsPresenter;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.devspark.robototextview.widget.RobotoTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelDetailsActivity extends BaseMvpActivity implements ChannelDetailsView {

    private static final String CHANNEL_ID_KEY = "channel_id";

    @InjectPresenter
    ChannelDetailsPresenter mPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.channel_name)
    RobotoTextView mChannelName;
    @Bind(R.id.channel_description)
    RobotoTextView mChannelDescription;
    @Bind(R.id.added_people_layout)
    AddedPeopleLayout mAddedPeopleLayout;

    public static Intent getIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelDetailsActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putString(CHANNEL_ID_KEY, channel.getId());
        intent.putExtras(bundle);

        return intent;
    }

    @OnClick(R.id.b_invite_member)
    public void onInviteMemberClick() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_details);
        ButterKnife.bind(this);
        initViews();
        initParameters();
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setTitle(null);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        final String channelId = extras.getString(CHANNEL_ID_KEY);
        mPresenter.getInitialData(channelId);
    }

    @Override
    public void showBaseDetails(Channel channel) {
        mChannelName.setText(getString(R.string.channel_display_name_format, channel.getDisplayName()));
        mChannelDescription.setText(channel.getPurpose());
    }

    @Override
    public void showMembers(List<User> users) {

    }
}
