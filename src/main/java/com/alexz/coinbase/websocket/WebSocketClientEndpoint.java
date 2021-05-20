package com.alexz.coinbase.websocket;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.util.logging.Logger;


@ClientEndpoint
public class WebSocketClientEndpoint {

    private final static Logger LOG = Logger.getLogger(WebSocketClientEndpoint.class.getName());

    Session userSession;
    private MessageHandler messageHandler;

    public WebSocketClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            userSession = container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (userSession != null) {
            try {
                System.out.println("Closing WebSocket session");
                userSession.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        // Callback to be called only once upon a new connection
        System.out.println("opening WebSocket");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("onClose: " + reason.toString());
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        //System.out.println("onMessage");
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable ex) {
        System.err.printf("WebSocket error => '%s' => '%s'", session, ex.getMessage());
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }
}