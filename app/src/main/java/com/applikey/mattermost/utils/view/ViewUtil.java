package com.applikey.mattermost.utils.view;

import android.view.ViewGroup;

import com.annimon.stream.Stream;

public final class ViewUtil {

    private ViewUtil() {

    }

    public static void setEnabledInDept(ViewGroup viewGroup, boolean enabled) {
        viewGroup.setEnabled(enabled);
        Stream.range(0, viewGroup.getChildCount())
                .map(viewGroup::getChildAt)
                .forEach(child -> {
                    child.setEnabled(enabled);
                    if (child instanceof ViewGroup) {
                        setEnabledInDept((ViewGroup) child, enabled);
                    }
                });
    }
}
