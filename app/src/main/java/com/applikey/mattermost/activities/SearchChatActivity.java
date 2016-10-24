package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.SearchChatAdapter;
import com.applikey.mattermost.mvp.presenters.SearchChatPresenter;
import com.applikey.mattermost.mvp.views.SearchChatView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Anatoliy Chub
 */

public class SearchChatActivity extends BaseMvpActivity implements SearchChatView{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabs)
    TabLayout mTabs;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @InjectPresenter
    SearchChatPresenter mSearchChatPresenter;

    private PagerAdapter mPagerAdapter;

    public static void startActivity(Context context){
        context.startActivity(new Intent(context, SearchChatActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_chat);

        App.getComponent().inject(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mPagerAdapter = new SearchChatAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }

}
