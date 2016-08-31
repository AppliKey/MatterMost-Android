package com.applikey.skeletonproject.storage.preferences;


import android.support.annotation.Nullable;

public interface Prefs {


    @Nullable
    String getCurrentUserId();

    void setCurrentUserId(@Nullable String id);
}
