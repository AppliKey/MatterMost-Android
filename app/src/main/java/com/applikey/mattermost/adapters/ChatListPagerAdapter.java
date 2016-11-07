package com.applikey.mattermost.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import static com.applikey.mattermost.Constants.EMPTY_STRING;

public class ChatListPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mPagerTabs;

    public ChatListPagerAdapter(FragmentManager fm, List<Fragment> tabs) {
        super(fm);
        mPagerTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        // Indexes should equal to tab behavior indexes
        return mPagerTabs.get(position);
    }

    @Override
    public int getCount() {
        return mPagerTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EMPTY_STRING;
    }
}
