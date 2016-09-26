package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.GroupAdapter;
import com.applikey.mattermost.models.groups.ChannelsWithMetadata;
import com.applikey.mattermost.mvp.presenters.ChannelsListPresenter;
import com.applikey.mattermost.mvp.views.ChannelsListView;
import com.applikey.mattermost.utils.kissUtils.utils.BundleUtil;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChannelListFragment extends BaseMvpFragment implements ChannelsListView {

    private static final String BEHAVIOR_KEY = "TabBehavior";
    private TabBehavior mTabBehavior = TabBehavior.UNDEFINED;

    @InjectPresenter
    ChannelsListPresenter mPresenter;

    @Bind(R.id.rv_channels)
    RecyclerView rvChannels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle arguments = getArguments();

        final int behaviorOrdinal = BundleUtil.getInt(arguments, BEHAVIOR_KEY);
        mTabBehavior = TabBehavior.values()[behaviorOrdinal];
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_channels, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTabBehavior == TabBehavior.CHANNELS) {
            mPresenter.getInitialData();
        }
    }

    public static ChannelListFragment newInstance(TabBehavior behavior) {
        final ChannelListFragment fragment = new ChannelListFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(BEHAVIOR_KEY, behavior.ordinal());

        fragment.setArguments(bundle);

        return fragment;
    }

    public TabBehavior getBehavior() {
        return mTabBehavior;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    @Override
    public void displayInitialData(ChannelsWithMetadata channelsWithMetadata) {
        final GroupAdapter adapter = new GroupAdapter(channelsWithMetadata.values());
        rvChannels.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvChannels.setAdapter(adapter);
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
                return R.drawable.no_resource;
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
}
