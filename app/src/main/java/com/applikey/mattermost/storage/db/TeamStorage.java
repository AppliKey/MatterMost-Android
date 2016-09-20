package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.team.Team;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

// TODO RealmObjectStorage abstraction
// TODO Review API
public class TeamStorage {

    private static final String CHOSEN_TEAM_KEY = "chosen_team";

    @Inject
    Db mDb;

    public TeamStorage() {
        App.getComponent().inject(this);
    }

    public void saveTeamsWithRemoval(Iterable<Team> teams) {
        mDb.saveTransactionalWithRemoval(teams);
    }

    public Observable<List<Team>> listAll() {
        return mDb.listRealmObjects(Team.class);
    }

    public void setChosenTeam(Team team) {
        mDb.saveTransactional(new DictionaryEntry(CHOSEN_TEAM_KEY, team.getId()));
    }

    public Observable<Team> getChosenTeam() {
        final Observable<DictionaryEntry> dictionaryEntry =
                mDb.getSingleDictionaryEntry(CHOSEN_TEAM_KEY);
        return dictionaryEntry.flatMap(v -> {
            final String teamId = v.getValue();
            return mDb.listSingeRealmObject(Team.class, "id", teamId);
        });
    }
}
