package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.AddedMembersPresenter;
import com.applikey.mattermost.mvp.views.AddedMembersView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddedMembersActivity extends BaseMvpActivity implements AddedMembersView {

    private static final String USERS_IDS_KEY = "USERS_IDS_KEY";

    @InjectPresenter
    AddedMembersPresenter mPresenter;

    @Bind(R.id.rv_added_members)
    RecyclerView mRvAddedMembers;

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
        mPresenter.getUsersByIds(usersIds);
    }

    @Override
    public void showAddedMembers(List<User> users) {

    }
}
