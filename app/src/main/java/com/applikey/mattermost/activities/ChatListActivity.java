package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.ChatListPagerAdapter;
import com.applikey.mattermost.events.TabIndicatorRequested;
import com.applikey.mattermost.manager.notitifcation.NotificationManager;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.ChatListScreenPresenter;
import com.applikey.mattermost.mvp.views.ChatListScreenView;
import com.applikey.mattermost.platform.socket.WebSocketService;
import com.applikey.mattermost.views.TabBehavior;
import com.applikey.mattermost.views.TabSelectedListener;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatListActivity extends DrawerActivity implements ChatListScreenView {

    private final TabIndicatorModel mTabIndicatorModel = new TabIndicatorModel();

    @InjectPresenter
    ChatListScreenPresenter mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.vpChatList)
    ViewPager mViewPager;

    private ChatListPagerAdapter mChatListPagerAdapter;

    public static Intent getIntent(Context context) {
        final Intent intent = new Intent(context, ChatListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent getIntent(Context context, Bundle bundle) {
        final Intent intent = getIntent(context);
        intent.putExtra(NotificationManager.NOTIFICATION_BUNDLE_KEY, bundle);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ButterKnife.bind(this);
        initView();
        mEventBus.register(this);

        onNewIntent(getIntent());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatListPagerAdapter != null) {
            mPresenter.checkSettingChanges();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        mPresenter.initPages();
        return true;
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    public void onChannelLoaded(Channel channel) {
        startActivity(ChatActivity.getIntent(this, channel));
    }

    @Override
    public void stopWebSocketService() {
        stopService(WebSocketService.getIntent(this));
    }

    @Override
    public void initViewPager(List<Fragment> pages) {
        mChatListPagerAdapter = new ChatListPagerAdapter(getSupportFragmentManager(), pages);
        mViewPager.setAdapter(mChatListPagerAdapter);
        mChatListPagerAdapter.notifyDataSetChanged();
        mTabLayout.setupWithViewPager(mViewPager, false);
        final int offset = mPresenter.shouldShowUnreadTab() ? 0 : 1;
        final int tabCount = mTabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            final TabLayout.Tab tab = mTabLayout.getTabAt(i);
            final int tabIconIndex = i + offset;
            if (tab != null) {
                tab.setCustomView(R.layout.tab_chat_list);
                tab.setIcon(TabBehavior.getItemBehavior(tabIconIndex).getIcon());

                final View customTab = tab.getCustomView();
                if (customTab != null) {
                    final View notificationIcon = customTab.findViewById(R.id.iv_notification_icon);
                    mTabIndicatorModel.register(TabBehavior.getItemBehavior(tabIconIndex),
                                                (ImageView) notificationIcon);
                }
            }
        }
        final TabSelectedListener mOnTabSelectedListener = new ChatListTabSelectedListener(mViewPager);
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
        mOnTabSelectedListener.onTabReselected(mTabLayout.getTabAt(0));
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount() - 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(SearchChatActivity.getIntent(this));
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Bundle bundle = getIntent().getBundleExtra(NotificationManager.NOTIFICATION_BUNDLE_KEY);
        if (bundle != null) {
            final String channelId = bundle.getString(
                    NotificationManager.NOTIFICATION_CHANNEL_ID_KEY);
            if (channelId != null) {
                mPresenter.preloadChannel(channelId);
            }
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Subscribe
    public void on(TabIndicatorRequested event) {
        mTabIndicatorModel.handleEvent(event);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
    }

    private class ChatListTabSelectedListener extends TabSelectedListener {

        ChatListTabSelectedListener(ViewPager viewPager) {
            super(viewPager);
        }

        protected int getSelectedTabColor() {
            if (mSelectedTabColor == -1) {
                mSelectedTabColor = ContextCompat.getColor(ChatListActivity.this, R.color.tabSelected);
            }
            return mSelectedTabColor;
        }

        protected int getUnSelectedTabColor() {
            if (mUnSelectedTabColor == -1) {
                mUnSelectedTabColor = ContextCompat.getColor(ChatListActivity.this, R.color.tabUnSelected);
            }
            return mUnSelectedTabColor;
        }
    }

    private class TabIndicatorModel {

        private final Object mutex = new Object();

        private final Map<TabBehavior, Boolean> mIndicatorVisibilities = new ArrayMap<>();
        private final Map<TabBehavior, ImageView> mIndicators = new ArrayMap<>();

        void handleEvent(TabIndicatorRequested event) {
            synchronized (mutex) {
                final TabBehavior tab = event.getBehavior();
                Log.d("cc", "handleEvent: " + event.getBehavior());
                mIndicatorVisibilities.put(tab, event.isVisible());
                updateVisibility(tab, event.isVisible());
            }
        }

        void register(TabBehavior tab, ImageView indicator) {
            synchronized (mutex) {
                mIndicators.put(tab, indicator);
                final boolean visible = mIndicatorVisibilities.containsKey(tab)
                        ? mIndicatorVisibilities.get(tab) : false;
                updateVisibility(tab, visible);
            }
        }

        private void updateVisibility(TabBehavior tab, boolean isVisible) {
            if (mIndicators.containsKey(tab)) {
                mIndicators.get(tab).setVisibility(isVisible ? VISIBLE : GONE);
            }
        }
    }
}
