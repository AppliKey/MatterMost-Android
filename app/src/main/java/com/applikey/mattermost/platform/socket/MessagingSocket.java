package com.applikey.mattermost.platform.socket;

import android.net.Uri;
import android.util.Log;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.URI;

import rx.Emitter;
import rx.Observable;

public class MessagingSocket implements Socket {

    private static final String TAG = "MessagingSocket";

    private final BearerTokenFactory mBearerTokenFactory;
    private final Gson mGson;
    private final URI mURI;
    private WebSocket mWebSocket;

    public MessagingSocket(BearerTokenFactory bearerTokenFactory, Gson gson, Uri endpoint) {
        mBearerTokenFactory = bearerTokenFactory;
        mGson = gson;
        mURI = URI.create(endpoint.toString());
    }

    @Override
    public Observable<WebSocketEvent> listen() {
        return Observable.fromEmitter(emitter -> {
            try {
                final WebSocketAdapter socketListener = new WebSocketAdapter() {

                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        emitter.onNext(mGson.fromJson(text, WebSocketEvent.class));
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                        Log.e(TAG, "WebSocket Error: " + cause.getMessage());
                        switch (cause.getError()) {
                            case SOCKET_CONNECT_ERROR:
                                emitter.onError(new ConnectException("Unable to establish connection"));
                                break;
                            default:
                                emitter.onError(new SocketException("Unexpected socket exception"));
                        }
                    }

                    @Override
                    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {
                        Log.e(TAG, "handleCallbackError: ", cause);
                        emitter.onError(cause);
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket,
                                               WebSocketFrame serverCloseFrame,
                                               WebSocketFrame clientCloseFrame,
                                               boolean closedByServer)
                            throws Exception {
                        Log.d(TAG, "Socket disconnected!");
                        emitter.onError(new SocketException("Connection interrupted"));
                    }
                };
                mWebSocket = new WebSocketFactory()
                        .createSocket(mURI, Constants.WEB_SOCKET_TIMEOUT);
                mWebSocket.addListener(socketListener);
                mWebSocket.addHeader(Constants.AUTHORIZATION_HEADER, mBearerTokenFactory.getBearerTokenString());
                emitter.setCancellation(() -> {
                    mWebSocket.removeListener(socketListener);
                    mWebSocket.sendClose();
                    mWebSocket.disconnect();
                });
                mWebSocket.connect();
                Log.d(TAG, "Socket connected!");
            } catch (Exception e) {
                Log.e(TAG, "Socket error: " + e.getMessage());
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    @Override
    public boolean isOpen() {
        return mWebSocket != null && mWebSocket.isOpen();
    }

}
