package com.applikey.mattermost.models.user;

public interface Searchable<T> {

    boolean search(T searchFilter);
}
