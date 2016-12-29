package com.applikey.mattermost.utils;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.PresentersCounter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Date: 14-Nov-16
 * Time: 04:39
 *
 * @author Yuri Shmakov
 */
@SuppressWarnings("WeakerAccess")
public class FixedPresentersCounter extends PresentersCounter {

    private final Map<MvpPresenter<?>, Set<String>> mConnections = new HashMap<>();

    /**
     * Save delegate tag when it inject presenter to delegate's object
     *
     * @param presenter Injected presenter
     * @param delegateTag Delegate tag
     */
    @Override
    public void injectPresenter(MvpPresenter<?> presenter, String delegateTag) {
        Set<String> delegateTags = mConnections.get(presenter);
        if (delegateTags == null) {
            delegateTags = new HashSet<>();
            mConnections.put(presenter, delegateTags);
        }

        delegateTags.add(delegateTag);
    }

    /**
     * Remove tag when delegate's object was fully destroyed
     *
     * @param presenter Rejected presenter
     * @param delegateTag Delegate tag
     *
     * @return True if there are no links to this presenter and presenter be able to destroy. False otherwise
     */
    @Override
    public boolean rejectPresenter(MvpPresenter<?> presenter, String delegateTag) {
        final Set<String> delegateTags = mConnections.get(presenter);
        if (delegateTags == null) {
            mConnections.remove(presenter);
            return true;
        }
        delegateTags.remove(delegateTag);

        final boolean noTags = delegateTags.isEmpty();

        if (noTags) {
            mConnections.remove(presenter);
        }

        return noTags;
    }

    @SuppressWarnings("unused")
    @Override
    public boolean isInjected(MvpPresenter<?> presenter) {
        final Set<String> mDelegateTags = mConnections.get(presenter);

        return mDelegateTags != null && !mDelegateTags.isEmpty();
    }
}