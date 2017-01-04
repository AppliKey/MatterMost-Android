package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.utils.kissUtils.utils.CommonUtil;
import com.applikey.mattermost.utils.kissUtils.utils.ToastUtil;
import com.applikey.mattermost.views.ProgressDialogCompat;
import com.applikey.mattermost.web.images.ImageLoader;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * This class is taken from Skeleton Project.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    @Inject
    protected EventBus mEventBus;

    @Inject
    protected ImageLoader mImageLoader;

    private ProgressDialogCompat mProgressDialog;

    public void showToast(@NonNull String text) {
        ToastUtil.show(text);
    }

    public void showToast(@StringRes int text) {
        ToastUtil.show(text);
    }

    public ApplicationComponent getComponent() {
        return App.getComponent();
    }

    public void hideKeyboard() {
        CommonUtil.hideIME(this, getCurrentFocus());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
    }

    public void showLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
            return;
        }

        mProgressDialog = new ProgressDialogCompat(this);
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTint(R.color.colorAccent);

        mProgressDialog.show();
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showProgress(boolean show) {
        if (show) {
            showLoadingDialog();
        } else {
            hideLoadingDialog();
        }
    }
}
