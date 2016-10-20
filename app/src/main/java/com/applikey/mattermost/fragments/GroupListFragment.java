package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.presenters.GroupListPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class GroupListFragment extends BaseChatListFragment {

    @InjectPresenter
    GroupListPresenter mPresenter;

    public static BaseChatListFragment newInstance() {
        final BaseChatListFragment fragment = new GroupListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, TabBehavior.GROUPS.ordinal());

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
        return R.string.no_groups_available;
    }
}