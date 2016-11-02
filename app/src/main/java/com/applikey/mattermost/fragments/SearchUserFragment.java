package com.applikey.mattermost.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.BaseActivity;
import com.applikey.mattermost.activities.ChatActivity;
import com.applikey.mattermost.adapters.UserAdapter;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.mvp.presenters.SearchUserPresenter;
import com.applikey.mattermost.mvp.views.SearchUserView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Anatoliy Chub
 */

public class SearchUserFragment extends BaseMvpFragment implements SearchUserView,
        UserAdapter.ClickListener {

    private static final String TAG = SearchUserFragment.class.getSimpleName();

    @InjectPresenter
    SearchUserPresenter mPresenter;

    @Bind(R.id.recycle_view)
    RecyclerView mRecycleView;

    private UserAdapter mUserAdapter;

    public static SearchUserFragment newInstance() {
        return new SearchUserFragment();
    }

    public SearchUserFragment() {
        App.getComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_chat, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getPresenter().getData("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClicked(User user) {
        getPresenter().handleUserClick(user);
    }

    private void initView() {
        mUserAdapter = new UserAdapter(mImageLoader);
        mUserAdapter.setOnClickListener(this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mUserAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPresenter().unSubscribe();
    }

    @Override
    public void displayData(List<User> users) {
        Log.d(TAG, "displayData: ");
        mUserAdapter.setDataSet(users);
    }

    protected SearchUserPresenter getPresenter() {
        if (mPresenter == null) {
            throw new RuntimeException("Presenter is null");
        }
        return mPresenter;
    }

    public void startChatActivity(Channel channel) {
        getActivity().startActivity(ChatActivity.getIntent(getContext(), channel));
    }

    public void showLoading(boolean show) {
        if (show) {
            ((BaseActivity) getActivity()).showLoadingDialog();
        } else {
            ((BaseActivity) getActivity()).hideLoadingDialog();
        }
    }

    @Override
    public void clearData(){
        mUserAdapter.clear();
    }

}
