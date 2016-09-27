package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.applikey.mattermost.fragments.BaseChannelListFragment;
import com.applikey.mattermost.fragments.ChannelListFragment;
import com.applikey.mattermost.fragments.EmptyChatListFragment;
import com.applikey.mattermost.fragments.GroupListFragment;
import com.applikey.mattermost.mvp.presenters.ChannelsListPresenter;
import com.applikey.mattermost.mvp.presenters.GroupsListPresenter;

import static com.applikey.mattermost.fragments.BaseChannelListFragment.TabBehavior.getItemBehavior;

public class ChatListPagerAdapter extends FragmentPagerAdapter {

    // We skip undefined page. This should be changed later on after we introduce disabling unread page
    private final int pageCount = BaseChannelListFragment.TabBehavior.values().length - 1;

    private final Context mContext;

    public ChatListPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        mContext = context;
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
