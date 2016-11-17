package com.applikey.mattermost.storage.db;

import com.annimon.stream.Stream;
import com.applikey.mattermost.models.prefs.Preference;
import com.applikey.mattermost.storage.preferences.Prefs;

import java.util.List;
import java.util.Locale;

public final class PreferenceStorage {

    private static final String TAG = "PreferenceStorage";
    private static final String FORMAT_REMOTE_PREF_ID = "%1$s_%2$s_%3$s";

    private final Db mDb;
    private final Prefs mPrefs;

    public PreferenceStorage(final Db db, final Prefs prefs) {
        mDb = db;
        mPrefs = prefs;
    }

    public void save(List<Preference> preferences) {
        Stream.of(preferences)
                .forEach(it -> it.setId(String.format(Locale.ROOT, FORMAT_REMOTE_PREF_ID,
                                                      it.getUserId(), it.getName(), it.getCategory())));
        mDb.saveTransactional(preferences);
    }
}
