package com.applikey.mattermost.fragments;

import android.os.Bundle;

import com.arellomobile.mvp.MvpDelegate;

/**
 * This class is taken from Moxy samples, and extended from {@link BaseFragment}.
 */
public class BaseMvpFragment extends BaseFragment {

    private MvpDelegate<? extends BaseMvpFragment> mMvpDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMvpDelegate().onCreate();
    }

    @Override
    public void onStart() {
        super.onStart();

        getMvpDelegate().onAttach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getMvpDelegate().onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isRemoving() || getActivity().isFinishing()) {
            getMvpDelegate().onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getMvpDelegate().onSaveInstanceState(outState);
    }

    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }
}
