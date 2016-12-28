package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.applikey.mattermost.App;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.utils.kissUtils.utils.ToastUtil;
import com.applikey.mattermost.web.images.ImageLoader;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends RxFragment {

    @Inject
    EventBus mEventBus;

    @Inject ImageLoader mImageLoader;

    private Unbinder mUnbinder;

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

    protected void bindViews(View view) {
        mUnbinder = ButterKnife.bind(this, view);
    }
    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
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
