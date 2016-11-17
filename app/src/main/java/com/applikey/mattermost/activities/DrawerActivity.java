package com.applikey.mattermost.activities;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.NavigationPresenter;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.utils.kissUtils.utils.SystemUtil;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

public abstract class DrawerActivity extends BaseMvpActivity implements NavigationView {

    private static final int ITEM_ALL_CHANNELS = 0;
    private static final int ITEM_INVITE_MEMBER = 1;
    private static final int ITEM_SETTINGS = 2;
    private Drawer mDrawer;
    @InjectPresenter
    NavigationPresenter mPresenter;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initDrawer();
    }

    protected boolean showHamburger() {
        return true;
    }

    protected abstract Toolbar getToolbar();

    private void closeDrawer() {
        mDrawer.closeDrawer();
    }

    private void initDrawer() {
        final PrimaryDrawerItem mItemAllChannels = new PrimaryDrawerItem().withName(R.string.all_channels)
                .withIdentifier(ITEM_ALL_CHANNELS)
                .withTypeface(Typeface.SANS_SERIF);
        final PrimaryDrawerItem mItemInviteNewMember = new PrimaryDrawerItem().withName(R.string.invite_new_member)
                .withIdentifier(ITEM_INVITE_MEMBER)
                .withTypeface(Typeface.SANS_SERIF);
        final PrimaryDrawerItem mItemSettings = new PrimaryDrawerItem().withName(R.string.settings)
                .withIdentifier(ITEM_SETTINGS)
                .withTypeface(Typeface.SANS_SERIF);

        mDrawer = new DrawerBuilder().withActivity(this)
                .withToolbar(getToolbar())
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.header_drawer)
                .addDrawerItems(
                        mItemAllChannels,
                        mItemInviteNewMember,
                        mItemSettings
                               )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    itemScreen((int) drawerItem.getIdentifier());
                    return true;
                })
                .withSelectedItem(-1)
                .build();
        final View header = mDrawer.getHeader();
        header.setOnClickListener(v -> headerClick());

        final ActionBarDrawerToggle toggle = mDrawer.getActionBarDrawerToggle();
        toggle.setDrawerIndicatorEnabled(false);

        if (showHamburger()) {
            toggle.setHomeAsUpIndicator(R.drawable.ic_hamburger);
        } else {
            toggle.setHomeAsUpIndicator(R.drawable.ic_back);
            getToolbar().setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int statusBarHeight = SystemUtil.getStatusBarHeight(this);
            header.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    private void headerClick() {
        mPresenter.createNewChannel();
    }

    private void itemScreen(int id) {
        switch (id) {
            case ITEM_ALL_CHANNELS:
                if (!(this instanceof ChatListActivity)) {
                    finish();
                }
                break;
            case ITEM_INVITE_MEMBER:
                break;
            case ITEM_SETTINGS:
                startSettings();
                break;
        }
        closeDrawer();
    }

    private void startSettings() {
        startActivity(SettingsActivity.getIntent(this));
        closeDrawer();
    }

    @Override
    public void startChannelCreating() {
        startActivity(CreateChannelActivity.getIntent(this));
        closeDrawer();
    }
}
