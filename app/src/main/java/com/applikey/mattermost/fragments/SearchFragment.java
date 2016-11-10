package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class SearchFragment extends BaseMvpFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @Bind(R.id.rv_items)
    RecyclerView mRecycleView;

    protected SearchAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(getLayout(), container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void startChatView(Channel channel) {
        getActivity().startActivity(ChatActivity.getIntent(getContext(), channel));
    }

    public void showLoading(boolean show) {
        if (show) {
            ((BaseActivity) getActivity()).showLoadingDialog();
        } else {
            ((BaseActivity) getActivity()).hideLoadingDialog();
        }
    }

    protected void initView(SearchAdapter.ClickListener clickListener) {
        mAdapter = new SearchAdapter(mImageLoader);
        mAdapter.setOnClickListener(clickListener);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mAdapter);
    }

    @LayoutRes
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }


    public void displayData(List<SearchItem> items) {
        Log.d(TAG, "displayData size:" + items.size());
        for(SearchItem searchItem : items){
            Log.d(TAG, "displayData: " + searchItem);
        }
        mAdapter.setDataSet(items);
    }


    public void clearData() {
        mAdapter.clear();
    }

}
