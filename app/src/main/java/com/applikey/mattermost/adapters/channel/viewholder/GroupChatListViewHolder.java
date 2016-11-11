package com.applikey.mattermost.adapters.channel.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.views.CircleCounterView;

import butterknife.Bind;

public class GroupChatListViewHolder extends BaseChatListViewHolder {

    @Bind(R.id.iv_first)
    ImageView mIvFirst;

    @Bind(R.id.iv_second)
    ImageView mIvSecond;

    @Bind(R.id.iv_third)
    ImageView mIvThird;

    @Bind(R.id.iv_fourth)
    ImageView mIvFourth;

    @Bind(R.id.ccv_fourth)
    CircleCounterView mCcvFourth;

    public GroupChatListViewHolder(View itemView) {
        super(itemView);
    }

    public ImageView getFirstImageView() {
        return mIvFirst;
    }

    public ImageView getSecondImageView() {
        return mIvSecond;
    }

    public ImageView getThirdImageView() {
        return mIvThird;
    }

    public ImageView getFourthImageView() {
        return mIvFourth;
    }

    public CircleCounterView getCounterView() {
        return mCcvFourth;
    }
}
