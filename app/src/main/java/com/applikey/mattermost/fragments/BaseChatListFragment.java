package com.applikey.mattermost.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
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
import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.adapters.channel.BaseChatListAdapter;
import com.applikey.mattermost.adapters.channel.UserChatListAdapter;
import com.applikey.mattermost.events.TabIndicatorRequested;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.utils.kissUtils.utils.BundleUtil;
import com.applikey.mattermost.views.TabBehavior;
import com.applikey.mattermost.web.images.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public abstract class BaseChatListFragment extends BaseMvpFragment
        implements ChatListView, UserChatListAdapter.ChannelListener {

    /* package */ static final String BEHAVIOR_KEY = "TabBehavior";

    @Bind(R.id.rv_channels)
    RecyclerView mRvChannels;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @Inject
    ImageLoader mImageLoader;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    private TabBehavior mTabBehavior = TabBehavior.UNDEFINED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();

        final int behaviorOrdinal = BundleUtil.getInt(arguments, BEHAVIOR_KEY);
        mTabBehavior = TabBehavior.values()[behaviorOrdinal];
        App.getUserComponent().inject(this);
        getPresenter().displayData();
    }

    @Override
    public void onStart() {
        super.onStart();
        mTvEmptyState.setText(getResources().getString(getEmptyStateTextId()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void displayInitialData(RealmResults<Channel> channels) {
        Log.d(BaseChatListFragment.class.getSimpleName(), "Data displayed " + channels.size());

        mRvChannels.setVisibility(View.VISIBLE);
        mTvEmptyState.setVisibility(View.GONE);
        final BaseChatListAdapter adapter = getAdapter(channels);
        adapter.setChannelListener(this);
        mRvChannels.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvChannels.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(Channel channel) {
        final Activity activity = getActivity();
        final Intent intent = ChatActivity.getIntent(activity, channel);
        activity.startActivity(intent);
    }

    @Override
    @CallSuper
    public void onLoadAdditionalData(Channel channel) {
        getPresenter().getLastPost(channel);
    }

    @Override
    public void showUnreadIndicator(boolean showIndicator) {
        mEventBus.post(new TabIndicatorRequested(mTabBehavior, showIndicator));
    }

    @Override
    public void showEmpty() {
        mTvEmptyState.setVisibility(View.VISIBLE);
        mRvChannels.setVisibility(View.GONE);
    }

    protected abstract ChatListPresenter getPresenter();

    protected abstract int getEmptyStateTextId();

    protected abstract BaseChatListAdapter getAdapter(RealmResults<Channel> channels);

}
