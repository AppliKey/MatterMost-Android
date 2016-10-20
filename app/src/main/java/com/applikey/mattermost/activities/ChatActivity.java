package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.PostAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.PostDto;
import com.applikey.mattermost.mvp.presenters.ChatPresenter;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.web.ErrorHandler;
import com.applikey.mattermost.web.images.ImageLoader;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    @InjectPresenter
    ChatPresenter mPresenter;

    @Inject
    @Named("currentUserId")
    String mCurrentUserId;

    @Inject
    ImageLoader mImageLoader;

    private PostAdapter mAdapter;

    private String mChannelId;
    private String mChannelName;
    private String mChannelType;
    private boolean mLoading;
    private boolean mIsNeedToScrollToStart = true;

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

        mAdapter = new PostAdapter(mCurrentUserId, mImageLoader);
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

    private void initParameters() {
        final Bundle extras = getIntent().getExtras();
        mChannelId = extras.getString(CHANNEL_ID_KEY);
        mChannelName = extras.getString(CHANNEL_NAME_KEY);
        mChannelType = extras.getString(CHANNEL_TYPE_KEY);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRvMessages.setLayoutManager(linearLayoutManager);
        mRvMessages.addOnScrollListener(getPaginationScrollListener());
        mRvMessages.setAdapter(mAdapter);
    }

    private RecyclerView.OnScrollListener getPaginationScrollListener() {
        return new RecyclerView.OnScrollListener() {
            private int pastVisibleItems;
            private int visibleItemCount;
            private int totalItemCount;
            private final int threshold = 5;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (dy < 0) {
                    Timber.d("");
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    Timber.d("visibleItems = %d, totalItems = %d, pastVisibleItems = %d", visibleItemCount, totalItemCount, pastVisibleItems);

                    if (!mLoading) {
                        if ((visibleItemCount + threshold + pastVisibleItems) >= totalItemCount) {
                            Timber.d("requesting %d items", totalItemCount);
                            mLoading = true;
                            mPresenter.fetchData(mChannelId);
                        }
                    }

                }
            }
        };
    }

    @Override
    public void onDataFetched() {
        Log.d(ChatActivity.class.getSimpleName(), "Data Fetched");
        mLoading = false;

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
    }

    private void hideEmptyState() {
        mRvMessages.setVisibility(VISIBLE);
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
}
