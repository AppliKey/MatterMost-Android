package com.applikey.mattermost.views;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class InsetItemDecoration extends RecyclerView.ItemDecoration {

    private int mInset;

    public InsetItemDecoration(int inset) {
        this.mInset = inset;
    }

    @Override
    public void getItemOffsets(Rect outRect,
            View view,
            RecyclerView parent,
            RecyclerView.State state) {
        final RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
        if (holder != null) {
            final RecyclerView.Adapter adapter = parent.getAdapter();
            if (holder.getAdapterPosition() == 0) {
                outRect.top = mInset;
                return;
            } else if (holder.getAdapterPosition() == adapter.getItemCount() - 1) {
                outRect.bottom = mInset;
                return;
            }
        }
        super.getItemOffsets(outRect, view, parent, state);
    }
}
