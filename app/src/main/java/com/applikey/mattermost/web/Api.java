package com.applikey.mattermost.web;

import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.ExtraInfo;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.web.PingResponse;

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

    @POST("/api/v3/teams/{teamId}/channels/{channelId}/posts/{postId}/delete")
    Observable<Void> deletePost(@Path("teamId") String teamId,
                                @Path("channelId") String channelId,
                                @Path("postId") String postId);

    @POST("/api/v3/teams/{teamId}/channels/{channelId}/posts/update")
    Observable<Post> updatePost(@Path("teamId") String teamId,
                                @Path("channelId") String channelId,
                                @Body Post post);

    // Lists all joined channels and private groups, aswell as their metadata as "Memberships"
    @GET("/api/v3/teams/{teamId}/channels/")
    Observable<ChannelResponse> listChannels(@Path("teamId") String teamId);

    //This url is not containing "/" symbol at the start
    //In this case it build full url in the next way :
    //"www.mattermost.com/v3/" + "applikeyteam" = "www.mattermost.com/v3/applikeyteam"
    //In another case, if we add "/" symbol at the start,
    //it works in the some another way:
    //"www.mattermost.com/v3/" + "applikeyteam" = "www.mattermost.com/applikeyteam"
    //(It cuts all subdirectories from base url)
    //It resolve validation problem(in the case if user input (<server_url> + <team>), instead (<server_url>))
    @GET("api/v3/general/ping")
    Observable<PingResponse> ping();

    @GET("/api/v3/teams/{teamId}/channels/{channelId}/posts/page/{offset}/{limit}")
    Observable<PostResponse> getPostsPage(@Path("teamId") String teamId,
                                          @Path("channelId") String channelId,
                                          @Path("offset") int offset,
                                          @Path("limit") int limit);

    @GET("/api/v3/teams/{teamId}/channels/{channelId}/extra_info")
    Observable<ExtraInfo> getChannelExtra(@Path("teamId") String teamId,
                                          @Path("channelId") String channelId);
}
