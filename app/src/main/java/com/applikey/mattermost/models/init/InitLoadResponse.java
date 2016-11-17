package com.applikey.mattermost.models.init;

import com.applikey.mattermost.models.prefs.Preference;

import java.util.List;

//TODO add needed parameters
public class InitLoadResponse {

    private List<Preference> preferences;

    public InitLoadResponse(List<Preference> preferences) {
        this.preferences = preferences;
    }

    public List<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }
}
