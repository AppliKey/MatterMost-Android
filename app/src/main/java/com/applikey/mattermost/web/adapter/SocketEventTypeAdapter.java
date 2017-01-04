package com.applikey.mattermost.web.adapter;

import com.applikey.mattermost.models.socket.Props;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;


public class SocketEventTypeAdapter extends BaseTypeAdapter<WebSocketEvent> {

    @Override
    public void write(JsonWriter out, WebSocketEvent value) throws IOException {
        throw new IllegalStateException("WebSocketEvent to JSON not implemented.");
    }

    @Override
    public WebSocketEvent read(JsonReader in) throws IOException {
        final WebSocketEvent event = new WebSocketEvent();
        final Gson gson = getGson();
        in.beginObject();

        while (in.hasNext()) {
            final String nextName = in.nextName();
            switch (nextName) {
                case "team_id":
                    event.setTeamId(gson.fromJson(in, String.class));
                    break;
                case "channel_id":
                    event.setChannelId(gson.fromJson(in, String.class));
                    break;
                case "user_id":
                    event.setUserId(gson.fromJson(in, String.class));
                    break;
                case "event":
                case "action":
                    event.setEvent(gson.fromJson(in, String.class));
                    break;
                case "data":
                case "props":
                    event.setProps(gson.fromJson(in, Props.class));
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();

        return event;
    }

}
