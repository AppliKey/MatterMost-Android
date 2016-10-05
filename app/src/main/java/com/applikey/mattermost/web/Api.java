package com.applikey.mattermost.web;

import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;

import java.util.List;
import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface Api {

    @POST("/api/v3/users/login")
    Observable<Response<AuthenticationResponse>> authorize(
            @Body AuthenticationRequest authenticationRequest);

    @GET("/api/v3/teams/all")
    Observable<Map<String, Team>> listTeams();

    @GET("/api/v3/users/me")
    Observable<Response> getMe();

    @GET("/api/v3/users/initial_load")
    Observable<Response> getInitialLoad();

    @GET("/api/v3/users/direct_profiles")
    Observable<Map<String, User>> getDirectProfiles();

    @GET("/api/v3/users/profiles/{teamId}")
    Observable<Map<String, User>> getTeamProfiles(@Path("teamId") String teamId);

    @POST("/api/v3/users/status")
    Observable<Map<String, String>> getUserStatusesCompatible(@Body String[] userIds);

    @GET("/api/v3/users/status")
    Observable<Map<String, String>> getUserStatuses();

    @POST("/api/v3/users/send_password_reset")
    @FormUrlEncoded
    Observable<Response> sendPasswordReset(@Field("email") String email);

    // Lists all joined channels and private groups, aswell as their metadata as "Memberships"
    @GET("/api/v3/teams/{teamId}/channels/")
    Observable<ChannelResponse> listChannels(@Path("teamId") String teamId);
}
