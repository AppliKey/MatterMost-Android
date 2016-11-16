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

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Emitter;
import rx.Observable;


public class MessagingSocket implements Socket {

    private static final String TAG = "MessagingSocket";

    private final BearerTokenFactory mBearerTokenFactory;
    private final Gson mGson;
    private final URI mURI;
    private WebSocket mWebSocket;
    private AtomicBoolean mIsDisconnectingFired = new AtomicBoolean(false);

    public MessagingSocket(BearerTokenFactory bearerTokenFactory, Gson gson, Uri endpoint) {
        mBearerTokenFactory = bearerTokenFactory;
        mGson = gson;
        mURI = URI.create(endpoint.toString());
    }

    @Override
    public Observable<WebSocketEvent> listen() {
        return Observable.fromEmitter(emitter -> {
            mIsDisconnectingFired.set(false);
            try {
                final WebSocketAdapter socketListener = new WebSocketAdapter() {

                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        emitter.onNext(mGson.fromJson(text, WebSocketEvent.class));
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                        Log.e(TAG, "WebSocket Error: ", cause);
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
                        if (mIsDisconnectingFired.get()) {
                            Log.d(TAG, "Socket disconnected!");
                            emitter.onCompleted();
                        } else {
                            Log.w(TAG, "Socket connection interrupted. Trying to reconnect...");
                            emitter.onError(new SocketException("Connection interrupted"));
                        }
                    }
                };
                mWebSocket = new WebSocketFactory()
                        .setConnectionTimeout(Constants.WEB_SOCKET_TIMEOUT)
                        .createSocket(mURI);
                mWebSocket.addListener(socketListener);
                mWebSocket.addHeader(Constants.AUTHORIZATION_HEADER, mBearerTokenFactory.getBearerTokenString());
                emitter.setCancellation(() -> mWebSocket.removeListener(socketListener));
                mWebSocket.connect();
                Log.d(TAG, "Socket connected!");
            } catch (IOException | WebSocketException e) {
                Log.e(TAG, "Socket error: ", e);
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    @Override
    public boolean isOpen() {
        return mWebSocket != null && mWebSocket.isOpen() && !mIsDisconnectingFired.get();
    }

    @Override
    public void close() {
        mIsDisconnectingFired.set(true);
        if (mWebSocket != null) {
            mWebSocket.sendClose();
            mWebSocket.disconnect();
        }
    }
}
