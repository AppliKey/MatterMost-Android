package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.presenters.DirectChatListPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class DirectChatListFragment extends BaseChatListFragment {

    @InjectPresenter
    DirectChatListPresenter mPresenter;

    public static DirectChatListFragment newInstance() {
        final DirectChatListFragment fragment = new DirectChatListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, TabBehavior.DIRECT.ordinal());

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
    protected int getEmptyStateTextId() {
        return R.string.no_direct_chats_available;
    }
}
