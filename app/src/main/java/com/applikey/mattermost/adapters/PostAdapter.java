package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.RealmString;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.image.ImagePathHelper;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import io.realm.SubscribeableRealmRecyclerViewAdapter;

public class PostAdapter extends SubscribeableRealmRecyclerViewAdapter<Post, PostAdapter.ViewHolder> {

    private static final int MY_POST_VIEW_TYPE = 0;
    private static final int OTHERS_POST_VIEW_TYPE = 1;

    private final String mCurrentUserId;
    private final String mCurrentTeamId;
    private final ImageLoader mImageLoader;
    private final ImagePathHelper mImagePathHelper;
    private final OnLongClickListener mOnLongClickListener;
    private final Channel.ChannelType mChannelType;

    private long mLastViewed;
    private String mNewMessageIndicatorId = "";

    public PostAdapter(Context context,
                       RealmResults<Post> posts,
                       String currentUserId,
                       String currentTeamId,
                       ImageLoader imageLoader,
                       ImagePathHelper imagePathHelper,
                       Channel.ChannelType channelType,
                       long lastViewed,
                       OnLongClickListener onLongClickListener,
                       boolean autoUpdate) {
        super(context, posts, autoUpdate);
        mCurrentUserId = currentUserId;
        mCurrentTeamId = currentTeamId;
        mImageLoader = imageLoader;
        mImagePathHelper = imagePathHelper;
        mChannelType = channelType;
        mLastViewed = lastViewed;
        mOnLongClickListener = onLongClickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int index) {
        final Post item = getItem(index);
        return item != null ? item.hashCode() : 0;
    }

