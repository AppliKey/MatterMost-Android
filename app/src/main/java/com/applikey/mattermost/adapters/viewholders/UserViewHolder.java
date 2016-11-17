package com.applikey.mattermost.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class UserViewHolder extends ClickableViewHolder {

    private final View mRoot;

    @Bind(R.id.iv_preview_image)
    ImageView mPreviewImage;

    @Bind(R.id.tv_channel_name)
    TextView mChannelName;

    public UserViewHolder(View itemView) {
        super(itemView);

        mRoot = itemView;

        ButterKnife.bind(this, itemView);
    }

    public View getRoot() {
        return mRoot;
    }

    public ImageView getPreviewImage() {
        return mPreviewImage;
    }


    public TextView getChannelName() {
        return mChannelName;
    }

}
