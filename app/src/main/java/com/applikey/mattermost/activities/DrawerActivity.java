package com.applikey.mattermost.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.NavigationPresenter;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import javax.inject.Inject;

public abstract class DrawerActivity extends BaseMvpActivity implements NavigationView {

    private static final int ITEM_FIND_MORE_CHANNELS = 0;
    private static final int ITEM_INVITE_MEMBER = 1;
    private static final int ITEM_SETTINGS = 2;

    @InjectPresenter
    NavigationPresenter mPresenter;

    @Inject
    Prefs mPrefs;

    private Drawer mDrawer;
    private View mDrawerFooter;
    private View mDrawerHeader;

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        App.getUserComponent().inject(this);
        initDrawer();
        mPresenter.initUser();
    }

    @Override
    public void startChannelCreating() {
        startActivity(CreateChannelActivity.getIntent(this));
        closeDrawer();
    }

    @Override
    public void onUserInit(User user) {
        ImageView iv_avatar = (ImageView) mDrawerHeader.findViewById(R.id.iv_avatar);
        TextView tv_name = (TextView) mDrawerHeader.findViewById(R.id.tv_name);
        TextView tv_team = (TextView) mDrawerHeader.findViewById(R.id.tv_team);

        mImageLoader.displayCircularImage(user.getProfileImage(), iv_avatar);
        tv_name.setText(User.getDisplayableName(user));
        tv_team.setText(mPrefs.getCurrentTeamName());
    }

    protected boolean showHamburger() {
        return true;
    }

    protected abstract Toolbar getToolbar();

    private void closeDrawer() {
        mDrawer.closeDrawer();
    }

    private void initDrawer() {
        final PrimaryDrawerItem mItemAllChannels = new PrimaryDrawerItem().withName(R.string.find_more_channels)
                .withIdentifier(ITEM_FIND_MORE_CHANNELS)
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
                .withStickyFooter(R.layout.footer_drawer)
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
        mDrawerFooter = mDrawer.getStickyFooter();
        mDrawerFooter.setOnClickListener(v -> headerClick());

        mDrawerHeader = mDrawer.getHeader();
        final ActionBarDrawerToggle toggle = mDrawer.getActionBarDrawerToggle();
        toggle.setDrawerIndicatorEnabled(false);

        if (showHamburger()) {
            toggle.setHomeAsUpIndicator(R.drawable.ic_hamburger);
        } else {
            toggle.setHomeAsUpIndicator(R.drawable.ic_back);
            getToolbar().setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void headerClick() {
        mPresenter.createNewChannel();
    }

    private void itemScreen(int id) {
        switch (id) {
            case ITEM_FIND_MORE_CHANNELS:
                findMoreChannels();
                break;
            case ITEM_INVITE_MEMBER:
                startInviteNewMember();
                break;
            case ITEM_SETTINGS:
                startSettings();
                break;
        }
        closeDrawer();
    }

    private void findMoreChannels() {
        startActivity(FindMoreChannelsActivity.getIntent(this));
        closeDrawer();
    }

    private void startInviteNewMember() {
        startActivity(InviteNewMemberActivity.getIntent(this));
        closeDrawer();
    }

    private void startSettings() {
        startActivity(SettingsActivity.getIntent(this));
        closeDrawer();
    }
}
