package com.applikey.mattermost.injects;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Scope
@Retention(SOURCE)
public @interface PerUser {

}
