package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private static final String TAG = SearchChannelFragment.class.getSimpleName();

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
        Log.d(TAG, "onViewCreated: ");
        mPresenter.requestNotJoinedChannels();
        mPresenter.getData("");
    }

    @Override
    public void onItemClicked(Channel channel) {
        Log.d(TAG, "onItemClicked: ");
        mPresenter.handleChannelClick(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    @Override
    public void displayData(List<Channel> channels) {
        Log.d(TAG, "displayData: ");
        mChannelAdapter.setDataSet(channels);
    }

    @Override
    public void startChatView(Channel channel) {
        // TODO: 07.11.16 IMPLEMENT THIS
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
