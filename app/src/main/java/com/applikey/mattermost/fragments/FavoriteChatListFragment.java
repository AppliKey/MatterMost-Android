package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.presenters.FavoriteChatListPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

import static com.applikey.mattermost.views.TabBehavior.FAVOURITES;

public class FavoriteChatListFragment extends BaseChatListFragment {

    @InjectPresenter
    FavoriteChatListPresenter mPresenter;

    public static BaseChatListFragment newInstance() {
        final BaseChatListFragment fragment = new FavoriteChatListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, FAVOURITES.ordinal());

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
        return R.string.no_favorite_chats_available;
    }
}
