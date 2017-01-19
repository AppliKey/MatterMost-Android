package com.applikey.mattermost.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PostAdapter;
import com.applikey.mattermost.fragments.ImageAttachmentDialogFragment;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.ChatPresenter;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.image.ImagePathHelper;
import com.applikey.mattermost.utils.kissUtils.utils.FileUtil;
import com.applikey.mattermost.utils.pagination.PaginationScrollListener;
import com.applikey.mattermost.utils.view.ViewUtil;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import timber.log.Timber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatActivity extends DrawerActivity implements ChatView {

    private static final String CHANNEL_ID_KEY = "channel-id";
    private static final String CHANNEL_TYPE_KEY = "channel-type";
    private static final String CHANNEL_LAST_VIEWED_KEY = "channel-last-viewed";
    private static final String CHANNEL_NAME = "channel-name";
    private static final String ACTION_JOIN_TO_CHANNEL_KEY = "join-to-channel";

    private static final String DIALOG_TAG_IMAGE_ATTACHMENT = "dialog-attachment-image";

    private static final int PICK_FILE_REQUEST_CODE = 451;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.srl_chat)
    SwipeRefreshLayout mSrlChat;

    @BindView(R.id.rv_messages)
    RecyclerView mRvMessages;

    @BindView(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @BindView(R.id.et_message)
    EmojiEditText mEtMessage;

    @BindView(R.id.ll_reply)
    LinearLayout mLlReply;

    @BindView(R.id.view_reply_separator)
    View mViewReplySeparator;

    @BindView(R.id.iv_reply_close)
    ImageView mIvReplyClose;

    @BindView(R.id.tv_reply_message)
    TextView mTvReplyMessage;

    @BindView(R.id.iv_emoji)
    ImageView mIvEmojicon;

    @BindView(R.id.root_view)
    View rootView;

    @BindView(R.id.btn_join_channel)
    Button mBtnJoinChat;

    @BindView(R.id.tv_join_offer)
    TextView mTvJoinOffer;

    @BindView(R.id.join_layout)
    LinearLayout mJoinLayout;

    @BindView(R.id.l_message)
    LinearLayout mMessageLayout;

    @BindView(R.id.chat_layout)
    ViewGroup mChatLayout;

    @BindView(R.id.loading_progress_bar)
    MaterialProgressBar mLoadingProgressBar;

    @BindView(R.id.hsv_attachments)
    HorizontalScrollView mAttachmentsRoot;

    @BindView(R.id.ll_attachments)
    LinearLayout mAttachmentsLayout;

    @InjectPresenter
    ChatPresenter mPresenter;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ImageLoader mImageLoader;

    @Inject
    ImagePathHelper mImagePathHelper;

    @Inject
    Prefs mPrefs;

    @Inject
    ErrorHandler mErrorHandler;

    private String mRootId;

    private String mChannelId;

    private String mChannelType;

    private long mChannelLastViewed;

    private PostAdapter mAdapter;

    private EmojiPopup mEmojiPopup;

    private boolean mIsJoined;

    private RxPermissions mRxPermissions;

    private DownloadManager mDownloadManager;

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
        mRxPermissions = RxPermissions.getInstance(this);

        mEmojiPopup = EmojiPopup.Builder
                .fromRootView(rootView)
                .setOnSoftKeyboardCloseListener(() -> mEmojiPopup.dismiss())
                .setOnEmojiPopupShownListener(() -> mIvEmojicon.setSelected(true))
                .setOnEmojiPopupDismissListener(() -> mIvEmojicon.setSelected(false))
                .build(mEtMessage);

        initParameters();
        mPresenter.getInitialData(mChannelId);

        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();

        Timber.w("ChatActivity onStart");

        mPresenter.fetchAfterRestart();

        if (!mIsJoined) {
            final String channelName = getIntent().getStringExtra(CHANNEL_NAME);
            showJoiningInterface(channelName);
        } else {
            mPresenter.loadMessages(mChannelId);
        }
    }

    public void showJoiningInterface(String channelName) {
        mSrlChat.setVisibility(GONE);
        mJoinLayout.setVisibility(VISIBLE);

        ViewUtil.setEnabledInDept(mChatLayout, false);

        mTvJoinOffer.setText(getString(R.string.join_offer, channelName));
    }

    @Override
    public void showLoading(boolean show) {
        mLoadingProgressBar.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void onDataReady(RealmResults<Post> posts, boolean listenUpdates) {
        final Channel.ChannelType channelType = Channel.ChannelType.fromRepresentation(mChannelType);

        final String currentTeamId = mPrefs.getCurrentTeamId();

        mAdapter = new PostAdapter(this, posts, mCurrentUserId, currentTeamId, mImageLoader, mImagePathHelper,
                channelType, mChannelLastViewed, mOnPostLongClick, mDefaultAttachmentClickListener,
                mImageAttachmentClickListener, listenUpdates);

        mRvMessages.addOnScrollListener(mPaginationListener);
        mRvMessages.setAdapter(mAdapter);
        mRvMessages.setHasFixedSize(true);

        mSrlChat.setOnRefreshListener(() -> mPresenter.fetchNextPage(mAdapter.getItemCount()));
    }

    @Override
    public void subscribeForMessageChanges() {
        mAdapter.enableAutoUpdates();
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
    public void clearAttachmentsInput() {
        mAttachmentsLayout.removeAllViews();
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
        if (mIsJoined) {
            startActivity(ChannelDetailsActivity.getIntent(this, channel));
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            final String uri = data.getDataString();
            final String filePath = FileUtil.getPath(this, Uri.parse(uri));

            mPresenter.pickAttachment(filePath);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showAddingAttachment(String filePath, String fileName) {
        final LayoutInflater inflater = getLayoutInflater();
        final View root = inflater.inflate(R.layout.list_item_attachment, null);

        final ImageView closeButton = (ImageView) root.findViewById(R.id.iv_attachment_close_button);
        final TextView name = (TextView) root.findViewById(R.id.tv_attachment_name);

        name.setText(fileName);

        closeButton.setOnClickListener(v -> {
            mPresenter.removePickedAttachment(filePath);
            ((ViewGroup) root.getParent()).removeView(root);
        });

        mAttachmentsLayout.addView(root);
    }

    @OnClick(R.id.iv_send_message)
    void onSend() {
        mPresenter.sendMessage(mChannelId, mEtMessage.getText().toString(), mRootId);
    }

    @OnClick(R.id.iv_attach)
    void onAttach() {
        mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (!granted) {
                        // notify user
                        Toast.makeText(this, R.string.please_grant_permission, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
                }, mErrorHandler::handleError);
    }

    @OnClick(R.id.btn_join_channel)
    void onClickJoinChat() {
        mPresenter.joinChannel(mChannelId);
    }

    @OnClick(R.id.iv_emoji)
    void onClickEmoji() {
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

    @Override
    public void downloadFile(DownloadManager.Request downloadRequest, String fileName) {
        downloadRequest.setDestinationInExternalFilesDir(this,
                Environment.DIRECTORY_DOWNLOADS, fileName);
        mDownloadManager.enqueue(downloadRequest);
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
        mIsJoined = getIntent().getBooleanExtra(ACTION_JOIN_TO_CHANNEL_KEY, false);
        mChannelId = getIntent().getStringExtra(CHANNEL_ID_KEY);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_shevron);
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
            mToolbar.setOnClickListener(v -> mPresenter.openChannelDetails());
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

    private final View.OnClickListener mImageAttachmentClickListener = v -> {
        final String url = (String) v.getTag();

        if (url != null) {
            ImageAttachmentDialogFragment.newInstance(url).show(getSupportFragmentManager(),
                    DIALOG_TAG_IMAGE_ATTACHMENT);
        }
    };

    private final View.OnClickListener mDefaultAttachmentClickListener = v -> {
        final String url = (String) v.getTag();

        if (url != null) {
            mRxPermissions.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(granted -> {
                        if (!granted) {
                            // notify user
                            Toast.makeText(this, R.string.please_grant_permission, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mPresenter.requestDownload(url);
                    }, mErrorHandler::handleError);
        }
    };

    private final RecyclerView.OnScrollListener mPaginationListener = new PaginationScrollListener() {
        @Override
        public void onLoad() {
            mPresenter.fetchNextPage(mAdapter.getItemCount());
        }
    };

    private final PostAdapter.OnLongClickListener mOnPostLongClick = (post, isOwner) -> {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if (!post.isSent()) {
            dialogBuilder.setItems(R.array.post_own_opinion_fail_array, (dialog, which) -> {
                if (which == 0) {
                    mPresenter.sendMessage(mChannelId, post.getMessage(), null, post.getId());
                } else if (which == 1) {
                    deleteMessage(mChannelId, post);
                }
            });
        } else if (isOwner) {
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
