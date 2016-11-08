package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.mvp.presenters.SearchAllPresenter;
import com.applikey.mattermost.mvp.views.SearchAllView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;

// TODO: Generify search fragments
public class SearchAllFragment extends SearchFragment implements SearchAllView,
        SearchAdapter.ClickListener {

    @InjectPresenter
    SearchAllPresenter mPresenter;

    @Bind(R.id.rv_items)
    RecyclerView mRecycleView;

    public static SearchAllFragment newInstance() {
        return new SearchAllFragment();
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
        // TODO: IMPLEMENT
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    @Override
    public void startChatActivity(SearchItem item) {
        // TODO: IMPLEMENT
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }

}
