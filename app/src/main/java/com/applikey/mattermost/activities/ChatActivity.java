package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;

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
    private static final String CHANNEL_NAME_KEY = "channel-name";
    private static final String CHANNEL_TYPE_KEY = "channel-type";
    private static final String CHANNEL_LAST_VIEWED_KEY = "channel-last-viewed";

    private static final String CHANNEL_PREFIX = "#";
    private static final String DIRECT_PREFIX = "";

    private static final int MENU_ITEM_SEARCH = Menu.FIRST;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.srl_chat)
    SwipeRefreshLayout mSrlChat;

    @Bind(R.id.rv_messages)
    RecyclerView mRvMessages;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @Bind(R.id.et_message)
    EditText mEtMessage;

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

    private String mRootId;

    private String mChannelId;
    private String mChannelName;
    private String mChannelType;
    private long mChannelLastViewed;
    private PostAdapter mAdapter;

    private final RecyclerView.OnScrollListener mPaginationListener = new PaginationScrollListener() {
        @Override
        public void onLoad() {
            mPresenter.fetchData(mChannelId);
        }
    };

    public static Intent getIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(CHANNEL_ID_KEY, channel.getId());
        intent.putExtra(CHANNEL_NAME_KEY, channel.getDisplayName());
        intent.putExtra(CHANNEL_TYPE_KEY, channel.getType());
        intent.putExtra(CHANNEL_LAST_VIEWED_KEY, channel.getLastViewedAt());
        return intent;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        App.getUserComponent().inject(this);
        ButterKnife.bind(this);

        initParameters();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.getInitialData(mChannelId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setToolbarText();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_SEARCH, Menu.NONE, R.string.search)
                .setIcon(R.drawable.ic_search)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_SEARCH:
                //presenter.search()
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDataReady(RealmResults<Post> posts) {
        final Channel.ChannelType channelType = Channel.ChannelType.fromRepresentation(mChannelType);
        mAdapter = new PostAdapter(this, posts, mCurrentUserId, mImageLoader,
                channelType, mChannelLastViewed, onPostLongClick);

        mSrlChat.setOnRefreshListener(() -> mPresenter.fetchData(mChannelId));

        mRvMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        mRvMessages.addOnScrollListener(mPaginationListener);
        mRvMessages.setAdapter(mAdapter);

        if (posts.size() > 0) {
            hideEmptyState();
        } else {
            displayEmptyState();
        }
    }

    @Override
    public void onDataFetched() {
        Log.d(ChatActivity.class.getSimpleName(), "Data Fetched");
    }

    @OnClick(R.id.iv_send_message)
    public void onSend() {
        if (mRootId == null) {
            mPresenter.sendMessage(mChannelId, mEtMessage.getText().toString());
        } else {
            mPresenter.sendReplyMessage(mChannelId, mEtMessage.getText().toString(), mRootId);
        }
    }

    @Override
    public void onMessageSent(long createdAt) {
        mEtMessage.setText(null);
        mAdapter.setLastViewed(createdAt);
        scrollToStart();
        hideReply();
    }

    @Override
    public void showProgress(boolean enabled) {
        mSrlChat.setRefreshing(enabled);
    }

    private void scrollToStart() {
        mRvMessages.scrollToPosition(0);
    }

    private void displayEmptyState() {
        mRvMessages.setVisibility(GONE);
        mTvEmptyState.setVisibility(VISIBLE);
    }

    private void hideEmptyState() {
        mRvMessages.setVisibility(VISIBLE);
        mTvEmptyState.setVisibility(GONE);
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

    private void setToolbarText() {
        final String prefix = !mChannelType.equals(Channel.ChannelType.DIRECT.getRepresentation())
                ? CHANNEL_PREFIX : DIRECT_PREFIX;

        mToolbar.setTitle(prefix + mChannelName);
    }

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

    private void deleteMessage(String channelId, Post post) {
        mPresenter.deleteMessage(channelId, post);
    }

    private void editMessage(String channelId, Post post) {
        final EditText input = new EditText(this);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(layoutParams);
        input.setText(post.getMessage());

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input)
                .setTitle(R.string.edit_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    mPresenter.editMessage(channelId, post, input.getText().toString());
                })
                .show();
    }

    private void replyMessage(Post post) {
        displayReply();
        mTvReplyMessage.setText(post.getMessage());
        mRootId = post.getId();
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        mChannelId = extras.getString(CHANNEL_ID_KEY);
        mChannelName = extras.getString(CHANNEL_NAME_KEY);
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
            mToolbar.setOnClickListener(v -> {
                mPresenter.channelNameClick();
            });
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

    @Override
    public void openChannelDetails(Channel channel) {
        startActivity(ChannelDetailsActivity.getIntent(this, channel));
    }

    @Override
    public void openUserProfile(User user) {
        startActivity(UserProfileActivity.getIntent(this, user));
    }
}
