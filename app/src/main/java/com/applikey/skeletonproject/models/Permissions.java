package com.applikey.skeletonproject.models;

import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Permissions {


    // TODO: 26.03.16 example model, should be deleted

    public boolean admin;

    public boolean push;

    public boolean pull;

}
