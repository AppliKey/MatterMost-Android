package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.view.View;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.mvp.presenters.SearchMessagePresenter;
import com.applikey.mattermost.mvp.views.SearchMessageView;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class SearchMessageFragment extends SearchFragment implements SearchMessageView,
        SearchAdapter.ClickListener {

    @InjectPresenter
    SearchMessagePresenter mPresenter;

    public static SearchMessageFragment newInstance() {
        return new SearchMessageFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(this);
        mPresenter.requestNotJoinedChannels();
        mPresenter.getData(Constants.EMPTY_STRING);
    }

    @Override
    public void onItemClicked(SearchItem item) {
        mPresenter.handleItemClick(item);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }

}