package com.applikey.mattermost.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.applikey.mattermost.fragments.ChannelListFragment;
import com.applikey.mattermost.fragments.EmptyChatListFragment;
import com.applikey.mattermost.fragments.GroupListFragment;
import com.applikey.mattermost.fragments.DirectChatListFragment;
import com.applikey.mattermost.fragments.UnreadChatListFragment;
import com.applikey.mattermost.views.TabBehavior;

public class ChatListPagerAdapter extends FragmentPagerAdapter {

    // We skip undefined page. This should be changed later on after we introduce disabling unread page
    private final int pageCount = TabBehavior.values().length - 1;

    public ChatListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Indexes should equal to tab behavior indexes
        switch (position) {
            case 0: {
                return UnreadChatListFragment.newInstance();
            }
            case 2: {
                return ChannelListFragment.newInstance();
            }
            case 3: {
                return GroupListFragment.newInstance();
            }
            case 4: {
                return DirectChatListFragment.newInstance();
            }
            default: {
                return EmptyChatListFragment.newInstance();
            }
        }
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
