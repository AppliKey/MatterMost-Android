package com.applikey.mattermost.typeadapters;

import com.applikey.mattermost.models.RealmString;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.realm.RealmList;

public class RealmListStringTypeAdapter extends TypeAdapter<RealmList<RealmString>> {

    @Override
    public void write(JsonWriter out, RealmList<RealmString> value) throws IOException {
        out.beginArray();
        for (RealmString realmString : value) {
            out.value(realmString.getValue());
        }
        out.endArray();
    }

    @Override
    public RealmList<RealmString> read(JsonReader in) throws IOException {
        final RealmList<RealmString> realmStrings = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
            } else {
                final RealmString realmString = new RealmString(in.nextString());
                realmStrings.add(realmString);
            }
        }
        in.endArray();
        return realmStrings;
    }
}
