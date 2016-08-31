package com.applikey.mattermost.storage.db;


import android.content.Context;

public class DbImpl implements DB {


    private Context mContext;

    public DbImpl(Context context) {
        mContext = context;
    }
}
