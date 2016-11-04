package com.applikey.mattermost.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Anatoliy Chub
 */

public class ChannelViewHolder extends RecyclerView.ViewHolder {

    private final View mRoot;

    @Bind(R.id.iv_preview_image1)
    ImageView mIvPreviewImage1;
    @Bind(R.id.iv_preview_image2)
    ImageView mIvPreviewImage2;
    @Bind(R.id.iv_preview_image3)
    ImageView mIvPreviewImage3;
    @Bind(R.id.iv_preview_image4)
    ImageView mIvPreviewImage4;
    @Bind(R.id.table)
    TableLayout mTable;
    @Bind(R.id.tv_message)
    TextView mTvMessage;
    @Bind(R.id.tv_date)
    TextView mTvDate;
    @Bind(R.id.tv_channel_name)
    TextView mTvChannelName;

    public ChannelViewHolder(View itemView) {
        super(itemView);

        mRoot = itemView;

        ButterKnife.bind(this, itemView);
    }

    public View getRoot() {
        return mRoot;
    }


    public ImageView getIvPreviewImage1() {
        return mIvPreviewImage1;
    }

    public ImageView getIvPreviewImage2() {
        return mIvPreviewImage2;
    }

    public ImageView getIvPreviewImage3() {
        return mIvPreviewImage3;
    }

    public ImageView getIvPreviewImage4() {
        return mIvPreviewImage4;
    }

    public TextView getChannelName() {
        return mTvChannelName;
    }

    public TextView getTvMessage() {
        return mTvMessage;
    }
}