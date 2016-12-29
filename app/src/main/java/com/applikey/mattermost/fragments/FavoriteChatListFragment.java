package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.BaseChatListAdapter;
import com.applikey.mattermost.adapters.channel.FavoriteChatListAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.BaseChatListPresenter;
import com.applikey.mattermost.mvp.presenters.FavoriteChatListPresenter;

import io.realm.RealmResults;

import static com.applikey.mattermost.views.TabBehavior.FAVOURITES;

public class FavoriteChatListFragment extends BaseChatListFragment {

    private static final String TAG = "FavoriteChatListFragment";

    public static BaseChatListFragment newInstance() {
        final BaseChatListFragment fragment = new FavoriteChatListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BaseChatListFragment.BEHAVIOR_KEY, FAVOURITES.ordinal());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    BaseChatListPresenter providePresenter() {
        return new FavoriteChatListPresenter();
    }

    @Override
    protected int getEmptyStateTextId() {
        return R.string.no_favorite_chats_available;
    }

    @Override
    protected BaseChatListAdapter getAdapter(RealmResults<Channel> channels) {
        return new FavoriteChatListAdapter(getContext(), channels, mImageLoader, mCurrentUserId);
    }

    @Override
    public void onLoadAdditionalData(Channel channel) {
        super.onLoadAdditionalData(channel);
        mPresenter.getChatUsers(channel);
    }
}
