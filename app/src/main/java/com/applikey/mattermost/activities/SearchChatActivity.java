package com.applikey.mattermost.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.mvp.presenters.SearchChatPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Anatoliy Chub
 */

public class SearchChatActivity extends BaseMvpActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @InjectPresenter
    SearchChatPresenter mSearchChatPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_chat);

        App.getComponent().inject(this);
        ButterKnife.bind(this);

    }

}
