package com.applikey.mattermost.views;

import com.applikey.mattermost.R;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;


public class CustomPrimaryDrawerItem extends PrimaryDrawerItem {

    @Override
    public int getLayoutRes() {
        return R.layout.item_drawer_primary;
    }
}
