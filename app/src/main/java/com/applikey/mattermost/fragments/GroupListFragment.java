package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.BaseChatListAdapter;
import com.applikey.mattermost.adapters.channel.GroupChatListAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.GroupListPresenter;

import io.realm.RealmResults;

import static com.applikey.mattermost.views.TabBehavior.GROUPS;

public class GroupListFragment extends BaseChatListFragment {

    public static BaseChatListFragment newInstance() {
        final BaseChatListFragment fragment = new GroupListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, GROUPS.ordinal());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    BaseChatListPresenter providePresenter() {
        return new GroupListPresenter();
    }

    @Override
    protected BaseChatListAdapter getAdapter(RealmResults<Channel> channels) {
        return new GroupChatListAdapter(getContext(), channels, mImageLoader, mCurrentUserId);
    }

    @Override
    protected int getEmptyStateTextId() {
        return R.string.no_groups_available;
    }

    @Override
    public void onLoadAdditionalData(Channel channel) {
        super.onLoadAdditionalData(channel);
        mPresenter.getChatUsers(channel);
    }
}
