package com.applikey.mattermost.platform.socket;

import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import rx.Emitter;
import rx.Observable;

public class OkHttpMessagingSocket implements Socket {

    private final OkHttpClient mOkHttpClient;
    private final Gson mGson;
    private final String mUrl;

    private WebSocket mWebSocket;

    public OkHttpMessagingSocket(final OkHttpClient okHttpClient, final Gson gson, final String endpoint) {
        mOkHttpClient = okHttpClient;
        mGson = gson;
        mUrl = endpoint;
    }

    @Override
    public Observable<WebSocketEvent> listen() {
        return Observable.fromEmitter(emitter -> {

            final WebSocketListener webSocketListener = new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    emitter.onNext(mGson.fromJson(text, WebSocketEvent.class));
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                    emitter.onError(throwable);
                }
            };

            final Request request = new Request.Builder().url(mUrl).build();
            mWebSocket = mOkHttpClient.newWebSocket(request, webSocketListener);

            emitter.setCancellation(() -> mWebSocket.close(0, ""));
        }, Emitter.BackpressureMode.BUFFER);
    }

    @Override
    public boolean isOpen() {
        return mWebSocket != null;
    }
}
