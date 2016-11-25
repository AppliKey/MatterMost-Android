package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.user.UserListParcelableWrapper;
import com.applikey.mattermost.mvp.presenters.BaseEditChannelPresenter;
import com.applikey.mattermost.mvp.views.BaseEditChannelView;
import com.applikey.mattermost.views.AddedPeopleLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public abstract class BaseEditChannelActivity extends BaseMvpActivity
        implements BaseEditChannelView, PeopleToNewChannelAdapter.OnUserChosenListener {

    private static final int REQUEST_ADDED_MEMBERS_DIALOG = 1;

    @Bind(R.id.et_channel_name)
    EditText mEtChannelName;

    @Bind(R.id.et_channel_description)
    EditText mEtChannelDescription;

    @Bind(R.id.et_search_people)
    EditText mEtSearchPeople;

    @Bind(R.id.rv_peoples)
    RecyclerView mRvPeoples;

    @Bind(R.id.added_people_layout)
    AddedPeopleLayout mAddedPeopleLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    protected PeopleToNewChannelAdapter mAdapter;

    public static Intent getIntent(Context context) {
        return new Intent(context, CreateChannelActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mAddedPeopleLayout.setImageLoader(mImageLoader);
        mAdapter = new PeopleToNewChannelAdapter(true, this, mImageLoader);
        mRvPeoples.setLayoutManager(new LinearLayoutManager(this));
        mRvPeoples.setNestedScrollingEnabled(false);
        mRvPeoples.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChosen(User user) {
        getPresenter().operateWithUser(user);
    }

    @OnTextChanged(R.id.et_search_people)
    public void onSearchFilterChanged(Editable editableString) {
        getPresenter().filterByFullName(editableString.toString());
    }


    @Override
    public void showEmptyChannelNameError(boolean isPublic) {
        hideLoadingDialog();
        @StringRes final int errorRes = isPublic
                ? R.string.error_channel_name_empty
                : R.string.error_private_group_name_empty;
        showToast(errorRes);
    }

    @Override
    public void showAddedUser(User user) {
        mAddedPeopleLayout.addUser(user);
        mAdapter.addAlreadyAddedUser(user);
    }

    @Override
    public void showAddedUsers(List<User> users) {
        mAddedPeopleLayout.showUsers(users);
        mAdapter.addAlreadyAddedUsers(users);
    }

    @Override
    public void removeUser(User user) {
        mAddedPeopleLayout.removeUser(user);
        mAdapter.removeAlreadyAddedUser(user);
    }

    @Override
    public void showAllUsers(List<User> allUsers) {
        showEmptyState(allUsers.isEmpty());
        mAdapter.addUsers(allUsers);
    }

    @Override
    public void showError(String error) {
        hideLoadingDialog();
        showToast(error);
    }


    @OnClick(R.id.added_people_layout)
    public void onAddedUsersPanelClick() {
        final List<User> invitedUsers = getPresenter().getInvitedUsers();
        final Intent intent = AddedMembersActivity.getIntent(this, invitedUsers, true);
        startActivityForResult(intent, REQUEST_ADDED_MEMBERS_DIALOG);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDED_MEMBERS_DIALOG) {
            if (resultCode == RESULT_OK) {
                final UserListParcelableWrapper wrapper = data.getParcelableExtra(
                        AddedMembersActivity.USERS_IDS_KEY);
                getPresenter().setAlreadyAddedUsers(wrapper.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            mRvPeoples.setVisibility(View.GONE);
            mTvEmptyState.setVisibility(View.VISIBLE);
        } else {
            mRvPeoples.setVisibility(View.VISIBLE);
            mTvEmptyState.setVisibility(View.GONE);
        }
    }

    protected abstract BaseEditChannelPresenter getPresenter();

    @LayoutRes
    protected abstract int getLayoutRes();

    @Override
    public void showLoadingDialog() {
        hideKeyboard();
        super.showLoadingDialog();
    }
}
