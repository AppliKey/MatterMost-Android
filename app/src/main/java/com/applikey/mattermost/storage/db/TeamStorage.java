package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

// TODO RealmObjectStorage abstraction
// TODO Review API
public class TeamStorage {

    private static final String CHOSEN_TEAM_KEY = "chosen_team";
    private static final String KEY_TEAM_ID = Constants.PACKAGE_NAME + ".TEAM_ID";
    private static final String TAG = TeamStorage.class.getSimpleName();

    private final Db mDb;
    private final Prefs mPrefs;

    @Inject
    public TeamStorage(Db db, Prefs prefs) {
        mDb = db;
        mPrefs = prefs;
    }

    public void saveTeamsWithRemoval(Iterable<Team> teams) {
        mDb.saveTransactionalWithRemoval(teams);
    }

    public Observable<List<Team>> listAll() {
        return mDb.listRealmObjects(Team.class);
    }

    public Observable<Team> getChosenTeam() {
        final Observable<DictionaryEntry> dictionaryEntry =
                mDb.getSingleDictionaryEntry(CHOSEN_TEAM_KEY);
        return dictionaryEntry.flatMap(v -> {
            final String teamId = v.getValue();
            return mDb.listSingeRealmObject(Team.class, "id", teamId).first();
        });
    }

    public Observable<String> getTeamId() {
        return mPrefs.getValue(KEY_TEAM_ID);
    }

    public void setChosenTeam(Team team) {
        mDb.saveTransactional(new DictionaryEntry(CHOSEN_TEAM_KEY, team.getId()));
    }
}
