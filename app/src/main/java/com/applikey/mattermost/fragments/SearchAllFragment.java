package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * @author Anatoliy Chub
 */

public class SearchAllFragment extends SearchFragment implements SearchAllView,
        ChannelAdapter.ClickListener {

    private static final String TAG = SearchAllFragment.class.getSimpleName();

    @InjectPresenter
    SearchAllPresenter mPresenter;

    @Bind(R.id.recycle_view)
    RecyclerView mRecycleView;

    private SearchAdapter mAdapter;


    public static SearchAllFragment newInstance() {
        return new SearchAllFragment();
    }



    @Override
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getPresenter().requestNotJoinedChannels();
        getPresenter().getData("");
    }

    @Override
    public void onItemClicked(Channel channel) {
        Log.d(TAG, "onItemClicked: ");

    }

    private void initView() {
        mAdapter = new SearchAdapter(mImageLoader);
//        mAdapter.setOnClickListener(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPresenter().unSubscribe();
    }

    @Override
    public void displayData(List<SearchItem> channels) {
        Log.d(TAG, "displayData: ");
        mAdapter.setDataSet(channels);
    }

    @Override
    public void startChatActivity(SearchItem item) {

    }

    protected SearchAllPresenter getPresenter() {
        if (mPresenter == null) {
            throw new RuntimeException("Presenter is null");
        }
        return mPresenter;
    }


    @Override
    public void clearData(){
        mAdapter.clear();
    }

}
