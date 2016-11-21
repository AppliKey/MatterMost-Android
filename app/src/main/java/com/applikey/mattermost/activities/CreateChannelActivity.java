package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.MenuItem;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.mvp.presenters.BaseEditChannelPresenter;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.arellomobile.mvp.presenter.InjectPresenter;

public class CreateChannelActivity extends BaseEditChannelActivity
        implements CreateChannelView, PeopleToNewChannelAdapter.OnUserChosenListener {

    @InjectPresenter
    CreateChannelPresenter mPresenter;


    public static Intent getIntent(Context context) {
        return new Intent(context, CreateChannelActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChannelTypeView.setOnCheckedChangedListener((view, checked) -> {
            @StringRes final int title = checked
                    ? R.string.new_private_group
                    : R.string.new_public_channel;
            @StringRes final int purposeHint = checked
                    ? R.string.create_private_group_description_hint
                    : R.string.create_public_channel_description_hint;
            CreateChannelActivity.this.setTitle(getResources().getString(title));
            mEtChannelDescription.setHint(purposeHint);
        });
        setTitle(getString(R.string.new_private_group));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_channel) {
            createChannel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected BaseEditChannelPresenter getPresenter() {
        return mPresenter;
    }

    private void createChannel() {
        final String channelName = mEtChannelName.getText().toString().trim();
        final String channelDescription = mEtChannelDescription.getText().toString().trim();
        final boolean isPublicChannel = !mChannelTypeView.isChecked();
        mPresenter.createChannel(channelName, channelDescription, isPublicChannel);

    }

    @Override
    public void onChannelCreated() {
        finish();
    }

}

