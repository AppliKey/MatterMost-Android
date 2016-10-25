package com.applikey.mattermost.injects;

import javax.inject.Scope;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Scope
@Retention(SOURCE)
public @interface PerUser {
}
