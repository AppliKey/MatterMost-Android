package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.UserViewHolder;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private static final String TAG = UserAdapter.class.getSimpleName();

    private List<User> mDataSet = new ArrayList<>();
    private ImageLoader mImageLoader;
    private ClickListener mClickListener = null;

    public UserAdapter(ImageLoader imageLoader) {
        super();

        mImageLoader = imageLoader;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_user, parent, false);

        final UserViewHolder vh = new UserViewHolder(v);
        vh.getRoot().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder vh, int position) {
        final User data = mDataSet.get(position);

        vh.getChannelName().setText(User.getDisplayableName(data));

        setChannelIcon(vh, data);

        vh.getRoot().setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(List<User> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    public void setOnClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    private void setChannelIcon(UserViewHolder viewHolder, User element) {

        final String previewImagePath = element.getProfileImage();
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            mImageLoader.displayCircularImage(previewImagePath, viewHolder.getPreviewImage());
        }
    }


    public interface ClickListener {

        void onItemClicked(User user);
    }

    private final View.OnClickListener mOnClickListener = v -> {
        final int position = (Integer) v.getTag();

        final User user = mDataSet.get(position);

        if (mClickListener != null) {
            mClickListener.onItemClicked(user);
        }
    };

    /* package */

}
