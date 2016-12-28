package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PeopleToNewChannelAdapter;
import com.applikey.mattermost.mvp.presenters.BaseEditChannelPresenter;
import com.applikey.mattermost.mvp.presenters.CreateChannelPresenter;
import com.applikey.mattermost.mvp.views.CreateChannelView;
import com.applikey.mattermost.views.ChannelTypeView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateChannelActivity extends BaseEditChannelActivity
        implements CreateChannelView, PeopleToNewChannelAdapter.OnUserChosenListener {

    @InjectPresenter
    CreateChannelPresenter mPresenter;

    @BindView(R.id.channel_type_view)
    ChannelTypeView mChannelTypeView;

    @BindView(R.id.btn_add_all)
    CheckedTextView mChBtnAddAll;

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

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_create_group_or_channel;
    }

    private void createChannel() {
        showLoadingDialog();
        final String channelName = mEtChannelName.getText().toString().trim();
        final String channelDescription = mEtChannelDescription.getText().toString().trim();
        final boolean isPublicChannel = !mChannelTypeView.isChecked();
        mPresenter.createChannel(channelName, channelDescription, isPublicChannel);

    }

    @Override
    public void onChannelCreated() {
        hideLoadingDialog();
        finish();
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


    @OnClick(R.id.btn_add_all)
    public void onClickButtonAddAll(CheckedTextView chBtnAddAll) {
        if (chBtnAddAll.isChecked()) {
            chBtnAddAll.setText(R.string.cancel);
            getPresenter().inviteAll();
        } else {
            chBtnAddAll.setText(R.string.button_add_all);
            getPresenter().revertInviteAll();
        }
        chBtnAddAll.setChecked(!chBtnAddAll.isChecked());
    }

}

