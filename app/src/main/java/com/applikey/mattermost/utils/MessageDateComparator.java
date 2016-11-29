package com.applikey.mattermost.utils;

import com.applikey.mattermost.models.SearchItem;

import java.util.Comparator;

public class MessageDateComparator implements Comparator<SearchItem> {

    @Override
    public int compare(SearchItem o1, SearchItem o2) {
        final int priorityDifference = o2.getSortPriority() - o1.getSortPriority();

        if (priorityDifference != 0) {
            return priorityDifference;
        }

        return o1.compareByDate(o2);
    }
}
