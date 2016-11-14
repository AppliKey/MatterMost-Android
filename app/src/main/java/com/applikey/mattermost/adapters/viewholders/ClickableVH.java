package com.applikey.mattermost.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.applikey.mattermost.utils.RecyclerItemClickListener;

/**
 * @author Anatoliy Chub
 */

public class ClickableVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = ClickableVH.class.getSimpleName();

    private RecyclerItemClickListener.OnItemClickListener mClickListener;

    public ClickableVH(View itemView) {
        super(itemView);
    }

    public void setClickListener(RecyclerItemClickListener.OnItemClickListener itemClickListener){
        itemView.setOnClickListener(this);
        mClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        mClickListener.onItemClick(v, getAdapterPosition());
    }
}
