package com.applikey.mattermost.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.applikey.mattermost.fragments.BaseChatListFragment;
import com.applikey.mattermost.fragments.ChannelListFragment;
import com.applikey.mattermost.fragments.EmptyChatListFragment;
import com.applikey.mattermost.fragments.GroupListFragment;
import com.applikey.mattermost.fragments.DirectChatListFragment;

public class ChatListPagerAdapter extends FragmentPagerAdapter {

    // We skip undefined page. This should be changed later on after we introduce disabling unread page
    private final int pageCount = BaseChatListFragment.TabBehavior.values().length - 1;

    public ChatListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
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
