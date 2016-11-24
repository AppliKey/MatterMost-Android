package com.applikey.mattermost.web.adapter;

import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.socket.Props;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;


public class PropsTypeAdapter extends BaseTypeAdapter<Props> {

    @Override
    public void write(JsonWriter out, Props value) throws IOException {
        throw new IllegalStateException("Props to JSON not implemented");
    }

    @Override
    public Props read(JsonReader in) throws IOException {
        final Props props = new Props();
        final Gson gson = getGson();
        in.beginObject();

        while (in.hasNext()) {
            final String nextName = in.nextName();
            switch (nextName) {
                case "channel_id":
                    props.setChannelId(gson.fromJson(in, String.class));
                    break;
                case "channel_display_name":
                    props.setChannelDisplayName(gson.fromJson(in, String.class));
                    break;
                case "channel_type":
                    props.setChannelType(gson.fromJson(in, String.class));
                    break;
                case "post":
                    String postString = gson.fromJson(in, String.class);
                    props.setPost(gson.fromJson(postString, Post.class));
                    break;
                case "sender_name":
                    props.setSenderName(gson.fromJson(in, String.class));
                    break;
                case "team_id":
                    props.setTeamId(gson.fromJson(in, String.class));
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();

        return props;
    }
}
