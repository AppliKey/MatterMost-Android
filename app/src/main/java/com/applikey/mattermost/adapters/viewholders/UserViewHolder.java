package com.applikey.mattermost.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.RecyclerItemClickListener;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserViewHolder extends ClickableViewHolder {

    @BindView(R.id.iv_preview_image)
    ImageView mPreviewImage;

    @BindView(R.id.tv_channel_name)
    TextView mChannelName;

    public UserViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public ImageView getPreviewImage() {
        return mPreviewImage;
    }

    @Override
    public TextView getName() {
        return mChannelName;
    }

    public void bind(ImageLoader imageLoader,RecyclerItemClickListener.OnItemClickListener listener , User data) {
        mChannelName.setText(User.getDisplayableName(data));
        setChannelIcon(imageLoader, data);
        setClickListener(listener);
    }

    private void setChannelIcon(ImageLoader imageLoader, User user) {
        final String previewImagePath = user != null ? user.getProfileImage() : null;
        final ImageView previewImage = mPreviewImage;
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            imageLoader.displayCircularImage(previewImagePath, previewImage);
        } else {
            previewImage.setImageResource(R.drawable.no_resource);
        }
    }

}
