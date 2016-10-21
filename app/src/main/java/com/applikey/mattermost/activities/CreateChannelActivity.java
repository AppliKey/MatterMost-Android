package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.views.ChannelTypeView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateChannelActivity extends BaseMvpActivity implements CreateChannelView{
    @Bind(R.id.et_channel_name)
    EditText mEtChannelName;

    @Bind(R.id.et_channel_description)
    EditText mEtChannelDescription;

    @Bind(R.id.et_search_people)
    EditText mEtSearchPeople;

    @Bind(R.id.rv_added_peoples)
    RecyclerView mRvAddedPeoples;

    @Bind(R.id.channel_type_view)
    ChannelTypeView mChannelTypeView;

    @InjectPresenter
    CreateChannelPresenter mPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, CreateChannelActivity.class);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_or_channel);
        ButterKnife.bind(this);
        //mSwitchChannelType.setOnCheckedChangeListener();

    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();

    }

    @Override
    public void showMembers(List<User> members) {

    }


}
