package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.UserAdapter;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.SearchUserPresenter;
import com.applikey.mattermost.mvp.views.SearchUserView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

public class SearchUserFragment extends SearchFragment implements SearchUserView,
        UserAdapter.ClickListener {

    @InjectPresenter
    SearchUserPresenter mPresenter;

    private UserAdapter mUserAdapter;

    public static SearchUserFragment newInstance() {
        return new SearchUserFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mPresenter.getData("");
    }

    @Override
    public void onItemClicked(User user) {
        mPresenter.handleUserClick(user);
    }

    @Override
    public void displayData(List<User> users) {
        mUserAdapter.setDataSet(users);
    }

    @Override
    public void clearData() {
        mUserAdapter.clear();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }

    private void initView() {
        mUserAdapter = new UserAdapter(mImageLoader);
        mUserAdapter.setOnClickListener(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mUserAdapter);
    }
}
