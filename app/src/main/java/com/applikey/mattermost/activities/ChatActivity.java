package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends BaseMvpActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }
}
