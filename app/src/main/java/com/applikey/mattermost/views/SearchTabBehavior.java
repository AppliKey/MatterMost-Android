package com.applikey.mattermost.views;

import android.support.annotation.DrawableRes;

import com.applikey.mattermost.R;

public enum SearchTabBehavior {

    UNDEFINED {
        @Override
        public int getIcon() {
            return R.drawable.no_resource;
        }
    },
    ALL {
        @Override
        public int getIcon() {
            return R.drawable.ic_hamburger;
        }
    },
    UNREAD {
        @Override
        public int getIcon() {
            return R.drawable.ic_unread;
        }
    },
    CHANNELS {
        @Override
        public int getIcon() {
            return R.drawable.ic_public_channels_tab;
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

    public static SearchTabBehavior getItemBehavior(int pageIndex) {
        // Offset should be introduced
        return SearchTabBehavior.values()[pageIndex + 1];
    }
}
