package com.applikey.mattermost.web;

import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.models.groups.ChannelResponse;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.utils.PrimitiveConverterFactory;

import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Path;
import rx.Observable;

public class ApiDelegate implements Api {

    private final ServerUrlFactory urlFactory;
    private final OkHttpClient okHttpClient;

    private String serverUrl;
    private Api realApi;

    private final Object mutex = new Object();

    public ApiDelegate(OkHttpClient okHttpClient, ServerUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
        this.okHttpClient = okHttpClient;
    }

    private Api getRealApi() {
        final String currentServerUrl = urlFactory.getServerUrl();
        if (!isSameServerRequested(currentServerUrl)) {
            synchronized (mutex) {
                if (!isSameServerRequested(currentServerUrl)) {
                    serverUrl = currentServerUrl;
                    final Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(serverUrl)
                            .client(okHttpClient)
                            .addConverterFactory(PrimitiveConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                    //noinspection UnnecessaryLocalVariable
                    realApi = retrofit.create(Api.class);
                }
            }
        }
        return realApi;
    }

    private boolean isSameServerRequested(String requestedServer) {
        return serverUrl != null && serverUrl.equals(requestedServer);
    }

    @Override
    public Observable<Response<AuthenticationResponse>> authorize(
            @Body AuthenticationRequest authenticationRequest) {
        return getRealApi().authorize(authenticationRequest);
    }

    @Override
    public Observable<Map<String, Team>> listTeams() {
        return getRealApi().listTeams();
    }

    @Override
    public Observable<Response> getMe() {
        return getRealApi().getMe();
    }

    @Override
    public Observable<Response> getInitialLoad() {
        return getRealApi().getInitialLoad();
    }

    @Override
    public Observable<Response> getDirectProfiles() {
        return getRealApi().getDirectProfiles();
    }

    @Override
    public Observable<Response> getTeamProfiles(@Path("teamId") String teamId) {
        return getRealApi().getTeamProfiles(teamId);
    }

    @Override
    public Observable<Response> getUserStatuses() {
        return getRealApi().getUserStatuses();
    }

    @Override
    public Observable<Response> sendPasswordReset(@Field("email") String email) {
        return getRealApi().sendPasswordReset(email);
    }

    @Override
    public Observable<ChannelResponse> listChannels(@Path("teamId") String teamId) {
        return getRealApi().listChannels(teamId);
    }
}
