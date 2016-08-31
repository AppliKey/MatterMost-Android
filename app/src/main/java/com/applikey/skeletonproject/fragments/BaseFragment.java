package com.applikey.skeletonproject.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import com.applikey.skeletonproject.App;
import com.applikey.skeletonproject.activities.BaseActivity;
import com.applikey.skeletonproject.injects.ApplicationComponent;
import com.applikey.skeletonproject.utils.kissUtils.utils.ToastUtil;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public abstract class BaseFragment extends RxFragment {


    @Inject
    EventBus mEventBus;


    private ProgressDialog mProgressDialog;

    public void showToast(@NonNull String text) {
        ToastUtil.show(text);
    }

    public void showToast(@StringRes int text) {
        ToastUtil.show(text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public ApplicationComponent getComponent() {
        return ((App) getActivity().getApplication()).getComponent();
    }

    @Nullable
    public BaseActivity getBaseActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        }
        return null;
    }

    protected void showLoadingDialog() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            baseActivity.showLoadingDialog();
        }
    }

    protected void hideLoadingDialog() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            baseActivity.hideLoadingDialog();
        }
    }

}
