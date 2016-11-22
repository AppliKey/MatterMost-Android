package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.BaseEditChannelPresenter;
import com.applikey.mattermost.mvp.presenters.EditChannelPresenter;
import com.applikey.mattermost.mvp.views.EditChannelView;
import com.applikey.mattermost.views.AddedPeopleLayout;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class EditChannelActivity extends BaseEditChannelActivity implements EditChannelView {

    private static final String CHANNEL_ID_KEY = "channel-id";

    @Bind(R.id.add_members_text)
    TextView mAddMembersText;

    @Bind(R.id.members_layout)
    AddedPeopleLayout mMembersLayout;

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
        initParameters();
        mMembersLayout.setImageLoader(mImageLoader);
    }

    @Override
    public void showChannelData(Channel channel) {
        mEtChannelName.setText(channel.getDisplayName());
        mEtChannelDescription.setText(channel.getPurpose());
        boolean isPrivate = channel.getType()
                .equals(Channel.ChannelType.PRIVATE.getRepresentation());
        @StringRes final int title = isPrivate
                ? R.string.edit_private_group
                : R.string.edit_public_channel;
        setTitle(getResources().getString(title));
    }

    @OnClick(R.id.btn_delete_channel)
    public void onDeleteChannelClick() {
        mPresenter.deleteChannel();
    }

    @Override
    protected BaseEditChannelPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_edit_group_or_channel;
    }

    @Override
    public void onChannelUpdated() {
        finish();
    }

    @Override
    public void onChannelDeleted() {
        final Intent intent = new Intent(this, ChatListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_channel) {
            updateChannel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showMembers(List<User> users) {
        mAdapter.setAlreadyMemberUsers(users);
        mMembersLayout.showUsers(users);
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        final String channelId = extras.getString(CHANNEL_ID_KEY);
        mPresenter.getInitialData(channelId);
    }

    private void updateChannel() {
        final String channelName = mEtChannelName.getText().toString().trim();
        final String channelDescription = mEtChannelDescription.getText().toString().trim();
        mPresenter.updateChannel(channelName, channelDescription);
    }
}
