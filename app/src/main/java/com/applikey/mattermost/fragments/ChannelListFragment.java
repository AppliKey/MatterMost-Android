package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.ChannelListPresenter;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.views.TabBehavior;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class ChannelListFragment extends BaseChatListFragment {

    @InjectPresenter
    ChannelListPresenter mPresenter;

    public static BaseChatListFragment newInstance() {
        final BaseChatListFragment fragment = new ChannelListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, TabBehavior.CHANNELS.ordinal());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected ChatListPresenter getPresenter() {
        if (mPresenter == null) {
            throw new RuntimeException("Presenter is null");
        }
        return mPresenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.displayData();
    }

    @Override
    protected int getEmptyStateTextId() {
        return R.string.no_channels_available;
    }
}
