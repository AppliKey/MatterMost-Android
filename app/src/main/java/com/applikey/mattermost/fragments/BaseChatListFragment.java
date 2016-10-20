package com.applikey.mattermost.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.adapters.ChatListAdapter;
import com.applikey.mattermost.events.TabIndicatorRequested;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.utils.kissUtils.utils.BundleUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseChatListFragment extends BaseMvpFragment implements ChatListView {

    /* package */ static final String BEHAVIOR_KEY = "TabBehavior";
    private TabBehavior mTabBehavior = TabBehavior.UNDEFINED;

    @Bind(R.id.rv_channels)
    RecyclerView mRvChannels;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @Inject
    ImageLoader mImageLoader;

    @Inject
    EventBus mEventBus;

    @Inject
    @Named("currentUserId")
    String mCurrentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();

        final int behaviorOrdinal = BundleUtil.getInt(arguments, BEHAVIOR_KEY);
        mTabBehavior = TabBehavior.values()[behaviorOrdinal];

        App.getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mTvEmptyState.setText(getResources().getString(getEmptyStateTextId()));

        getPresenter().getInitialData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPresenter().unSubscribe();
    }

    @Override
    public void displayInitialData(List<Channel> channels) {
        Log.d(BaseChatListFragment.class.getSimpleName(), "Data displayed");

        if (channels.isEmpty()) {
            mTvEmptyState.setVisibility(View.VISIBLE);
            mRvChannels.setVisibility(View.GONE);
            return;
        }

        mRvChannels.setVisibility(View.VISIBLE);
        mTvEmptyState.setVisibility(View.GONE);
        final ChatListAdapter adapter = new ChatListAdapter(channels, mImageLoader, mCurrentUserId);
        adapter.setOnClickListener(mChatClickListener);
        mRvChannels.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvChannels.setAdapter(adapter);

        displayUnreadIndicator(channels);
    }

    private void displayUnreadIndicator(List<Channel> channels) {
        for (Channel channel : channels) {
            if (channel.isUnread()) {
                mEventBus.post(new TabIndicatorRequested(mTabBehavior, true));
                break;
            }
        }
    }

    public enum TabBehavior {
        UNDEFINED {
            @Override
            public int getIcon() {
                return R.drawable.no_resource;
            }
        },
        UNREAD {
            @Override
            public int getIcon() {
                return R.drawable.ic_unread;
            }
        },
        FAVOURITES {
            @Override
            public int getIcon() {
                return R.drawable.ic_favourites_tab;
            }
        },
        CHANNELS {
            @Override
            public int getIcon() {
                return R.drawable.ic_public_channels_tab;
            }
        },
        GROUPS {
            @Override
            public int getIcon() {
                return R.drawable.ic_private_channels_tab;
            }
        },
        DIRECT {
            @Override
            public int getIcon() {
                return R.drawable.ic_direct_tab;
            }
        };

        @DrawableRes
        public abstract int getIcon();

        public static TabBehavior getItemBehavior(int pageIndex) {
            // Offset should be introduced
            return TabBehavior.values()[pageIndex + 1];
        }
    }

    private final ChatListAdapter.ClickListener mChatClickListener = channel -> {
        final Activity activity = getActivity();
        final Intent intent = ChatActivity.getIntent(activity, channel);
        activity.startActivity(intent);
    };

    protected abstract ChatListPresenter getPresenter();

    protected abstract int getEmptyStateTextId();
}
