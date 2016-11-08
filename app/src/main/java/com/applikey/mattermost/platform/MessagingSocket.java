package com.applikey.mattermost.platform;

import android.util.Log;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.models.socket.WebSocketEvent;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.GsonFactory;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketError;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;

import rx.Emitter;
import rx.Observable;


public class MessagingSocket implements Socket {

    private static final String TAG = "MessagingSocket";

    private final BearerTokenFactory mBearerTokenFactory;
    private final Gson mGson;
    private WebSocket mWebSocket;
    private boolean mIsDisconnectingFired;

    public MessagingSocket(BearerTokenFactory bearerTokenFactory) {
        mBearerTokenFactory = bearerTokenFactory;
        mGson = GsonFactory.INSTANCE.getGson();
    }

    @Override
    public Observable<WebSocketEvent> listen(String url) {
        return Observable.fromEmitter(emitter -> {
            mIsDisconnectingFired = false;
            try {
                final WebSocketAdapter socketListener = new WebSocketAdapter() {

                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        Log.d(TAG, "onTextMessage: ");
                        emitter.onNext(mGson.fromJson(text, WebSocketEvent.class));
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                        Log.e(TAG, "onError: ", cause);
                        emitter.onError(cause);
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket,
                            WebSocketFrame serverCloseFrame,
                            WebSocketFrame clientCloseFrame,
                            boolean closedByServer)
                            throws Exception {
                        Log.d(TAG, "onDisconnected: isForceDisconnect: " + mIsDisconnectingFired);
                        if (mIsDisconnectingFired) {
                            emitter.onCompleted();
                        } else {
                            emitter.onError(new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR));
                        }
                    }

                    @Override
                    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                        Log.e(TAG, "onConnectError: ", exception);
                        emitter.onError(exception);
                    }
                };
                mWebSocket = new WebSocketFactory()
                        .setConnectionTimeout(Constants.WEB_SOCKET_TIMEOUT)
                        .createSocket(url);
                mWebSocket.addHeader(Constants.AUTHORIZATION_HEADER, mBearerTokenFactory.getBearerTokenString());
                mWebSocket.addListener(socketListener);
                mWebSocket.connectAsynchronously();
                emitter.setCancellation(() -> mWebSocket.removeListener(socketListener));
            } catch (IOException e) {
                emitter.onError(e);
            }
        }, Emitter.BackpressureMode.BUFFER);
    }

    @Override
    public boolean isConnected() {
        return mWebSocket != null && mWebSocket.isOpen() && !mIsDisconnectingFired;
    }

    @Override
    public void disconnect() {
        mIsDisconnectingFired = true;
        mWebSocket.sendClose();
        mWebSocket.disconnect();
    }
}
