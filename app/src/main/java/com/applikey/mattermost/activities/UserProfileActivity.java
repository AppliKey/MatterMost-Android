package com.applikey.mattermost.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.UserProfilePresenter;
import com.applikey.mattermost.mvp.views.UserProfileView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.devspark.robototextview.widget.RobotoTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivity extends BaseMvpActivity implements UserProfileView {

    private static final String USER_ID_KEY = "user-id";
    private static final int MENU_ITEM_FAVORITE = Menu.FIRST;

    @InjectPresenter
    UserProfilePresenter mPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.channel_name)
    RobotoTextView mChannelName;

    @Bind(R.id.channel_description)
    RobotoTextView mChannelDescription;

    @Bind(R.id.added_people_layout)
    AddedPeopleLayout mAddedPeopleLayout;

    @Bind(R.id.container)
    LinearLayout mContainer;

    private Menu mMenu;

    public static Intent getIntent(Context context, User user) {
        final Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(USER_ID_KEY, user.getId());
        return intent;
    }

    //TODO Implement Invite members logic
    @OnClick(R.id.b_invite_member)
    public void onInviteMemberClick() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_details);
        ButterKnife.bind(this);
        initViews();
        initParameters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_FAVORITE, Menu.NONE, R.string.make_favorite)
                .setIcon(R.drawable.ic_favorite_uncheck)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_FAVORITE:
                mPresenter.toggleChannelFavorite();
                return true;
            default:
                return false;
        }
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setTitle(null);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        mAddedPeopleLayout.setImageLoader(mImageLoader);
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        final String userId = extras.getString(USER_ID_KEY);
        mPresenter.getInitialData(userId);
    }

    @Override
    public void showBaseDetails(User user) {

    }

    @Override
    public void onMakeChannelFavorite(boolean favorite) {
        final int iconRes = favorite ? R.drawable.ic_favorite_check : R.drawable.ic_favorite_uncheck;
        mMenu.getItem(0).setIcon(iconRes);
    }
}
