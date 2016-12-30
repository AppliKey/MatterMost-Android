package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.BaseChatListAdapter;
import com.applikey.mattermost.adapters.channel.UserChatListAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.DirectChatListPresenter;

import io.realm.RealmResults;

import static com.applikey.mattermost.views.TabBehavior.DIRECT;

public class DirectChatListFragment extends BaseChatListFragment {

    public static DirectChatListFragment newInstance() {
        final DirectChatListFragment fragment = new DirectChatListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, DIRECT.ordinal());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    BaseChatListPresenter providePresenter() {
        return new DirectChatListPresenter();
    }

    @Override
    protected BaseChatListAdapter getAdapter(RealmResults<Channel> channels) {
        return new UserChatListAdapter(getContext(), channels, mImageLoader, mCurrentUserId);
    }

    @Override
    protected int getEmptyStateTextId() {
        return R.string.no_direct_chats_available;
    }
}
