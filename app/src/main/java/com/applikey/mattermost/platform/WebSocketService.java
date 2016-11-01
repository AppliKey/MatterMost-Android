package com.applikey.mattermost.platform;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.kissUtils.utils.UrlUtil;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import javax.inject.Inject;
import java.io.IOException;

public class WebSocketService extends IntentService {

    private static final String TAG = WebSocketService.class.getSimpleName();

    @Inject
    Prefs mPrefs;

    @Inject
    BearerTokenFactory mTokenFactory;

    private WebSocket mWebSocket;

    public WebSocketService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        App.getUserComponent().inject(this);

        try {
            openSocket();
        } catch (IOException | WebSocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mWebSocket.disconnect();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    private void openSocket() throws IOException, WebSocketException {
        String baseUrl = mPrefs.getCurrentServerUrl();
        baseUrl = UrlUtil.removeProtocol(baseUrl);
        baseUrl = UrlUtil.WEB_SERVICE_PROTOCOL_PREFIX + baseUrl;
        baseUrl = baseUrl + Constants.WEB_SOCKET_ENDPOINT;

        mWebSocket = new WebSocketFactory()
                .setConnectionTimeout(Constants.WEB_SOCKET_TIMEOUT)
                .createSocket(baseUrl);

        mWebSocket.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                super.onTextMessage(websocket, text);
                Log.d(TAG, text);
            }
        });

        mWebSocket.addHeader(Constants.AUTHORIZATION_HEADER, mTokenFactory.getBearerTokenString());

        mWebSocket.connectAsynchronously();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }
}
