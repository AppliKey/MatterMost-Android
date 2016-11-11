package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.user.UserListParcelableWrapper;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.applikey.mattermost.views.ChannelTypeView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CreateChannelActivity extends BaseMvpActivity implements CreateChannelView, PeopleToNewChannelAdapter.OnUserChosenListener {

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

    @Bind(R.id.channel_type_view)
    ChannelTypeView mChannelTypeView;

    @Bind(R.id.btn_add_all)
    CheckedTextView mChBtnAddAll;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @InjectPresenter
    CreateChannelPresenter mPresenter;

    private static final int REQUEST_ADDED_MEMBERS_DIALOG = 1;

    private PeopleToNewChannelAdapter mAdapter;

    public static Intent getIntent(Context context) {
        return new Intent(context, CreateChannelActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_or_channel);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mAddedPeopleLayout.setImageLoader(mImageLoader);
        mAdapter = new PeopleToNewChannelAdapter(this, mImageLoader);
        mRvPeoples.setLayoutManager(new LinearLayoutManager(this));
        mRvPeoples.setNestedScrollingEnabled(false);
        mRvPeoples.setAdapter(mAdapter);
        setTitle(getString(R.string.new_private_group));
        mChannelTypeView.setOnCheckedChangedListener((view, checked) -> {
            @StringRes final int title = checked ? R.string.new_private_group : R.string.new_public_channel;
            @StringRes final int purposeHint = checked ? R.string.create_private_group_description_hint : R.string.create_public_channel_description_hint;
            CreateChannelActivity.this.setTitle(getResources().getString(title));
            mEtChannelDescription.setHint(purposeHint);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_create_channel: {
                createChannel();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createChannel() {
        final String channelName = mEtChannelName.getText().toString().trim();
        final String channelDescription = mEtChannelDescription.getText().toString().trim();
        final boolean isPublicChannel = !mChannelTypeView.isChecked();
        mPresenter.createChannel(channelName, channelDescription, isPublicChannel);

    }

    @Override
    public void onChosen(User user) {
        mPresenter.operateWithUser(user);
    }

    @OnTextChanged(R.id.et_search_people)
    public void onSearchFilterChanged(Editable editableString) {
        mPresenter.filterByFullName(editableString.toString());
    }

    @Override
    public void onChannelCreated() {
        finish();
    }

    @Override
    public void showEmptyChannelNameError() {
        @StringRes final int errorRes = mChannelTypeView.isChecked()
                ? R.string.error_private_group_name_empty
                : R.string.error_channel_name_empty;
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
        mAdapter.addUsers(allUsers);
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @OnClick(R.id.btn_add_all)
    public void onClickButtonAddAll(CheckedTextView chBtnAddAll) {
        if (chBtnAddAll.isChecked()) {
            chBtnAddAll.setText(R.string.cancel);
            mPresenter.inviteAll();
        } else {
            chBtnAddAll.setText(R.string.button_add_all);
            mPresenter.revertInviteAll();
        }
        chBtnAddAll.setChecked(!chBtnAddAll.isChecked());
    }

    @OnClick(R.id.added_people_layout)
    public void onAddedUsersPanelClick() {
        final List<User> invitedUsers = mPresenter.getInvitedUsers();
        final Intent intent = AddedMembersActivity.getIntent(this, invitedUsers, true);
        startActivityForResult(intent, REQUEST_ADDED_MEMBERS_DIALOG);
    }

    @Override
    public void setButtonAddAllState(boolean isAllAlreadyInvited) {
        if (isAllAlreadyInvited) {
            mChBtnAddAll.setVisibility(View.GONE);
        } else {
            mChBtnAddAll.setVisibility(View.VISIBLE);
            mChBtnAddAll.setChecked(true);
            mChBtnAddAll.setText(R.string.button_add_all);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDED_MEMBERS_DIALOG) {
            if (resultCode == RESULT_OK) {
                final UserListParcelableWrapper wrapper = data.getParcelableExtra(AddedMembersActivity.USERS_IDS_KEY);
                mPresenter.setAlreadyAddedUsers(wrapper.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

