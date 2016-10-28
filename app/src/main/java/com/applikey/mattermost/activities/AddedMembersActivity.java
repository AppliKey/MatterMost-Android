package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.models.channel.UserPendingInvitation;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.AddedMembersPresenter;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddedMembersActivity extends BaseMvpActivity implements AddedMembersView, PeopleToNewChannelAdapter.OnUserChosenListener {

    public static final String USERS_IDS_KEY = "USERS_IDS_KEY";

    @InjectPresenter
    AddedMembersPresenter mPresenter;

    @Bind(R.id.rv_added_members)
    RecyclerView mRvAddedMembers;

    private PeopleToNewChannelAdapter mAdapter;

    public static Intent getIntent(Context context, ArrayList<String> usersIds) {
        final Intent intent = new Intent(context, AddedMembersActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList(USERS_IDS_KEY, usersIds);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_people);
        ButterKnife.bind(this);
        final List<String> usersIds = getIntent().getStringArrayListExtra(USERS_IDS_KEY);
        mAdapter = new PeopleToNewChannelAdapter(this, mImageLoader);
        mRvAddedMembers.setAdapter(mAdapter);
        mPresenter.getUsersByIds(usersIds);
        setTitle(R.string.added_members_title);
    }

    @Override
    public void showAddedMembers(List<UserPendingInvitation> users) {
        mAdapter.addUsers(users);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        intent.putStringArrayListExtra(USERS_IDS_KEY, mPresenter.getIds());
        setResult(RESULT_OK, intent);
        finish();
    }
}
