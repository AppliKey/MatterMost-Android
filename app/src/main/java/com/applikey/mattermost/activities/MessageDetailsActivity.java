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
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.MessageDetailsPresenter;
import com.applikey.mattermost.mvp.views.MessageDetailsView;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.views.LinkTextView;
import com.applikey.mattermost.views.SafeButton;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageDetailsActivity extends BaseMvpActivity implements MessageDetailsView {

    private static final String KEY_POST_ID = Constants.PACKAGE_NAME + "activities.message.details.activity.post.id";

    @InjectPresenter
    MessageDetailsPresenter mPresenter;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.iv_preview_image) ImageView mIvPreviewImage;
    @BindView(R.id.iv_status_bg) ImageView mIvStatusBg;
    @BindView(R.id.iv_status) ImageView mIvStatus;
    @BindView(R.id.tv_user_name) TextView mTvUserName;
    @BindView(R.id.tv_message) LinkTextView mTvMessage;
    @BindView(R.id.tv_timestamp) TextView mTvTimestamp;
    @BindView(R.id.btn_go_to_dialog) SafeButton mGoToDialogButton;
    @BindView(R.id.iv_preview_image_layout) RelativeLayout mIvPreviewImageLayout;

    public static Intent getIntent(Context context, String postId) {
        final Intent intent = new Intent(context, MessageDetailsActivity.class);
        intent.putExtra(KEY_POST_ID, postId);
        return intent;
    }

    @ProvidePresenter
    MessageDetailsPresenter providePresenter() {
        return new MessageDetailsPresenter(getIntent().getStringExtra(KEY_POST_ID));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        ButterKnife.bind(this);
        initToolbar();
    }

    @OnClick(R.id.btn_go_to_dialog)
    public void onGoToDialogClick() {
        mPresenter.onGoToDialogButtonClick();
    }

    @Override
    public void setAuthorInfo(User user) {
        mTvUserName.setText(User.getDisplayableName(user));
        setUserAvatar(mImageLoader, user);
    }

    @Override
    public void setPostInfo(Post post) {
        mTvMessage.setText(post.getMessage());
        mTvTimestamp.setText(TimeUtil.formatDateTime(post.getCreatedAt()));
    }

    @Override
    public void setUserStatus(User user) {
        final User.Status status = user != null ? User.Status.from(user.getStatus()) : null;
        if (status != null) {
            mIvStatus.setImageResource(status.getDrawableId());
        }
        mIvStatusBg.setVisibility(View.VISIBLE);
        mIvStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void startChatView(Channel channel) {
        startActivity(ChatActivity.getIntent(this, channel));
    }

    private void setUserAvatar(ImageLoader imageLoader, User user) {
        final String previewImagePath = user != null ? user.getProfileImage() : null;
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
        } else {
            mIvPreviewImage.setImageResource(R.drawable.no_resource);
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        setTitle(R.string.message_details);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}
