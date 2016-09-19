package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.team.Team;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

// TODO RealmObjectStorage abstraction
// TODO Review API
public class TeamStorage {

    private static final String CHOSEN_TEAM_KEY = "chosen_team";

    @Inject
    Db mDb;

    public TeamStorage() {
        App.getComponent().inject(this);
    }

    public void saveTeams(Iterable<Team> teams) {
        mDb.saveTransactional(teams);
    }

    public Observable<List<Team>> listAll() {
        return mDb.listRealmObjects(Team.class);
    }

    public void setChosenTeam(Team team) {
        mDb.saveTransactional(new DictionaryEntry(CHOSEN_TEAM_KEY, team.getId()));
    }
}
