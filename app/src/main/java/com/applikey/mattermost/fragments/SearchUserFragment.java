package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.UserAdapter;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.SearchUserPresenter;
import com.applikey.mattermost.mvp.views.SearchUserView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

/**
 * @author Anatoliy Chub
 */

public class SearchUserFragment extends SearchFragment implements SearchUserView,
        UserAdapter.ClickListener {

    private static final String TAG = SearchUserFragment.class.getSimpleName();

    @InjectPresenter
    SearchUserPresenter mPresenter;

    private UserAdapter mUserAdapter;

    public static SearchUserFragment newInstance() {
        return new SearchUserFragment();
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getPresenter().getData("");
    }

    @Override
    public void onItemClicked(User user) {
        Log.d(TAG, "onItemClicked: ");
        getPresenter().handleUserClick(user);
    }

    private void initView() {
        mUserAdapter = new UserAdapter(mImageLoader);
        mUserAdapter.setOnClickListener(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mUserAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPresenter().unSubscribe();
    }

    @Override
    public void displayData(List<User> users) {
        Log.d(TAG, "displayData: ");
        mUserAdapter.setDataSet(users);
    }

    protected SearchUserPresenter getPresenter() {
        if (mPresenter == null) {
            throw new RuntimeException("Presenter is null");
        }
        return mPresenter;
    }


    @Override
    public void clearData(){
        mUserAdapter.clear();
    }

}
