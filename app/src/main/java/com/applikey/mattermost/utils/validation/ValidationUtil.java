package com.applikey.mattermost.utils.validation;

import android.text.TextUtils;
import android.util.Patterns;

import rx.Single;

public final class ValidationUtil {

    private ValidationUtil() {

    }

    public static Single<Boolean> validateEmail(String email) {
        return Single.just(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static Single<Boolean> validateUsername(String username) {
        return Single.just(!TextUtils.isEmpty(username));
    }
}
