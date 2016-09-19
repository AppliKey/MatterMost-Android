package com.applikey.mattermost.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.TeamListAdapter;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.mvp.presenters.ChooseTeamPresenter;
import com.applikey.mattermost.mvp.views.ChooseTeamView;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseTeamActivity extends BaseMvpActivity implements ChooseTeamView {

    @Bind(R.id.rv_teams)
    RecyclerView rvTeams;

    @InjectPresenter
    ChooseTeamPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        ButterKnife.bind(this);

        mPresenter.getInitialData();
    }

    @Override
    public void displayTeams(List<Team> teams) {
        final TeamListAdapter adapter = new TeamListAdapter(teams);
        adapter.setItemClickAdapter(mTeamClickListener);

        rvTeams.setLayoutManager(new LinearLayoutManager(this));
        rvTeams.setAdapter(adapter);
    }

    @Override
    public void onTeamChosen() {
        final Intent intent = new Intent(this, ChatListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenter.unSubscribe();
    }

    @OnClick(R.id.back)
    public void onBack() {
        finish();
    }

    private final TeamListAdapter.TeamClickListener mTeamClickListener = team -> {
        mPresenter.chooseTeam(team);
    };
}