    public void setLastViewed(long lastViewed) {
        mLastViewed = lastViewed;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final int layoutId = viewType == MY_POST_VIEW_TYPE
                ? R.layout.list_item_post_my : R.layout.list_item_post_other;

        final View itemView = inflater.inflate(layoutId, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.mTvTimestamp.setOnClickListener(v ->
                viewHolder.toggleDate(
                        getItem(viewHolder.getAdapterPosition())));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Post post = getData().get(position);

        final boolean isLastPost = position == getData().size() - 1;
        final boolean isFirstPost = position == 0;

        Post previousPost = null;
        Post nextPost = null;

        if (!isLastPost) {
            final int nextPostPosition = position + 1;
            final Post nextPostTemp = getData().get(nextPostPosition);
            nextPost = nextPostTemp != null ? nextPostTemp : null;
        }

        if (!isFirstPost) {
            final int previousPostPosition = position - 1;
            final Post previousPostTemp = getData().get(previousPostPosition);
            previousPost = previousPostTemp != null ? previousPostTemp : null;
        }

        final boolean showDate = isLastPost || !isPostsSameDate(post, nextPost);
        final boolean showAuthor = isLastPost || showDate || !isPostsSameAuthor(nextPost, post);
        final boolean showTime = isFirstPost || !isPostsSameSecond(post, previousPost)
                || !isPostsSameAuthor(post, previousPost);

        final boolean mNewMessageIndicatorShowed = !TextUtils.isEmpty(mNewMessageIndicatorId);
        final boolean showNewMessageIndicator = (!mNewMessageIndicatorShowed &&
                mLastViewed < post.getCreatedAt() &&
                !isLastPost && nextPost.getCreatedAt() < mLastViewed) ||
                TextUtils.equals(mNewMessageIndicatorId, post.getId());

        if (showNewMessageIndicator) {
            mNewMessageIndicatorId = post.getId();
        }

        holder.bindHeader(showNewMessageIndicator, showDate);

        final boolean isMy = isMy(post);
        holder.bindAttachments(context, post, isMy, mCurrentTeamId, mImageLoader, mImagePathHelper);

        if (isMy) {
            holder.bindOwnPost(mChannelType, post, showAuthor, showTime, mOnLongClickListener);
        } else {
            holder.bindOtherPost(mChannelType, post, showAuthor, showTime, mImageLoader, mOnLongClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final Post post = getItem(position);
        return isMy(post) ? MY_POST_VIEW_TYPE : OTHERS_POST_VIEW_TYPE;
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    private boolean isPostsSameAuthor(Post post, Post nextPost) {
        return !(post == null || nextPost == null)
                && post.getUserId().equals(nextPost.getUserId());
    }

    private boolean isPostsSameSecond(Post post, Post nextPost) {
        return !(post == null || nextPost == null)
                && TimeUtil.sameTime(post.getCreatedAt(), nextPost.getCreatedAt());
    }

    private boolean isPostsSameDate(Post post, Post nextPost) {
        return !(post == null || nextPost == null)
                && TimeUtil.sameDate(post.getCreatedAt(), nextPost.getCreatedAt());
    }

    @FunctionalInterface
    public interface OnLongClickListener {

        void onLongClick(Post post, boolean isPostOwner);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.iv_status)
        ImageView mIvStatus;

        @Nullable
        @BindView(R.id.iv_preview_image)
        ImageView mIvPreviewImage;

        @Nullable
        @BindView(R.id.iv_preview_image_layout)
        FrameLayout mIvPreviewImageLayout;

        @BindView(R.id.tv_date)
        TextView mTvDate;

        @BindView(R.id.tv_message)
        TextView mTvMessage;

        @BindView(R.id.tv_timestamp)
        TextView mTvTimestamp;

        @BindView(R.id.tv_new_message)
        TextView mTvNewMessage;

        @BindView(R.id.tv_name)
        TextView mTvName;

        @BindView(R.id.tv_reply_message)
        TextView mTvReplyMessage;

        @Nullable
        @BindView(R.id.iv_fail)
        ImageView mIvFail;

        @BindView(R.id.attachments_container)
        LinearLayout mAttachmentsContainer;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void setFailStatusVisible(boolean visible) {
            if (mIvFail != null) {
                mIvFail.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }

        void toggleDate(Post post) {
            final String time;
            if (mTvTimestamp.length() > TimeUtil.DEFAULT_FORMAT_TIME_ONLY.length()) {
                time = TimeUtil.formatTimeOnly(post.getCreatedAt());
            } else {
                time = TimeUtil.formatDateTime(post.getCreatedAt());
            }
            TransitionManager.beginDelayedTransition((ViewGroup) itemView);
            mTvTimestamp.setText(time);
        }

        void bindOwnPost(Channel.ChannelType channelType,
                         Post post,
                         boolean showAuthor,
                         boolean showTime,
                         OnLongClickListener onLongClickListener) {
            bind(channelType, post, showAuthor, showTime);

            itemView.setOnLongClickListener(v -> {
                onLongClickListener.onLongClick(post, true);
                return true;
            });
            mTvName.setText(R.string.you);

            setFailStatusVisible(!post.isSent());
        }

        void bindOtherPost(Channel.ChannelType channelType,
                           Post post,
                           boolean showAuthor,
                           boolean showTime,
                           ImageLoader imageLoader,
                           OnLongClickListener onLongClickListener) {
            bind(channelType, post, showAuthor, showTime);

            final User author = post.getAuthor();

            final String previewImagePath = author.getProfileImage();
            if (mIvPreviewImageLayout != null && mIvPreviewImage != null
                    && mIvStatus != null && previewImagePath != null
                    && !previewImagePath.isEmpty()) {
                imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
                mIvStatus.setImageResource(User.Status.from(author.getStatus()).getDrawableId());
                mIvPreviewImageLayout.setVisibility(showAuthor ? View.VISIBLE : View.INVISIBLE);
            }

            itemView.setOnLongClickListener(v -> {
                onLongClickListener.onLongClick(post, false);
                return true;
            });

            if (mIvPreviewImageLayout != null && channelType == Channel.ChannelType.DIRECT) {
                mIvPreviewImageLayout.setVisibility(View.GONE);
            }
        }

        private void bindHeader(boolean showNewMessageIndicator, boolean showDate) {
            mTvDate.setVisibility(showDate ? View.VISIBLE : View.GONE);
            mTvNewMessage.setVisibility(showNewMessageIndicator ? View.VISIBLE : View.GONE);
        }

        private void bindAttachments(Context context, Post post, boolean isMy, String currentTeamId,
                                     ImageLoader imageLoader, ImagePathHelper imagePathHelper) {
            mAttachmentsContainer.removeAllViews();
            mAttachmentsContainer.setVisibility(post.getFilenames().isEmpty() ? View.GONE : View.VISIBLE);
            for (RealmString filename : post.getFilenames()) {
                final LinearLayout container = new LinearLayout(itemView.getContext());
                final LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                container.setOrientation(LinearLayout.HORIZONTAL);
                final ImageView thumbnail = new ImageView(container.getContext());
                final int iconSize = (int) context.getResources().getDimension(R.dimen.attachment_icon_size);
                final int iconPadding = (int) context.getResources().getDimension(R.dimen.attachment_icon_padding);
                final LinearLayout.LayoutParams thumbnailParams =
                        new LinearLayout.LayoutParams(iconSize, iconSize);
                thumbnail.setLayoutParams(thumbnailParams);
                thumbnail.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
                thumbnail.setImageDrawable(ContextCompat.getDrawable(thumbnail.getContext(),
                        R.drawable.ic_attach_grey));
                container.addView(thumbnail);
                final TextView name = new TextView(container.getContext());
                final LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                name.setTextColor(ContextCompat.getColor(name.getContext(), isMy ? R.color.colorDisabled :
                        R.color.textPrimary));
                final String filenameValue = filename.getValue();
                final String extractedFileName = StringUtil.extractFileName(filenameValue);
                name.setText(extractedFileName);
                name.setTag(filenameValue);
                nameParams.gravity = Gravity.CENTER_VERTICAL;
                name.setLayoutParams(nameParams);
                container.addView(name);
                container.setLayoutParams(containerParams);
                mAttachmentsContainer.addView(container);

                if (imagePathHelper.isImage(extractedFileName)) {
                    final String imagePath = imagePathHelper.getAttachmentImageUrl(currentTeamId, filenameValue);
                    if (imagePath != null) {
                        imageLoader.displayThumbnailImage(imagePath, thumbnail);
                    }
                }
            }
        }

        private void bind(Channel.ChannelType channelType,
                          Post post,
                          boolean showAuthor,
                          boolean showTime) {
            mTvDate.setText(TimeUtil.formatDateOnly(post.getCreatedAt()));
            mTvTimestamp.setText(TimeUtil.formatTimeOnly(post.getCreatedAt()));
            mTvName.setText(User.getDisplayableName(post.getAuthor()));

            mTvMessage.setVisibility(TextUtils.isEmpty(post.getMessage()) ? View.GONE : View.VISIBLE);
            mTvMessage.setText(post.getMessage());
            mTvMessage.setOnLongClickListener(v -> {
                itemView.performLongClick();
                return true;
            });

            mTvName.setVisibility(showAuthor ? View.VISIBLE : View.GONE);
            mTvTimestamp.setVisibility(showTime ? View.VISIBLE : View.GONE);

            if (channelType == Channel.ChannelType.DIRECT) {
                mTvName.setVisibility(View.GONE);
            }

            if (post.getRootPost() != null) {
                mTvReplyMessage.setVisibility(View.VISIBLE);
                mTvReplyMessage.setText(post.getRootPost().getMessage());
            } else {
                mTvReplyMessage.setVisibility(View.GONE);
                mTvReplyMessage.setText(null);
            }
        }
    }
}
