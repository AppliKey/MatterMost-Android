package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.MessageDetailsPresenter;
import com.applikey.mattermost.mvp.views.MessageDetailsView;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.views.LinkTextView;
import com.applikey.mattermost.views.SafeButton;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageDetailsActivity extends BaseMvpActivity implements MessageDetailsView {

    public static final String KEY_POST_ID = Constants.PACKAGE_NAME + "activities.message.details.activity.post.id";

    @InjectPresenter
    MessageDetailsPresenter mPresenter;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.iv_preview_image) ImageView mIvPreviewImage;
    @Bind(R.id.iv_status_bg) ImageView mIvStatusBg;
    @Bind(R.id.iv_status) ImageView mIvStatus;
    @Bind(R.id.tv_user_name) TextView mTvUserName;
    @Bind(R.id.tv_message) LinkTextView mTvMessage;
    @Bind(R.id.tv_timestamp) TextView mTvTimestamp;
    @Bind(R.id.btn_go_to_dialog) SafeButton mGoToDialogButton;
    @Bind(R.id.iv_preview_image_layout) RelativeLayout mIvPreviewImageLayout;

    private Message mMessage;

    public static Intent getIntent(Context context, String postId) {
        Intent intent = new Intent(context, MessageDetailsActivity.class);
        intent.putExtra(KEY_POST_ID, postId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        ButterKnife.bind(this);
        initToolbar();
        mPresenter.initMessage(getIntent().getStringExtra(KEY_POST_ID));
    }

    private void initToolbar(){
        setSupportActionBar(mToolbar);
        setTitle(R.string.message_details);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    @OnClick(R.id.btn_go_to_dialog)
    public void onGoToDialogClick() {
        mPresenter.onGoToDialogButtonClick(mMessage.getChannel());
    }

    @Override
    public void initView(Message message) {
        mMessage = message;
        final User user = message.getUser();
        final Post post = message.getPost();
        final Channel channel = message.getChannel();
        setChannelIcon(mImageLoader, channel);
        setStatusIcon(channel);
        mTvUserName.setText(User.getDisplayableName(user));
        mTvMessage.setText(post.getMessage());
        mTvTimestamp.setText(TimeUtil.formatDateTime(post.getCreatedAt()));
    }

    public void startChatView(Channel channel) {
        startActivity(ChatActivity.getIntent(this, channel));
    }

    private void setChannelIcon(ImageLoader imageLoader, Channel element) {
        final User member = element.getDirectCollocutor();
        final String previewImagePath = member != null ? member.getProfileImage() : null;
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
        } else {
            mIvPreviewImage.setImageResource(R.drawable.no_resource);
        }
    }

    private void setStatusIcon(Channel channel) {
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(channel.getType())) {
            final User member = channel.getDirectCollocutor();
            final User.Status status = member != null ? User.Status.from(member.getStatus()) : null;
            if (status != null) {
                mIvStatus.setImageResource(status.getDrawableId());
            }
            mIvStatusBg.setVisibility(View.VISIBLE);
            mIvStatus.setVisibility(View.VISIBLE);
        } else {
            mIvStatusBg.setVisibility(View.GONE);
            mIvStatus.setVisibility(View.GONE);
        }
    }
}
