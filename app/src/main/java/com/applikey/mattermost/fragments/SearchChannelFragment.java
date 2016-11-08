package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.ChannelAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.SearchChannelPresenter;
import com.applikey.mattermost.mvp.views.SearchChannelView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;

public class SearchChannelFragment extends SearchFragment implements SearchChannelView,
        ChannelAdapter.ClickListener {

    @InjectPresenter
    SearchChannelPresenter mPresenter;

    @Bind(R.id.rv_items)
    RecyclerView mRecycleView;

    private ChannelAdapter mChannelAdapter;

    public static SearchChannelFragment newInstance() {
        return new SearchChannelFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mPresenter.requestNotJoinedChannels();
        mPresenter.getData("");
    }

    @Override
    public void onItemClicked(Channel channel) {
        mPresenter.handleChannelClick(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    @Override
    public void displayData(List<Channel> channels) {
        mChannelAdapter.setDataSet(channels);
    }

    @Override
    public void startChatView(Channel channel) {
        // TODO: IMPLEMENT
    }

    @Override
    public void clearData() {
        mChannelAdapter.clear();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }

    private void initView() {
        mChannelAdapter = new ChannelAdapter(mImageLoader);
        mChannelAdapter.setOnClickListener(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mChannelAdapter);
    }
}
