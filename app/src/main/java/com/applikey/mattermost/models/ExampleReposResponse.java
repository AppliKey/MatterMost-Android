
package com.applikey.mattermost.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class ExampleReposResponse {


    // TODO: 26.03.16 example model, should be deleted


    @JsonField(name = "id")
    public long id;
    @JsonField(name = "name")
    public String name;
    @JsonField(name = "full_name")
    
    public String fullName;
    @JsonField(name = "owner")
    
    public Owner owner;
    @JsonField(name = "private")
    
    public boolean _private;
    @JsonField(name = "html_url")
    
    public String htmlUrl;
    @JsonField(name = "description")
    
    public String description;
    @JsonField(name = "fork")
    
    public boolean fork;
    @JsonField(name = "url")
    
    public String url;
    @JsonField(name = "forks_url")
    
    public String forksUrl;
    @JsonField(name = "keys_url")
    
    public String keysUrl;
    @JsonField(name = "collaborators_url")
    
    public String collaboratorsUrl;
    @JsonField(name = "teams_url")
    
    public String teamsUrl;
    @JsonField(name = "hooks_url")
    
    public String hooksUrl;
    @JsonField(name = "issue_events_url")
    
    public String issueEventsUrl;
    @JsonField(name = "events_url")
    
    public String eventsUrl;
    @JsonField(name = "assignees_url")
    
    public String assigneesUrl;
    @JsonField(name = "branches_url")
    
    public String branchesUrl;
    @JsonField(name = "tags_url")
    
    public String tagsUrl;
    @JsonField(name = "blobs_url")
    
    public String blobsUrl;
    @JsonField(name = "git_tags_url")
    
    public String gitTagsUrl;
    @JsonField(name = "git_refs_url")
    
    public String gitRefsUrl;
    @JsonField(name = "trees_url")
    
    public String treesUrl;
    @JsonField(name = "statuses_url")
    
    public String statusesUrl;
    @JsonField(name = "languages_url")
    
    public String languagesUrl;
    @JsonField(name = "stargazers_url")
    
    public String stargazersUrl;
    @JsonField(name = "contributors_url")
    
    public String contributorsUrl;
    @JsonField(name = "subscribers_url")
    
    public String subscribersUrl;
    @JsonField(name = "subscription_url")
    
    public String subscriptionUrl;
    @JsonField(name = "commits_url")
    
    public String commitsUrl;
    @JsonField(name = "git_commits_url")
    
    public String gitCommitsUrl;
    @JsonField(name = "comments_url")
    
    public String commentsUrl;
    @JsonField(name = "issue_comment_url")
    
    public String issueCommentUrl;
    @JsonField(name = "contents_url")
    
    public String contentsUrl;
    @JsonField(name = "compare_url")
    
    public String compareUrl;
    @JsonField(name = "merges_url")
    
    public String mergesUrl;
    @JsonField(name = "archive_url")
    
    public String archiveUrl;
    @JsonField(name = "downloads_url")
    
    public String downloadsUrl;
    @JsonField(name = "issues_url")
    
    public String issuesUrl;
    @JsonField(name = "pulls_url")
    
    public String pullsUrl;
    @JsonField(name = "milestones_url")
    
    public String milestonesUrl;
    @JsonField(name = "notifications_url")
    
    public String notificationsUrl;
    @JsonField(name = "labels_url")
    
    public String labelsUrl;
    @JsonField(name = "releases_url")
    
    public String releasesUrl;
    @JsonField(name = "deployments_url")
    
    public String deploymentsUrl;
    @JsonField(name = "created_at")
    
    public String createdAt;
    @JsonField(name = "updated_at")
    
    public String updatedAt;
    @JsonField(name = "pushed_at")
    
    public String pushedAt;
    @JsonField(name = "git_url")
    
    public String gitUrl;
    @JsonField(name = "ssh_url")
    
    public String sshUrl;
    @JsonField(name = "clone_url")
    
    public String cloneUrl;
    @JsonField(name = "svn_url")
    
    public String svnUrl;
    @JsonField(name = "homepage")
    
    public Object homepage;
    @JsonField(name = "size")
    
    public long size;
    @JsonField(name = "stargazers_count")
    
    public long stargazersCount;
    @JsonField(name = "watchers_count")
    
    public long watchersCount;
    @JsonField(name = "language")
    
    public String language;
    @JsonField(name = "has_issues")
    
    public boolean hasIssues;
    @JsonField(name = "has_downloads")
    
    public boolean hasDownloads;
    @JsonField(name = "has_wiki")
    
    public boolean hasWiki;
    @JsonField(name = "has_pages")
    
    public boolean hasPages;
    @JsonField(name = "forks_count")
    
    public long forksCount;
    @JsonField(name = "mirror_url")
    
    public Object mirrorUrl;
    @JsonField(name = "open_issues_count")
    
    public long openIssuesCount;
    @JsonField(name = "forks")
    
    public long forks;
    @JsonField(name = "open_issues")
    
    public long openIssues;
    @JsonField(name = "watchers")
    
    public long watchers;
    @JsonField(name = "default_branch")
    
    public String defaultBranch;
    @JsonField(name = "permissions")
    
    public Permissions permissions;

}
