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
import android.widget.TextView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.adapters.SearchAdapter;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.views.SearchView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class SearchFragment extends BaseMvpFragment implements SearchView {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @Bind(R.id.rv_items)
    RecyclerView mRecyclerView;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    protected SearchAdapter mAdapter;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getUserComponent().inject(this);
    }

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
        getActivity().startActivity(ChatActivity.getIntent(getContext(), channel, !channel.isJoined()));
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            ((BaseActivity) getActivity()).showLoadingDialog();
        } else {
            ((BaseActivity) getActivity()).hideLoadingDialog();
        }
    }

    protected void initView(SearchAdapter.ClickListener clickListener) {
        mAdapter = new SearchAdapter(mImageLoader, mCurrentUserId);
        mAdapter.setOnClickListener(clickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @LayoutRes
    protected int getLayout() {
        return R.layout.fragment_search_chat;
    }

    @Override
    public void displayData(List<SearchItem> items) {
        Log.d(TAG, "displayData: " + items);
        if(items == null || items.isEmpty()) {
            setEmptyState(true);
        } else {
            setEmptyState(false);
            mAdapter.setDataSet(items);
        }
    }

    public void setEmptyState(boolean isEmpty){
        if (isEmpty) {
            mTvEmptyState.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mTvEmptyState.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void clearData() {
        mAdapter.clear();
    }

    @Override
    public void setSearchText(String text) {
        mAdapter.setSearchText(text);
    }
}
