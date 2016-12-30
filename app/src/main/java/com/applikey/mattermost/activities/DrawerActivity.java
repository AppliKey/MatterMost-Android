package com.applikey.mattermost.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.NavigationPresenter;
import com.applikey.mattermost.mvp.views.NavigationView;
import com.applikey.mattermost.views.CustomPrimaryDrawerItem;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import butterknife.ButterKnife;

public abstract class DrawerActivity extends BaseMvpActivity implements NavigationView {

    public static final int ITEM_FIND_MORE_CHANNELS = 0;
    public static final int ITEM_INVITE_MEMBER = 1;
    public static final int ITEM_SETTINGS = 2;

    @InjectPresenter
    NavigationPresenter mPresenter;

    private Drawer mDrawer;
    private View mDrawerFooter;
    private View mDrawerHeader;

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initDrawer();
    }

    @Override
    public void startChannelCreating() {
        startActivity(CreateChannelActivity.getIntent(this));
        closeDrawer();
    }

    @Override
    public void setUserInfo(User user) {
        final ImageView ivAvatar = ButterKnife.findById(mDrawerHeader, R.id.iv_avatar);
        final TextView tvName = ButterKnife.findById(mDrawerHeader, R.id.tv_name);

        mImageLoader.displayCircularImage(user.getProfileImage(), ivAvatar);
        tvName.setText(User.getDisplayableName(user));
    }

    @Override
    public void setTeamName(String teamName) {
        final TextView tvTeam = ButterKnife.findById(mDrawerHeader, R.id.tv_team);
        tvTeam.setText(mPresenter.getTeamName());
    }

    @Override
    public void findMoreChannels() {
        startActivity(FindMoreChannelsActivity.getIntent(this));
    }

    @Override
    public void startInviteNewMember() {
        startActivity(InviteNewMemberActivity.getIntent(this));
    }

    @Override
    public void startSettings() {
        startActivity(SettingsActivity.getIntent(this));
    }

    protected boolean showHamburger() {
        return true;
    }

    protected abstract Toolbar getToolbar();

    private void closeDrawer() {
        mDrawer.closeDrawer();
    }

    private void initDrawer() {
        final PrimaryDrawerItem mItemAllChannels = new CustomPrimaryDrawerItem().withName(R.string.find_more_channels)
                .withIdentifier(ITEM_FIND_MORE_CHANNELS)
                .withTypeface(Typeface.SANS_SERIF);
        final PrimaryDrawerItem mItemInviteNewMember = new CustomPrimaryDrawerItem().withName(
                R.string.invite_new_member)
                .withIdentifier(ITEM_INVITE_MEMBER)
                .withTypeface(Typeface.SANS_SERIF);
        final PrimaryDrawerItem mItemSettings = new CustomPrimaryDrawerItem().withName(R.string.settings)
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
                    itemClick((int) drawerItem.getIdentifier());
                    return true;
                })
                .withSelectedItem(-1)
                .build();
        mDrawerFooter = mDrawer.getStickyFooter();
        mDrawerFooter.setOnClickListener(v -> footerClick());

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

    private void footerClick() {
        mPresenter.createNewChannel();
    }

    private void itemClick(int id) {
        mPresenter.onItemClick(id);
        mDrawer.deselect();
        closeDrawer();
    }
}
