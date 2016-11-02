package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    private static final String TAG = ChannelAdapter.class.getSimpleName();

    private List<Channel> mDataSet = new ArrayList<>();
    private ImageLoader mImageLoader;
    private ClickListener mClickListener = null;

    public ChannelAdapter(ImageLoader imageLoader) {
        super();

        mImageLoader = imageLoader;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_channel, parent, false);

        final ViewHolder vh = new ViewHolder(v);
        vh.getRoot().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final Channel data = mDataSet.get(position);

        vh.getChannelName().setText(data.getDisplayName());

        setChannelIcon(vh, data);

        vh.getRoot().setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(List<Channel> dataSet) {
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

    private void setChannelIcon(ViewHolder viewHolder, Channel element) {

    }


    public interface ClickListener {

        void onItemClicked(Channel channel);
    }

    private final View.OnClickListener mOnClickListener = v -> {
        final int position = (Integer) v.getTag();

        final Channel channel = mDataSet.get(position);

        if (mClickListener != null) {
            mClickListener.onItemClicked(channel);
        }
    };

    /* package */
    class ViewHolder extends RecyclerView.ViewHolder {

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

        ViewHolder(View itemView) {
            super(itemView);

            mRoot = itemView;

            ButterKnife.bind(this, itemView);
        }

        View getRoot() {
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

        TextView getChannelName() {
            return mTvChannelName;
        }

    }
}
