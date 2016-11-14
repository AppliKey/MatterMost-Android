package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.ChannelAdapter;
import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.SearchAllPresenter;
import com.applikey.mattermost.mvp.views.SearchAllView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;

// TODO: Generify search fragments
public class SearchAllFragment extends SearchFragment implements SearchAllView,
        ChannelAdapter.ClickListener {

    @InjectPresenter
    SearchAllPresenter mPresenter;

    @Bind(R.id.rv_items)
    RecyclerView mRecycleView;

    private SearchAdapter mAdapter;

    public static SearchAllFragment newInstance() {
        return new SearchAllFragment();
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
        // TODO: IMPLEMENT
    }

    @Override
    public void displayData(List<SearchItem> channels) {
        mAdapter.setDataSet(channels);
    }

    @Override
    public void startChatActivity(SearchItem item) {
        // TODO: IMPLEMENT
    }

    @Override
    public void clearData() {
        mAdapter.clear();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }

    private void initView() {
        mAdapter = new SearchAdapter(mImageLoader);
//        mAdapter.setOnClickListener(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mAdapter);
    }
}
