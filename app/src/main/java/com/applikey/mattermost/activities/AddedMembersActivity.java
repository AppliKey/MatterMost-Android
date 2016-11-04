package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
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

public class AddedMembersActivity extends BaseMvpActivity
        implements AddedMembersView, PeopleToNewChannelAdapter.OnUserChosenListener,
        SearchView.OnQueryTextListener {

    public static final String USERS_IDS_KEY = "USERS_IDS_KEY";

    @InjectPresenter
    AddedMembersPresenter mPresenter;

    @Bind(R.id.rv_added_members)
    RecyclerView mRvAddedMembers;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private SearchView mSearchView;

    private PeopleToNewChannelAdapter mAdapter;

    public static Intent getIntent(Context context, ArrayList<String> usersIds) {
        final Intent intent = new Intent(context, AddedMembersActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putStringArrayList(USERS_IDS_KEY, usersIds);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_added_members, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem searchViewMenuItem = menu.findItem(R.id.action_search_added_members);
        mSearchView = (SearchView) searchViewMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
    public void onBackPressed() {
        final Intent intent = new Intent();
        final List<String> addedMembersIds = Stream.of(mPresenter.getResultingList())
                .map(User::getId)
                .collect(Collectors.toList());
        intent.putStringArrayListExtra(USERS_IDS_KEY, (ArrayList<String>) addedMembersIds);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mPresenter.getUsersAndFilterByFullName(newText);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_people);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        final List<String> usersIds = getIntent().getStringArrayListExtra(USERS_IDS_KEY);
        mAdapter = new PeopleToNewChannelAdapter(this, mImageLoader);
        mRvAddedMembers.setAdapter(mAdapter);
        mPresenter.getUsersByIds(usersIds);
        setTitle(R.string.added_members_title);
    }
}
