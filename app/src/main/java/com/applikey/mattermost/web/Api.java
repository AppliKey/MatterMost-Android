package com.applikey.mattermost.web;

import com.applikey.mattermost.models.auth.AttachDeviceRequest;
import com.applikey.mattermost.models.auth.AuthenticationRequest;
import com.applikey.mattermost.models.auth.AuthenticationResponse;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelPurposeRequest;
import com.applikey.mattermost.models.channel.ChannelRequest;
import com.applikey.mattermost.models.channel.ChannelResponse;
import com.applikey.mattermost.models.channel.ChannelTitleRequest;
import com.applikey.mattermost.models.channel.DirectChannelRequest;
import com.applikey.mattermost.models.channel.ExtraInfo;
import com.applikey.mattermost.models.channel.Membership;
import com.applikey.mattermost.models.channel.RequestUserId;
import com.applikey.mattermost.models.commands.InviteNewMembersRequest;
import com.applikey.mattermost.models.init.InitLoadResponse;
import com.applikey.mattermost.models.post.PendingPost;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.models.post.PostSearchRequest;
import com.applikey.mattermost.models.team.Team;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.models.web.PingResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;
import rx.Single;

public interface Api {

    String MULTIPART_IMAGE_TAG = "image";

    @POST("/api/v3/users/login")
    Observable<Response<AuthenticationResponse>> authorize(
            @Body AuthenticationRequest authenticationRequest);

    @GET("/api/v3/teams/all")
    Observable<Map<String, Team>> listTeams();

    @GET("/api/v3/users/me")
    Observable<User> getMe();

    @GET("/api/v3/users/initial_load")
    Observable<InitLoadResponse> getInitialLoad();

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
    Observable<Response<Void>> sendPasswordReset(@Field("email") String email);

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

    @GET("/api/v3/teams/{teamId}/channels/{channelId}")
    Observable<Channel> getChannelById(@Path("teamId") String teamId,
                                       @Path("channelId") String channelId);

    //This url is not containing "/" symbol at the start
    //In this case it build full url in the next way :
    //"www.mattermost.com/v3/" + "applikeyteam" = "www.mattermost.com/v3/applikeyteam"
    //In another case, if we add "/" symbol at the start,
    //it works in the some another way:
    //"www.mattermost.com/v3/" + "applikeyteam" = "www.mattermost.com/applikeyteam"
    //(It cuts all subdirectories from base url)
    //It resolve validation problem(in the case if user input (<server_url> + <team>), instead
    // (<server_url>))
    @GET("api/v3/general/ping")
    Observable<PingResponse> ping();

    @GET("/api/v3/teams/{teamId}/channels/{channelId}/posts/page/{offset}/{limit}")
    Observable<PostResponse> getPostsPage(@Path("teamId") String teamId,
                                          @Path("channelId") String channelId,
                                          @Path("offset") int offset,
                                          @Path("limit") int limit);

    @GET("/api/v3/teams/{teamId}/channels/{channelId}/posts/page/0/1")
    Observable<PostResponse> getLastPost(@Path("teamId") String teamId,
                                         @Path("channelId") String channelId);

    @GET("/api/v3/teams/{teamId}/channels/{channelId}/extra_info")
    Observable<ExtraInfo> getChannelExtra(@Path("teamId") String teamId,
                                          @Path("channelId") String channelId);

    @POST("/api/v3/teams/{teamId}/channels/{channelId}/posts/create")
    Observable<Post> createPost(@Path("teamId") String teamId,
                                @Path("channelId") String channelId,
                                @Body PendingPost request);

    @POST("/api/v3/teams/{teamId}/channels/{channelId}/update_last_viewed_at")
    Observable<Response<String>> updateLastViewedAt(@Path("teamId") String teamId,
                                                    @Path("channelId") String channelId);

    @POST("/api/v3/teams/{team_id}/channels/create")
    Observable<Channel> createChannel(@Path("team_id") String teamId, @Body ChannelRequest request);

    @POST("/api/v3/teams/{team_id}/channels/{channel_id}/add")
    Observable<Membership> addUserToChannel(@Path("team_id") String teamId,
                                            @Path("channel_id") String channelId,
                                            @Body RequestUserId userId);

    @POST("/api/v3/users/attach_device")
    Observable<Response<AttachDeviceRequest>> attachDevice(@Body AttachDeviceRequest request);

    @POST("/api/v3/teams/{team_id}/channels/create_direct")
    Observable<Channel> createChannel(@Path("team_id") String teamId,
                                      @Body DirectChannelRequest request);

    @GET("/api/v3/teams/{team_id}/channels/more")
    Single<ChannelResponse> getChannelsUserHasNotJoined(@Path("team_id") String teamId);

    @POST("/api/v3/teams/{team_id}/invite_members")
    Observable<Void> inviteNewMember(@Path("team_id") String teamId, @Body InviteNewMembersRequest body);

    @POST("api/v3/teams/{teamId}/channels/update")
    Observable<Channel> updateChannelTitle(@Path("teamId") String teamId,
                                           @Body ChannelTitleRequest request);

    @POST("api/v3/teams/{teamId}/channels/update_purpose")
    Observable<Channel> updateChannelPurpose(@Path("teamId") String teamId,
                                             @Body ChannelPurposeRequest request);

    @POST("/api/v3/teams/{team_id}/posts/search")
    Observable<PostResponse> searchPosts(@Path("team_id") String teamId, @Body PostSearchRequest request);

    @Multipart
    @POST("/api/v3/users/newimage")
    Observable<Void> uploadImage(@Part MultipartBody.Part image);

    @POST("/api/v3/users/update")
    Observable<User> editUser(@Body User user);

    @POST("/api/v3/teams/{team_id}/channels/{channel_id}/join")
    Single<Channel> joinToChannel(@Path("team_id") String teamId, @Path("channel_id") String channelId);
}
