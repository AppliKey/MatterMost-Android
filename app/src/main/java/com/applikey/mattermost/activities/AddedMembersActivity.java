package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.user.UserListParcelableWrapper;
import com.applikey.mattermost.mvp.presenters.AddedMembersPresenter;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddedMembersActivity extends BaseMvpActivity
        implements AddedMembersView, PeopleToNewChannelAdapter.OnUserChosenListener,
        SearchView.OnQueryTextListener {

    /* package */ static final String USERS_IDS_KEY = "USERS_IDS_KEY";
    private static final String EDITABLE_KEY = "EDITABLE_KEY";

    @InjectPresenter
    AddedMembersPresenter mPresenter;

    @Bind(R.id.rv_added_members)
    RecyclerView mRvAddedMembers;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private PeopleToNewChannelAdapter mAdapter;
    private boolean mEditable;

    // FIXME: 11.11.16 Save ids instead of Users
    public static Intent getIntent(Context context, List<User> alreadyAddedUsers, boolean editable) {
        final Intent intent = new Intent(context, AddedMembersActivity.class);
        final UserListParcelableWrapper wrapper = new UserListParcelableWrapper(alreadyAddedUsers);
        intent.putExtra(USERS_IDS_KEY, wrapper);
        intent.putExtra(EDITABLE_KEY, editable);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_members);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        final Bundle args = getIntent().getExtras();
        final UserListParcelableWrapper wrapper = args.getParcelable(USERS_IDS_KEY);
        mEditable = args.getBoolean(EDITABLE_KEY);
        final List<User> alreadyAddedUsers = wrapper.getData();
        mPresenter.setData(alreadyAddedUsers);
        //TODO implement disable plus button
        mAdapter = new PeopleToNewChannelAdapter(this, mImageLoader);
        mRvAddedMembers.setAdapter(mAdapter);
        setTitle(R.string.added_members_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_added_members, menu);
        return true;
    }

    @Override
    public void showAddedMembers(List<User> users) {
        mAdapter.addAlreadyAddedUsers(users);
    }

    @Override
    public void showUsers(List<User> users) {
        mAdapter.addUsers(users);
    }

    @Override
    public void onChosen(User user) {
        mPresenter.handleUser(user);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem searchViewMenuItem = menu.findItem(R.id.action_search_added_members);
        final SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        final UserListParcelableWrapper wrapper = new UserListParcelableWrapper(mPresenter.getInvitedUsers());
        intent.putExtra(USERS_IDS_KEY, wrapper);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mPresenter.filterByFullName(newText);
        return true;
    }

    @Override
    public void addInvitedUser(User user) {
        mAdapter.addAlreadyAddedUser(user);
    }

    @Override
    public void removeInvitedUser(User user) {
        mAdapter.removeAlreadyAddedUser(user);
    }
}
