package com.applikey.mattermost.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.applikey.mattermost.utils.RecyclerItemClickListener;

public class ClickableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = ClickableViewHolder.class.getSimpleName();

    private RecyclerItemClickListener.OnItemClickListener mClickListener;

    public ClickableViewHolder(View itemView) {
        super(itemView);
    }

    public void setClickListener(RecyclerItemClickListener.OnItemClickListener itemClickListener) {
        itemView.setOnClickListener(this);
        mClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener == null) {
            return;
        }
        mClickListener.onItemClick(v, getAdapterPosition());
    }
}
