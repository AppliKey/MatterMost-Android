package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.ChannelDetailsPresenter;
import com.applikey.mattermost.mvp.views.ChannelDetailsView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.transitionseverywhere.TransitionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelDetailsActivity extends BaseMvpActivity implements ChannelDetailsView {

    private static final String TAG = "ChannelDetailsActivity";

    private static final String CHANNEL_ID_KEY = "channel-id";
    private static final int MENU_ITEM_FAVORITE = Menu.FIRST;

    @InjectPresenter
    ChannelDetailsPresenter mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.channel_name)
    TextView mChannelName;

    @BindView(R.id.channel_description)
    TextView mChannelDescription;

    @BindView(R.id.added_people_layout)
    AddedPeopleLayout mAddedPeopleLayout;

    @BindView(R.id.container)
    LinearLayout mContainer;

    private Menu mMenu;

    public static Intent getIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChannelDetailsActivity.class);
        intent.putExtra(CHANNEL_ID_KEY, channel.getId());
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_details);
        ButterKnife.bind(this);
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_FAVORITE, Menu.NONE, R.string.make_favorite)
                .setIcon(R.drawable.ic_favorite_uncheck)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenu = menu;
        initParameters();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_FAVORITE:
                mPresenter.toggleFavorite();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void showBaseDetails(Channel channel) {
        mChannelName.setText(
                getString(R.string.channel_display_name_format, channel.getDisplayName()));
        mChannelDescription.setText(channel.getPurpose());
        if (TextUtils.isEmpty(channel.getPurpose())) {
            mChannelDescription.setVisibility(View.GONE);
        } else {
            mChannelDescription.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMembers(List<User> users) {
        TransitionManager.beginDelayedTransition(mContainer);
        mAddedPeopleLayout.showUsers(users);
    }

    @Override
    public void onMakeFavorite(boolean favorite) {
        Log.d(TAG, "onMakeFavorite: " + favorite);
        final int iconRes = favorite
                ? R.drawable.ic_favorite_check
                : R.drawable.ic_favorite_uncheck;
        mMenu.getItem(0).setIcon(iconRes);
    }

    @Override
    public void openEditChannel(Channel channel, boolean invite) {
        startActivity(EditChannelActivity.getIntent(this, channel, invite));

    }

    @OnClick(R.id.b_invite_member)
    public void onInviteMemberClick() {
        mPresenter.onEditChannel(true);
    }

    @OnClick(R.id.b_edit_channel)
    public void onEditChannelClick() {
        mPresenter.onEditChannel(false);
    }

    @OnClick(R.id.btn_leave_channel)
    public void onLeaveChannelClick() {
        mPresenter.leaveChannel();
    }

    @Override
    public void backToMainActivity() {
        startActivity(ChatListActivity.getIntent(this));
    }

    @OnClick(R.id.added_people_layout)
    void onAddedUsersPanelClick() {
        startActivity(AddedMembersActivity.getIntent(this, mAddedPeopleLayout.getUsers(), false));
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
            actionBar.setTitle(null);
        }
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mAddedPeopleLayout.setImageLoader(mImageLoader);
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        final String channelId = extras.getString(CHANNEL_ID_KEY);
        mPresenter.getInitialData(channelId);
    }
}
