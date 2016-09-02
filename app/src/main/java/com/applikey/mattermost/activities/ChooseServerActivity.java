package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseServerActivity extends BaseActivity {

    @Bind(R.id.et_server) EditText etServerUrl;
    @Bind(R.id.b_proceed) Button bProceed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_server);

        getComponent().inject(this);
        ButterKnife.bind(this);
    }

}
