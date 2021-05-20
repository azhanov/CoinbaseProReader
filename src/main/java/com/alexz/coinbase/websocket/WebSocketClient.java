package com.alexz.coinbase.websocket;

import com.alexz.coinbase.PayloadHelper;

import java.net.URI;

/**
 * WebSocketClient class orchestrates communication with the server, delegating work to helper classes.
 */
public class WebSocketClient {

    public static final String COINBASE_WS_URI = "wss://ws-feed.pro.coinbase.com/";

    WebSocketClientEndpoint clientEndPoint;

    public void execute(String instrument) {
        try {
            // Establish WebSocket connection
            clientEndPoint = new WebSocketClientEndpoint(new URI(WebSocketClient.COINBASE_WS_URI));
            // Create and inject messages handler
            WebSocketMessageHandler handler = new WebSocketMessageHandler();
            clientEndPoint.addMessageHandler(handler);
            // Create a subscription message
            String subscribeMessage = PayloadHelper.createL2SubscribeMessage(instrument);
            // Send message subscription and thus start listening on responses
            clientEndPoint.sendMessage(subscribeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        clientEndPoint.close();
    }
}

