package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.NotJoinedChannelsAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.FindMoreChannelsPresenter;
import com.applikey.mattermost.mvp.views.FindMoreChannelsView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FindMoreChannelsActivity extends BaseMvpActivity implements FindMoreChannelsView,
                                                                         NotJoinedChannelsAdapter.OnNotJoinedChannelClick {
    @Bind(R.id.rv_more_channels)
    RecyclerView mRvNotJoinedChannels;

    @InjectPresenter
    FindMoreChannelsPresenter mPresenter;

    private NotJoinedChannelsAdapter mAdapter;

    public static Intent getIntent(Context context) {
        return new Intent(context, FindMoreChannelsActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_more_channels);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void showNotJoinedChannels(List<Channel> notJoinedChannels) {
        mAdapter.setChannels(notJoinedChannels);
    }

    @Override
    public void onChannelClick(Channel channel) {
        Timber.d("onChannelClick, channel: %s", channel.getDisplayName());
    }

    private void initView() {
        mAdapter = new NotJoinedChannelsAdapter(this);
        mRvNotJoinedChannels.setAdapter(mAdapter);
    }

}
