package com.applikey.mattermost.views;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

/**
 * @author Anatoliy Chub
 */

public abstract class TabSelectedListener extends TabLayout.ViewPagerOnTabSelectedListener {

    protected int selectedTabColor = -1;
    protected int unSelectedTabColor = -1;

    protected TabSelectedListener(ViewPager viewPager) {
        super(viewPager);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        super.onTabSelected(tab);
        final Drawable icon = tab.getIcon();
        if (icon != null) {
            icon.setColorFilter(getSelectedTabColor(), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        super.onTabUnselected(tab);
        final Drawable icon = tab.getIcon();
        if (icon != null) {
            icon.setColorFilter(getUnSelectedTabColor(), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        super.onTabReselected(tab);
        final Drawable icon = tab.getIcon();
        if (icon != null) {
            icon.setColorFilter(getSelectedTabColor(), PorterDuff.Mode.SRC_IN);
        }
    }

    protected abstract int getSelectedTabColor();

    protected abstract int getUnSelectedTabColor();
}
