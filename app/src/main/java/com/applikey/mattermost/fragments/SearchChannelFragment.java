package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.view.View;

import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.SearchChannelPresenter;
import com.applikey.mattermost.mvp.views.SearchChannelView;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class SearchChannelFragment extends SearchFragment implements SearchChannelView,
        SearchAdapter.ClickListener {

    @InjectPresenter
    SearchChannelPresenter mPresenter;

    public static SearchChannelFragment newInstance() {
        return new SearchChannelFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(this);
        mPresenter.requestNotJoinedChannels();
        mPresenter.getData("");
    }

    @Override
    public void onItemClicked(SearchItem item) {
        mPresenter.handleChannelClick((Channel) item);
    }

    @Override
    public void displayData(List<Channel> channels) {
        mChannelAdapter.setDataSet(channels);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    @Override
    public void startChatView(Channel channel) {
        // TODO: IMPLEMENT
    }

}
