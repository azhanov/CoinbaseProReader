package com.alexz.coinbase.websocket;

/**
 * A contract to fulfill by a class that can process WebSocket messages.
 */
public interface MessageHandler {
    void handleMessage(String message);
}
