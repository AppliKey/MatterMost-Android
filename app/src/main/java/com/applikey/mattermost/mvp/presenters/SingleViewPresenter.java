package com.applikey.mattermost.mvp.presenters;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import java.util.Set;

/**
 * Simplifies getting view. Assumes that we don't care if there are multiple views
 * attached and get the random one.
 */
/* package */ abstract class SingleViewPresenter<T extends MvpView> extends MvpPresenter<T> {

    protected T getView() {
        final Set<T> attachedViews = getAttachedViews();
        if (attachedViews.size() == 0) {
            throw new RuntimeException("Please attach view");
        }
        return attachedViews.iterator().next();
    }
}
