package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.BaseChatListAdapter;
import com.applikey.mattermost.adapters.channel.GroupChatListAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.ChannelListPresenter;
import com.applikey.mattermost.views.TabBehavior;

import io.realm.RealmResults;

public class ChannelListFragment extends BaseChatListFragment {

    public static BaseChatListFragment newInstance() {
        final BaseChatListFragment fragment = new ChannelListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, TabBehavior.CHANNELS.ordinal());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    BaseChatListPresenter providePresenter() {
        return new ChannelListPresenter();
    }

    @Override
    protected BaseChatListAdapter getAdapter(RealmResults<Channel> channels) {
        return new GroupChatListAdapter(getContext(), channels, mImageLoader, mCurrentUserId);
    }

    @Override
    public void onLoadAdditionalData(Channel channel) {
        super.onLoadAdditionalData(channel);
        mPresenter.getChatUsers(channel);
    }

    @Override
    protected int getEmptyStateTextId() {
        return R.string.no_channels_available;
    }
}
