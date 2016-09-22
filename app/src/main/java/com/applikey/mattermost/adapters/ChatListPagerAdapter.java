package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.applikey.mattermost.fragments.ChatListFragment;

import static com.applikey.mattermost.fragments.ChatListFragment.TabBehavior.getItemBehavior;

public class ChatListPagerAdapter extends FragmentPagerAdapter {

    // We skip undefined page. This should be changed later on after we introduce disabling unread page
    private final int pageCount = ChatListFragment.TabBehavior.values().length - 1;

    private final Context mContext;

    public ChatListPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return ChatListFragment.newInstance(getItemBehavior(position));
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
