package com.applikey.mattermost.web;


import com.applikey.mattermost.models.ExampleReposResponse;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface Api {

    // TODO: 26.02.16 Add Retrofit api calls here
    @GET("orgs/square/repos")
    Observable<List<ExampleReposResponse>> fetchExampleRequest();

}
