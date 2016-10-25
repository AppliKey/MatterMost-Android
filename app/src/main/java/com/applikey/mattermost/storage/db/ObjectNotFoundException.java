package com.applikey.mattermost.storage.db;

/**
 * @author Anatoliy Chub
 */

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException() {
        super("Object Not Find");
    }

    public ObjectNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
