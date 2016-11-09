package com.applikey.mattermost.views;

import android.support.annotation.DrawableRes;

import com.applikey.mattermost.R;

public enum TabBehavior {

    UNDEFINED {
        @Override
        public int getIcon() {
            return R.drawable.no_resource;
        }
    },
    UNREAD {
        @Override
        public int getIcon() {
            return R.drawable.ic_unread;
        }
    },
    FAVOURITES {
        @Override
        public int getIcon() {
            return R.drawable.ic_favourites_tab;
        }
    },
    CHANNELS {
        @Override
        public int getIcon() {
            return R.drawable.ic_public_channels_tab;
        }
    },
    GROUPS {
        @Override
        public int getIcon() {
            return R.drawable.ic_private_channels_tab;
        }
    },
    DIRECT {
        @Override
        public int getIcon() {
            return R.drawable.ic_direct_tab;
        }
    };

    @DrawableRes
    public abstract int getIcon();

    public static TabBehavior getItemBehavior(int pageIndex) {
        // Offset should be introduced
        return TabBehavior.values()[pageIndex + 1];
    }
}
