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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PostAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostDto;
import com.applikey.mattermost.mvp.presenters.ChatPresenter;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.utils.pagination.PaginationScrollListener;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ChatActivity extends BaseMvpActivity implements ChatView {

    private static final String CHANNEL_ID_KEY = "channel-id";
    private static final String CHANNEL_NAME_KEY = "channel-name";
    private static final String CHANNEL_TYPE_KEY = "channel-type";

    private static final String CHANNEL_PREFIX = "#";
    private static final String DIRECT_PREFIX = "";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.srl_chat)
    SwipeRefreshLayout mSrlChat;

    @Bind(R.id.rv_messages)
    RecyclerView mRvMessages;

    @Bind(R.id.tv_empty_state)
    TextView mTvEmptyState;

    @InjectPresenter
    ChatPresenter mPresenter;

    @Inject
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String mCurrentUserId;

    @Inject
    ImageLoader mImageLoader;

    private PostAdapter mAdapter;

    private String mChannelId;
    private String mChannelName;
    private String mChannelType;
    private boolean mIsNeedToScrollToStart = true;


    private final RecyclerView.OnScrollListener mPaginationListener = new PaginationScrollListener() {
        @Override
        public void onLoad() {
            mPresenter.fetchData(mChannelId);
        }
    };

    public static Intent getIntent(Context context, Channel channel) {
        final Intent intent = new Intent(context, ChatActivity.class);

        final Bundle bundle = new Bundle();
        bundle.putString(CHANNEL_ID_KEY, channel.getId());
        bundle.putString(CHANNEL_NAME_KEY, channel.getDisplayName());
        bundle.putString(CHANNEL_TYPE_KEY, channel.getType());

        intent.putExtras(bundle);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        App.getComponent().inject(this);
        ButterKnife.bind(this);

        mAdapter = new PostAdapter(mCurrentUserId, mImageLoader, onPostLongClick);
        mSrlChat.setOnRefreshListener(() -> mPresenter.fetchData(mChannelId));

        initParameters();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPresenter.getInitialData(mChannelId);
        mPresenter.fetchData(mChannelId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setToolbarText();
    }

    @Override
    public void onDataFetched() {
        Log.d(ChatActivity.class.getSimpleName(), "Data Fetched");

    }

    @Override
    public void onPostDeleted(Post post) {
        mAdapter.deletePost(post);
    }

    @Override
    public void onPostUpdated(Post post) {
        mAdapter.updatePost(post);
    }

    @Override
    public void displayData(List<PostDto> posts) {
        Log.d(ChatActivity.class.getSimpleName(), "Data Displayed");

        displayPosts(posts);
    }

    @Override
    public void onFailure(Throwable cause) {
        ErrorHandler.handleError(cause);
    }

    private void displayEmptyState() {
        mRvMessages.setVisibility(GONE);
        mTvEmptyState.setVisibility(VISIBLE);
    }

    private void hideEmptyState() {
        mRvMessages.setVisibility(VISIBLE);
        mTvEmptyState.setVisibility(GONE);
    }

    private void setToolbarText() {
        final String prefix = !mChannelType.equals(Channel.ChannelType.DIRECT.getRepresentation())
                ? CHANNEL_PREFIX : DIRECT_PREFIX;

        mToolbar.setTitle(prefix + mChannelName);
    }

    private void displayPosts(List<PostDto> posts) {
        if (posts == null || posts.isEmpty()) {
            displayEmptyState();
            return;
        }
        mAdapter.updateDataSet(posts);
        if (mIsNeedToScrollToStart) {
            mRvMessages.scrollToPosition(0);
            mIsNeedToScrollToStart = false;
        }
        hideEmptyState();
    }

    @Override
    public void showProgress(boolean enabled) {
        mSrlChat.setRefreshing(enabled);
    }

    private final PostAdapter.OnLongClickListener onPostLongClick = post -> {
        final AlertDialog.Builder opinionDialogBuilder = new AlertDialog.Builder(this);
        opinionDialogBuilder.setItems(R.array.post_own_opinion_array, (dialog, which) -> {
            switch (which) {
                case 0:
                    deleteMessage(mChannelId, post);
                    break;
                case 1:
                    editMessage(mChannelId, post);
                    break;
                default:
                    throw new RuntimeException("Not implemented feature");
            }
        }).show();
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
                    post.setMessage(input.getText().toString());
                    mPresenter.editMessage(mChannelId, post);
                })
                .show();
    }

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        mChannelId = extras.getString(CHANNEL_ID_KEY);
        mChannelName = extras.getString(CHANNEL_NAME_KEY);
        mChannelType = extras.getString(CHANNEL_TYPE_KEY);
    }

    private LinearLayoutManager getLayoutManager() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        return linearLayoutManager;
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mRvMessages.addOnScrollListener(mPaginationListener);
        final ActionBar actionBar = getSupportActionBar();
        mRvMessages.setLayoutManager(getLayoutManager());
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        mRvMessages.setAdapter(mAdapter);
    }
}
