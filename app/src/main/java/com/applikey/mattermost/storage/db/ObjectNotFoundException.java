package com.applikey.mattermost.storage.db;

import com.applikey.mattermost.Constants;

/**
 * @author Anatoliy Chub
 */

public class ObjectNotFoundException extends RuntimeException {

    public static final String DEFAULT_MESSAGE = "Object not found";
    public ObjectNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ObjectNotFoundException(String message) {
        super(DEFAULT_MESSAGE + Constants.SPACE + message);
    }
}
