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
import android.widget.Button;
import android.widget.EditText;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.applikey.mattermost.views.ChannelTypeView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.ArrayList;
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
    Button mBtnAddAll;

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
            CreateChannelActivity.this.setTitle(getResources().getString(title));
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
    public void onChosen(User user, boolean isInvited) {
        if (isInvited) {
            mPresenter.addUser(user);
        } else {
            mPresenter.removeUser(user);
        }
    }

    @Override
    public void setAddAllButtonState(boolean isNeedToCancel) {
        if (isNeedToCancel) {
            mBtnAddAll.setText(R.string.button_add_all);
            mBtnAddAll.setBackgroundResource(R.drawable.round_button_gradient);
        } else {
            mBtnAddAll.setText(R.string.button_clear);
            mBtnAddAll.setBackgroundResource(R.drawable.round_button_gray);
        }
    }

    @Override
    public void showUsers(List<UserPendingInvitation> results) {
        mAdapter.addUsers(results);
    }

    @OnClick(R.id.btn_add_all)
    public void onBtnAddAllClick(Button view) {
        if (mPresenter.hasUsersInvitedAutomatically()) {
            mPresenter.removeAutomaticallyAddedUsers();
            setAddAllButtonState(true);
        } else {
            mPresenter.addAllUsers();
            setAddAllButtonState(false);
        }
    }

    @Override
    public void showAddedUsers(List<User> users) {
        mAddedPeopleLayout.showUsers(users);
    }

    @OnTextChanged(R.id.et_search_people)
    public void onSearchFilterChanged(Editable editableString) {
        final List<User> alreadyAddedUsers = mAddedPeopleLayout.getUsers();
        mPresenter.getUsersAndFilterByFullName(editableString.toString(), alreadyAddedUsers);
    }

    @Override
    public void addAllUsers(List<User> results) {
        showAddedUsers(results);
        mAdapter.setAllChecked(true);
    }

    @Override
    public void successfulClose() {
        finish();
    }

    @Override
    public void showEmptyChannelNameError() {
        showToast(getString(R.string.error_channel_name_empty));
    }

    @OnClick(R.id.added_people_layout)
    public void onAddedUsersPanelClick() {
        final List<String> alreadyAddedUsersIds = Stream.of(mAddedPeopleLayout.getUsers())
                .map(User::getId)
                .collect(Collectors.toList());
        final Intent intent = AddedMembersActivity.getIntent(this, (ArrayList<String>) alreadyAddedUsersIds);
        startActivityForResult(intent, REQUEST_ADDED_MEMBERS_DIALOG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADDED_MEMBERS_DIALOG: {
                if (resultCode == RESULT_OK) {
                    final ArrayList<String> addedUsersIds = data.getStringArrayListExtra(AddedMembersActivity.USERS_IDS_KEY);
                    mPresenter.showAlreadyAddedUsers(addedUsersIds);
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
