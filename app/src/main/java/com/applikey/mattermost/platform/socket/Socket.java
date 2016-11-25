package com.applikey.mattermost.platform.socket;


import com.applikey.mattermost.models.socket.WebSocketEvent;

import rx.Observable;

public interface Socket {

    Observable<WebSocketEvent> listen();

    boolean isOpen();

}
