package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.BaseChatListAdapter;
import com.applikey.mattermost.adapters.ChatListAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.presenters.UnreadChatListPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

import io.realm.RealmResults;

import static com.applikey.mattermost.views.TabBehavior.UNREAD;

public class UnreadChatListFragment extends BaseChatListFragment {

    @InjectPresenter
    UnreadChatListPresenter mPresenter;

    public static UnreadChatListFragment newInstance() {
        final UnreadChatListFragment fragment = new UnreadChatListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, UNREAD.ordinal());

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
    protected BaseChatListAdapter getAdapter(RealmResults<Channel> channels) {
        return new ChatListAdapter(getContext(), channels, mImageLoader, mCurrentUserId);
    }

    @Override
    protected int getEmptyStateTextId() {
        return R.string.no_unread_chats_available;
    }
}
