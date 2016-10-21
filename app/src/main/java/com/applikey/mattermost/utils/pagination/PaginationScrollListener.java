package com.applikey.mattermost.utils.pagination;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private int mPreviousTotal;
    private static final int THRESHOLD = 3;
    private boolean mLoading;

    private LinearLayoutManager mLayoutManager;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mLayoutManager == null) {
            mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }
        final int visibleItemCount = recyclerView.getChildCount();
        final int totalItemCount = mLayoutManager.getItemCount();
        final int firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
        final int lastVisibleItem = firstVisibleItem + visibleItemCount;
        if (mLoading) {
            if (totalItemCount != mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }

        if (!mLoading && (lastVisibleItem + THRESHOLD) > totalItemCount) {
            onLoad();
            mLoading = true;
        }

    }

    public abstract void onLoad();

}
