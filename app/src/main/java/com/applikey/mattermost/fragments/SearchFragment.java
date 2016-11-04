package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.models.channel.Channel;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class SearchFragment extends BaseMvpFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();

    @Bind(R.id.recycle_view)
    RecyclerView mRecycleView;

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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void startChatActivity(Channel channel) {
        getActivity().startActivity(ChatActivity.getIntent(getContext(), channel));
    }

    public void showLoading(boolean show) {
        if (show) {
            ((BaseActivity) getActivity()).showLoadingDialog();
        } else {
            ((BaseActivity) getActivity()).hideLoadingDialog();
        }
    }

    @LayoutRes
    protected abstract int getLayout();

}
