package com.applikey.mattermost.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Owner {


    // TODO: 26.03.16 example model, should be deleted

    @JsonField(name = "login")
    
    public String login;
    @JsonField(name = "id")
    
    public long id;
    @JsonField(name = "avatar_url")
    
    public String avatarUrl;
    @JsonField(name = "gravatar_id")
    
    public String gravatarId;
    @JsonField(name = "url")
    
    public String url;
    @JsonField(name = "html_url")
    
    public String htmlUrl;
    @JsonField(name = "followers_url")
    
    public String followersUrl;
    @JsonField(name = "following_url")
    
    public String followingUrl;
    @JsonField(name = "gists_url")
    
    public String gistsUrl;
    @JsonField(name = "starred_url")
    
    public String starredUrl;
    @JsonField(name = "subscriptions_url")
    
    public String subscriptionsUrl;
    @JsonField(name = "organizations_url")
    
    public String organizationsUrl;
    @JsonField(name = "repos_url")
    
    public String reposUrl;
    @JsonField(name = "events_url")
    
    public String eventsUrl;
    @JsonField(name = "received_events_url")
    
    public String receivedEventsUrl;
    @JsonField(name = "type")
    
    public String type;
    @JsonField(name = "site_admin")
    
    public boolean siteAdmin;


}
