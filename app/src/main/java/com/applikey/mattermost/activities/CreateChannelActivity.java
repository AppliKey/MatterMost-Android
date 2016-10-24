package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.applikey.mattermost.views.ChannelTypeView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @InjectPresenter
    CreateChannelPresenter mPresenter;

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
        mRvPeoples.setAdapter(mAdapter);
        setTitle(getString(R.string.new_public_channel));
        mChannelTypeView.setOnCheckedChangedListener((view, checked) -> {
            if (checked) {
                CreateChannelActivity.this.setTitle(getResources().getString(R.string.new_private_group));
            } else {
                CreateChannelActivity.this.setTitle(getResources().getString(R.string.new_public_channel));
            }
        });

    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();

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
    public void showUsers(List<UserPendingInvitation> results) {
        for (UserPendingInvitation user : results) {
            Timber.d("Name: %s %s, avatar: %s", user.getUser().getFirstName(), user.getUser().getLastName(), user.getUser().getProfileImage());
        }
        mAdapter.addUsers(results);
    }

    @Override
    public void showAddedUsers(List<User> users) {
        mAddedPeopleLayout.showUsers(users);
    }

    @Override
    public void showError(String message) {
        showToast(message);
    }
}
