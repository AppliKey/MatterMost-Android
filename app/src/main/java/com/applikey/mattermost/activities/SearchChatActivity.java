package com.applikey.mattermost.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.SearchChatAdapter;
import com.applikey.mattermost.events.SearchTabIndicatorRequested;
import com.applikey.mattermost.mvp.presenters.SearchChatPresenter;
import com.applikey.mattermost.mvp.views.SearchChatView;
import com.applikey.mattermost.views.SearchTabBehavior;
import com.applikey.mattermost.views.TabSelectedListener;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.devspark.robototextview.widget.RobotoEditText;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @author Anatoliy Chub
 */

public class SearchChatActivity extends BaseMvpActivity implements SearchChatView {

    private static final String TAG = SearchChatActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.et_search)
    RobotoEditText mEtSearch;
    @Bind(R.id.btn_clear_search)
    ImageButton mBtnClearSearch;

    @InjectPresenter
    SearchChatPresenter mSearchChatPresenter;

    private PagerAdapter mPagerAdapter;

    public static void startActivity(Context context) {
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

        mTabLayout.setupWithViewPager(mViewPager);
        final int tabCount = mTabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            final TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_chat_list);
                tab.setIcon(SearchTabBehavior.getItemBehavior(i).getIcon());

                final View customTab = tab.getCustomView();
                if (customTab != null) {
                    final View notificationIcon = customTab.findViewById(R.id.iv_notification_icon);
                    mTabIndicatorModel.register(SearchTabBehavior.values()[i + 1],
                            (ImageView) notificationIcon);
                }
            }
        }

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "finish: ");
            finish();
        });
        setTitle(null);

        final TabSelectedListener
                mOnTabSelectedListener = new SearchTabSelectedListener(mViewPager);
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
        mOnTabSelectedListener.onTabReselected(mTabLayout.getTabAt(0));
    }

    @Subscribe
    public void on(SearchTabIndicatorRequested event) {
        mTabIndicatorModel.handleEvent(event);
    }

    @OnTextChanged(R.id.et_search)
    public void onTextChanged(CharSequence text){
        Log.d(TAG, "onTextChanged: tab " + mTabLayout.getSelectedTabPosition());
        switch (mTabLayout.getSelectedTabPosition()){
            case 3 :
                mSearchChatPresenter.handleUserTextChanges(text.toString());
                break;
        }
    }

    @OnClick(R.id.btn_clear_search)
    public void onClearSearchClicked(){
        mEtSearch.setText(null);
    }

    private class SearchTabSelectedListener extends TabSelectedListener {


        protected SearchTabSelectedListener(ViewPager viewPager) {
            super(viewPager);
        }

        protected int getSelectedTabColor() {
            if (selectedTabColor == -1) {
                selectedTabColor = ContextCompat.getColor(SearchChatActivity.this,
                        R.color.tabSelected);
            }
            return selectedTabColor;
        }

        protected int getUnSelectedTabColor() {
            if (unSelectedTabColor == -1) {
                unSelectedTabColor = ContextCompat.getColor(SearchChatActivity.this,
                        R.color.tabUnSelected);
            }
            return unSelectedTabColor;
        }
    }

    private class TabIndicatorModel {

        private final Object mutex = new Object();

        private final Map<SearchTabBehavior, Boolean> mIndicatorVisibilities = new HashMap<>();
        private final Map<SearchTabBehavior, ImageView> mIndicators = new HashMap<>();

        void handleEvent(SearchTabIndicatorRequested event) {
            synchronized (mutex) {
                final SearchTabBehavior tab = event.getBehavior();
                Log.d("cc", "handleEvent: " + event.getBehavior());
                mIndicatorVisibilities.put(tab, event.isVisible());
                updateVisibility(tab, event.isVisible());
            }
        }

        void register(SearchTabBehavior tab, ImageView indicator) {
            synchronized (mutex) {
                mIndicators.put(tab, indicator);
                final boolean visible = mIndicatorVisibilities.containsKey(tab)
                        ? mIndicatorVisibilities.get(tab) : false;
                updateVisibility(tab, visible);
            }
        }

        private void updateVisibility(SearchTabBehavior tab, boolean isVisible) {
            if (mIndicators.containsKey(tab)) {
                mIndicators.get(tab).setVisibility(isVisible ? VISIBLE : GONE);
            }
        }
    }

    private final TabIndicatorModel
            mTabIndicatorModel = new TabIndicatorModel();

}
