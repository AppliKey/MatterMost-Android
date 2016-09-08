package com.applikey.mattermost.web;

import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.models.team.Team;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface Api {

    @POST("/api/v3/users/login")
    Observable<Response<AuthenticationResponse>> authorize(
            @Body AuthenticationRequest authenticationRequest);

    @GET("/api/v3/teams/all")
    Observable<Map<String, Team>> listTeams();
}
