package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PostAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.ChatPresenter;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.utils.pagination.PaginationScrollListener;
import com.applikey.mattermost.utils.view.ViewUtil;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatActivity extends DrawerActivity implements ChatView {

    private static final String CHANNEL_ID_KEY = "channel-id";
    private static final String CHANNEL_TYPE_KEY = "channel-type";
    private static final String CHANNEL_LAST_VIEWED_KEY = "channel-last-viewed";
    private static final String CHANNEL_NAME = "channel-name";
    private static final String ACTION_JOIN_TO_CHANNEL_KEY = "join-to-channel";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.srl_chat)
    SwipeRefreshLayout mSrlChat;

    @Bind(R.id.rv_messages)
    RecyclerView mRvMessages;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @Bind(R.id.et_message)
    EmojiEditText mEtMessage;

    @Bind(R.id.ll_reply)
    LinearLayout mLlReply;

    @Bind(R.id.view_reply_separator)
    View mViewReplySeparator;

    @Bind(R.id.iv_reply_close)
    ImageView mIvReplyClose;

    @Bind(R.id.tv_reply_message)
    TextView mTvReplyMessage;

    @InjectPresenter
    ChatPresenter mPresenter;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ImageLoader mImageLoader;

    @Bind(R.id.iv_emoji)
    ImageView mIvEmojicon;

    @Bind(R.id.root_view)
    View rootView;

    @Bind(R.id.btn_join_channel)
    Button mBtnJoinChat;

    @Bind(R.id.tv_join_offer)
    TextView mTvJoinOffer;

    @Bind(R.id.join_layout)
    LinearLayout mJoinLayout;

    @Bind(R.id.l_message)
    LinearLayout mMessageLayout;

    @Bind(R.id.chat_layout)
    ViewGroup mChatLayout;

    private String mRootId;

    private String mChannelId;

    private String mChannelType;

    private long mChannelLastViewed;

    private PostAdapter mAdapter;

    private EmojiPopup mEmojiPopup;

    public static Intent getIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(CHANNEL_ID_KEY, channel.getId());
        intent.putExtra(CHANNEL_TYPE_KEY, channel.getType());
        intent.putExtra(CHANNEL_LAST_VIEWED_KEY, channel.getLastViewedAt());
        intent.putExtra(CHANNEL_NAME, channel.getDisplayName());
        intent.putExtra(ACTION_JOIN_TO_CHANNEL_KEY, channel.isJoined());
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        App.getUserComponent().inject(this);
        ButterKnife.bind(this);

        mEmojiPopup = EmojiPopup.Builder
                .fromRootView(rootView)
                .setOnSoftKeyboardCloseListener(() -> mEmojiPopup.dismiss())
                .setOnEmojiPopupShownListener(() -> mIvEmojicon.setSelected(true))
                .setOnEmojiPopupDismissListener(() -> mIvEmojicon.setSelected(false))
                .build(mEtMessage);

        final boolean inJoined = getIntent().getBooleanExtra(ACTION_JOIN_TO_CHANNEL_KEY, false);
        mChannelId = getIntent().getStringExtra(CHANNEL_ID_KEY);
        mPresenter.getInitialData(mChannelId);

        if (!inJoined) {
            final String channelName = getIntent().getStringExtra(CHANNEL_NAME);
            showJoiningInterface(channelName);
        } else {
            mPresenter.loadMessages(mChannelId);
        }

        initParameters();
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.fetchAfterRestart();
    }

    public void showJoiningInterface(String channelName) {
        mSrlChat.setVisibility(GONE);
        mJoinLayout.setVisibility(VISIBLE);

        ViewUtil.setEnabledInDept(mChatLayout, false);

        mTvJoinOffer.setText(getString(R.string.join_offer, channelName));
    }

    @Override
    public void onDataReady(RealmResults<Post> posts) {
        final Channel.ChannelType channelType = Channel.ChannelType.fromRepresentation(mChannelType);
        mAdapter = new PostAdapter(this, posts, mCurrentUserId, mImageLoader,
                                   channelType, mChannelLastViewed, onPostLongClick);

        mRvMessages.addOnScrollListener(mPaginationListener);
        mRvMessages.setAdapter(mAdapter);

        mSrlChat.setOnRefreshListener(() -> mPresenter.fetchNextPage(mAdapter.getItemCount()));
    }

    @Override

    public void showEmpty(boolean show) {
        mSrlChat.setVisibility(show ? GONE : VISIBLE);
        mTvEmptyState.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void showProgress() {
        mSrlChat.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mSrlChat.setRefreshing(false);
    }

    @Override
    public void onMessageSent(long createdAt) {
        mAdapter.setLastViewed(createdAt);
        scrollToStart();
        hideReply();
    }

    @Override
    public void clearMessageInput() {
        mEtMessage.getText().clear();
    }

    @Override
    public void onChannelJoined() {
        mSrlChat.setVisibility(VISIBLE);
        mJoinLayout.setVisibility(View.GONE);

        ViewUtil.setEnabledInDept(mChatLayout, true);

        mPresenter.loadMessages(mChannelId);
    }

    @Override
    public void openChannelDetails(Channel channel) {
        startActivity(ChannelDetailsActivity.getIntent(this, channel));
    }

    @Override
    public void openUserProfile(User user) {
        startActivity(UserProfileActivity.getIntent(this, user));
    }

    @Override
    public void onBackPressed() {
        if (mEmojiPopup != null && mEmojiPopup.isShowing()) {
            mEmojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected boolean showHamburger() {
        return false;
    }

    @OnClick(R.id.iv_send_message)
    void onSend() {
        if (mRootId == null) {
            mPresenter.sendMessage(mChannelId, mEtMessage.getText().toString());
        } else {
            mPresenter.sendReplyMessage(mChannelId, mEtMessage.getText().toString(), mRootId);
        }
    }

    @OnClick(R.id.btn_join_channel)
    public void onClickJoinChat() {
        mPresenter.joinToChannel(mChannelId);
    }

    @OnClick(R.id.iv_emoji)
    public void onClickEmoji() {
        mEmojiPopup.toggle();
    }

    private void scrollToStart() {
        mRvMessages.scrollToPosition(0);
    }

    private void displayReply() {
        mLlReply.setVisibility(VISIBLE);
        mViewReplySeparator.setVisibility(VISIBLE);
    }

    private void hideReply() {
        mLlReply.setVisibility(GONE);
        mViewReplySeparator.setVisibility(GONE);
        mTvReplyMessage.setText(null);
        mRootId = null;
    }

    @Override
    public void showTitle(String title) {
        mToolbar.setTitle(title);
    }

    private void deleteMessage(String channelId, Post post) {
        showDeletionDialog(channelId, post);
    }

    private void editMessage(String channelId, Post post) {
        final EditText input = new EditText(this);
        final FrameLayout frameLayout = new FrameLayout(this);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        final int editTextMargin = getResources().getDimensionPixelOffset(R.dimen.more);
        layoutParams.setMargins(editTextMargin, 0, editTextMargin, 0);
        input.setLayoutParams(layoutParams);
        frameLayout.addView(input);
        input.setText(post.getMessage());
        input.setSelection(post.getMessage().length());

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(frameLayout)
                .setTitle(R.string.edit_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    if (input.getText().length() > 0) {
                        mPresenter.editMessage(channelId, post, input.getText().toString());
                    } else {
                        showDeletionDialog(channelId, post);
                    }
                })
                .show();
    }

    private void showDeletionDialog(String channelId, Post post) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.post_delete)
                .setMessage(R.string.are_you_sure_you_want_to_delete_this_post)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog1, which1) -> mPresenter.deleteMessage(channelId, post))
                .setPositiveButton(R.string.delete,
                                   (dialog1, which1) -> mPresenter.deleteMessage(
                                           channelId, post))
                .show();
    }

    private void replyMessage(Post post) {
        displayReply();
        mTvReplyMessage.setText(post.getMessage());
        mRootId = post.getId();
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        mChannelType = extras.getString(CHANNEL_TYPE_KEY);
        mChannelLastViewed = extras.getLong(CHANNEL_LAST_VIEWED_KEY);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_shevron);
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
            mToolbar.setOnClickListener(v -> mPresenter.channelNameClick());
        }
        mRvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    return;
                }
                hideKeyboard();
            }
        });

        mIvReplyClose.setOnClickListener(v -> hideReply());
    }

    private final RecyclerView.OnScrollListener mPaginationListener = new PaginationScrollListener() {
        @Override
        public void onLoad() {
            mPresenter.fetchNextPage(mAdapter.getItemCount());
        }
    };

    private final PostAdapter.OnLongClickListener onPostLongClick = (post, isOwner) -> {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if (isOwner) {
            dialogBuilder.setItems(R.array.post_own_opinion_array, (dialog, which) -> {
                switch (which) {
                    case 0:
                        deleteMessage(mChannelId, post);
                        break;
                    case 1:
                        editMessage(mChannelId, post);
                        break;
                    case 2:
                        replyMessage(post);
                        break;
                    default:
                        throw new RuntimeException("Not implemented feature");
                }
            });
        } else {
            dialogBuilder.setItems(R.array.post_opinion_array, (dialog, which) -> {
                switch (which) {
                    case 0:
                        replyMessage(post);
                        break;
                    default:
                        throw new RuntimeException("Not implemented feature");
                }
            });
        }
        dialogBuilder.show();
    };
}
