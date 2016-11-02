package com.applikey.mattermost.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.applikey.mattermost.views.TabBehavior;

import java.util.List;

public class ChatListPagerAdapter extends FragmentStatePagerAdapter {

    // We skip undefined page. This should be changed later on after we introduce disabling unread page
    private final int pageCount = TabBehavior.values().length - 1;
    private List<Fragment> mPagerTabs;

    public ChatListPagerAdapter(FragmentManager fm, List<Fragment> tabs) {
        super(fm);
        mPagerTabs = tabs;
    }

    public void setTabs(List<Fragment> tabs) {
        mPagerTabs = tabs;
        notifyDataSetChanged();
    }

    public void addTab(Fragment fragment) {
        mPagerTabs.add(fragment);
        notifyDataSetChanged();
    }

    public void addTab(int index, Fragment tab) {
        mPagerTabs.add(index, tab);
        notifyDataSetChanged();
    }

    public void removeTab(Fragment tab) {
        mPagerTabs.remove(tab.getId());
        notifyDataSetChanged();
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
        return mPagerTabs.get(position).getClass().getSimpleName().toString();
    }
}
