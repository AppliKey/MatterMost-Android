package com.applikey.mattermost.models;

import android.support.annotation.StringRes;

import com.applikey.mattermost.R;

// TODO: 29.11.16 It needs refactoring. We should use composition instead inheritance
public interface SearchItem {

    int PRIORITY_USER = 0;

    int PRIORITY_MESSAGE = 1;

    int PRIORITY_CHANNEL = 2;

    enum Type {
        CHANNEL(R.string.header_channel),
        USER(R.string.header_people),
        MESSAGE(R.string.header_message),
        MESSAGE_CHANNEL(R.string.header_message);

        public int getRes() {
            return res;
        }

        private int res;

        Type(@StringRes int res) {
            this.res = res;
        }
    }

    Type getSearchType();

    int getSortPriority();

    int compareByDate(SearchItem item);
}
