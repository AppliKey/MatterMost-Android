package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.rx.RetryWithDelay;
import com.applikey.mattermost.web.Api;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @Inject
    Prefs mPrefs;
    @Inject
    Api mApi;
    @Bind(R.id.image)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getComponent().inject(this);
        ButterKnife.bind(this);

        // TODO delete examples
        /** Examples here */
        loadImageExample();
        persistTestDataToPrefs();
        loadExampleWebReques();
    }

    private void loadExampleWebReques() {
        mApi.fetchExampleRequest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(5, 300))
                .subscribe(exampleReposResponse -> {
                    Log.d(TAG, "call: received response");
                }, throwable -> {
                    Log.e(TAG, "call: ", throwable);
                });
    }

    private void loadImageExample() {
        mImageLoader.displayImage("https://avatars0.githubusercontent.com/u/5869863?v=3&s=460", image);
    }

    private void persistTestDataToPrefs() {
        mPrefs.setCurrentUserId("test_user_id");
    }
}
