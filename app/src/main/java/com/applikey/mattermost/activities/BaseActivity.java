package com.applikey.mattermost.activities;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.injects.ApplicationComponent;
import com.applikey.mattermost.utils.kissUtils.utils.CommonUtil;
import com.applikey.mattermost.utils.kissUtils.utils.ToastUtil;
import com.applikey.mattermost.web.images.ImageLoader;
import com.tasomaniac.android.widget.DelayedProgressDialog;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.ActivityLifecycleProvider;
import com.trello.rxlifecycle.RxLifecycle;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class BaseActivity extends AppCompatActivity implements ActivityLifecycleProvider {


    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    @Inject
    EventBus mEventBus;


    @Inject
    ImageLoader mImageLoader;

    private DelayedProgressDialog mDialog;

    public void showToast(@NonNull String text) {
        ToastUtil.show(text);
    }

    public void showToast(@StringRes int text) {
        ToastUtil.show(text);
    }

    public ApplicationComponent getComponent() {
        return ((App) getApplication()).getComponent();
    }

    public void hideKeyboard() {
        CommonUtil.hideIME(this, getCurrentFocus());
    }

    @Override
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    public final <T> Observable.Transformer<T, T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilActivityEvent(lifecycleSubject, event);
    }

    @Override
    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxLifecycle.bindActivity(lifecycleSubject);
    }

    public void showLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            // do nothing
            return;
        }
        mDialog = DelayedProgressDialog.make(this, null, getString(R.string.please_wait));
        mDialog.setCancelable(false);
        mDialog.setMinShowTime(500);
        mDialog.show();
    }

    public void hideLoadingDialog() {
        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
                return;
            }
            mDialog = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        getComponent().inject(this);
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }
}
