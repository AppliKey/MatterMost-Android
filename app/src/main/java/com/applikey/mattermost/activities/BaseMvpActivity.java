package com.applikey.mattermost.activities;

import android.os.Bundle;

import com.arellomobile.mvp.MvpDelegate;

/**
 * This class is taken from Moxy samples, and extended from {@link BaseActivity}.
 */
public abstract class BaseMvpActivity extends BaseActivity {

    private MvpDelegate<? extends BaseMvpActivity> mMvpDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getMvpDelegate().onDetach();

        if (isFinishing()) {
            getMvpDelegate().onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getMvpDelegate().onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMvpDelegate().onAttach();
    }

    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }
}
