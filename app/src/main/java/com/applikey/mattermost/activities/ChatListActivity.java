package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.ChatListPagerAdapter;
import com.applikey.mattermost.fragments.ChannelListFragment;
import com.applikey.mattermost.mvp.presenters.ChatListPagePresenter;
import com.applikey.mattermost.mvp.views.ChatListScreenView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatListActivity extends BaseMvpActivity implements ChatListScreenView {

    @InjectPresenter
    ChatListPagePresenter mPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.vpChatList)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mPresenter.applyInitialViewState();

        mViewPager.setAdapter(new ChatListPagerAdapter(ChatListActivity.this,
                getSupportFragmentManager()));

        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            final TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(ChannelListFragment.TabBehavior.getItemBehavior(i).getIcon());
            }
        }

        final TabSelectedListener mOnTabSelectedListener = new TabSelectedListener(mViewPager);
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
        mOnTabSelectedListener.onTabReselected(mTabLayout.getTabAt(0));
    }

    @Override
    public void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    public static Intent getIntent(Context context) {
        final Intent intent = new Intent(context, ChatListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private class TabSelectedListener extends TabLayout.ViewPagerOnTabSelectedListener {

        private int selectedTabColor = -1;
        private int unSelectedTabColor = -1;

        TabSelectedListener(ViewPager viewPager) {
            super(viewPager);
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            super.onTabSelected(tab);
            final Drawable icon = tab.getIcon();
            if (icon != null) {
                icon.setColorFilter(getSelectedTabColor(), PorterDuff.Mode.SRC_IN);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            super.onTabUnselected(tab);
            final Drawable icon = tab.getIcon();
            if (icon != null) {
                icon.setColorFilter(getUnSelectedTabColor(), PorterDuff.Mode.SRC_IN);
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            super.onTabReselected(tab);
            final Drawable icon = tab.getIcon();
            if (icon != null) {
                icon.setColorFilter(getSelectedTabColor(), PorterDuff.Mode.SRC_IN);
            }
        }

        private int getSelectedTabColor() {
            if (selectedTabColor == -1) {
                selectedTabColor = ContextCompat.getColor(ChatListActivity.this,
                        R.color.tabSelected);
            }
            return selectedTabColor;
        }

        private int getUnSelectedTabColor() {
            if (unSelectedTabColor == -1) {
                unSelectedTabColor = ContextCompat.getColor(ChatListActivity.this,
                        R.color.tabUnSelected);
            }
            return unSelectedTabColor;
        }
    }
}
