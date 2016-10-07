package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.ChatListAdapter;
import com.applikey.mattermost.events.TabIndicatorRequested;
import com.applikey.mattermost.models.channel.ChannelWithMetadata;
import com.applikey.mattermost.models.channel.ChannelsWithMetadata;
import com.applikey.mattermost.mvp.presenters.ChatListPresenter;
import com.applikey.mattermost.mvp.views.ChatListView;
import com.applikey.mattermost.utils.kissUtils.utils.BundleUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseChatListFragment extends BaseMvpFragment implements ChatListView {

    /* package */ static final String BEHAVIOR_KEY = "TabBehavior";
    private TabBehavior mTabBehavior = TabBehavior.UNDEFINED;

    @Bind(R.id.rv_channels)
    RecyclerView rvChannels;

    @Bind(R.id.tv_empty_state)
    TextView tvEmptyState;

    @Inject
    ImageLoader mImageLoader;

    @Inject
    EventBus mEventBus;

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

        tvEmptyState.setText(getResources().getString(getEmptyStateTextId()));

        getPresenter().getInitialData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPresenter().unSubscribe();
    }

    @Override
    public void displayInitialData(ChannelsWithMetadata channelsWithMetadata) {
        if (channelsWithMetadata.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvChannels.setVisibility(View.GONE);
            return;
        }

        rvChannels.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        final ChatListAdapter adapter = new ChatListAdapter(channelsWithMetadata.values(),
                mImageLoader);
        rvChannels.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvChannels.setAdapter(adapter);

        displayUnreadIndicator(channelsWithMetadata);
    }

    private void displayUnreadIndicator(ChannelsWithMetadata channelsWithMetadata) {
        for (ChannelWithMetadata channel : channelsWithMetadata.values()) {
            if (channel.checkIsUnread()) {
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
        }, UNREAD {
            @Override
            public int getIcon() {
                return R.drawable.ic_unread;
            }
        }, FAVOURITES {
            @Override
            public int getIcon() {
                return R.drawable.ic_favourites_tab;
            }
        }, CHANNELS {
            @Override
            public int getIcon() {
                return R.drawable.ic_public_channels_tab;
            }
        }, GROUPS {
            @Override
            public int getIcon() {
                return R.drawable.ic_private_channels_tab;
            }
        }, DIRECT {
            @Override
            public int getIcon() {
                return R.drawable.ic_direct_tab;
            }
        };

        @DrawableRes
        public abstract int getIcon();

        public static TabBehavior getItemBehavior(int pageIndex) {
            // Offset should be introduces
            return TabBehavior.values()[pageIndex + 1];
        }
    }

    protected abstract ChatListPresenter getPresenter();

    protected abstract int getEmptyStateTextId();
}
