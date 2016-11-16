package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.view.View;

import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.mvp.presenters.SearchAllPresenter;
import com.applikey.mattermost.mvp.views.SearchAllView;
import com.arellomobile.mvp.presenter.InjectPresenter;

// TODO: Generify search fragments
public class SearchAllFragment extends SearchFragment implements SearchAllView,
        SearchAdapter.ClickListener {

    @InjectPresenter
    SearchAllPresenter mPresenter;

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
        mPresenter.handleItemClick(item);
    }


}
