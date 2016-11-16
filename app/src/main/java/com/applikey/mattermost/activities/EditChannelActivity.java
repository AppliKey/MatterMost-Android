package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.mvp.presenters.BaseEditChannelPresenter;
import com.applikey.mattermost.mvp.presenters.EditChannelPresenter;
import com.applikey.mattermost.mvp.views.EditChannelView;
import com.arellomobile.mvp.presenter.InjectPresenter;

/**
 * @author Denis Kolesnik
 * @since 16.11.16
 */

public class EditChannelActivity extends BaseEditChannelActivity implements EditChannelView {

    private static final String CHANNEL_ID_KEY = "channel-id";

    @InjectPresenter
    EditChannelPresenter mPresenter;

    public static Intent getIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, EditChannelActivity.class);
        intent.putExtra(CHANNEL_ID_KEY, channel.getId());
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChannelTypeView.setOnCheckedChangedListener((view, checked) -> {
            @StringRes final int title = checked
                    ? R.string.edit_private_group
                    : R.string.edit_public_channel;
            @StringRes final int purposeHint = checked
                    ? R.string.create_private_group_description_hint
                    : R.string.create_public_channel_description_hint;
            EditChannelActivity.this.setTitle(getResources().getString(title));
            mEtChannelDescription.setHint(purposeHint);
        });
        mChannelTypeView.setEnabled(false);
        setTitle(getString(R.string.edit_private_group));
        initParameters();
    }

    @Override
    public void showChannelData(Channel channel) {
        mEtChannelName.setText(channel.getDisplayName());
        mEtChannelDescription.setText(channel.getPurpose());
        mChannelTypeView.setChecked(
                channel.getType().equals(Channel.ChannelType.PRIVATE.getRepresentation()));
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        final String channelId = extras.getString(CHANNEL_ID_KEY);
        mPresenter.getInitialData(channelId);
    }

    @Override
    protected BaseEditChannelPresenter getPresenter() {
        return mPresenter;
    }
}
