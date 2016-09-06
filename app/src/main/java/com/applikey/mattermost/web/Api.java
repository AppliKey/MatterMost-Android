package com.applikey.mattermost.web;

import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface Api {

    @POST("/api/v3/users/login")
    Observable<AuthenticationResponse> authorize(@Body AuthenticationRequest authenticationRequest);
}
