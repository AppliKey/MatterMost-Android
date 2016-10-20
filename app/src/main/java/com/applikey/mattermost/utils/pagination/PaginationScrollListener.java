package com.applikey.mattermost.utils.pagination;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal;
    private static final int THRESHOLD = 3;
    private boolean loading;

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
        if (loading) {
            if (totalItemCount != previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        if (!loading && (lastVisibleItem + THRESHOLD) > totalItemCount) {
            onLoad();
            loading = true;
        }

    }

    public abstract void onLoad();

}
