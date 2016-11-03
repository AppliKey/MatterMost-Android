package com.applikey.mattermost.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author Anatoliy Chub
 */

public interface SearchItem {


    @Retention(SOURCE)
    @IntDef({CHANNEL, USER})
    @interface Type {}

    int USER = 0;
    int CHANNEL = 1;

    @Type
    int getSearchType();

}
