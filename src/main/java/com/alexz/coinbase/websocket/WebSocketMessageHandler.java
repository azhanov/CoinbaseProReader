package com.alexz.coinbase.websocket;

import com.alexz.coinbase.PayloadHelper;
import com.alexz.coinbase.UIHelper;
import com.alexz.coinbase.model.OrderBook;
import com.alexz.coinbase.model.OrderBookUpdate;

import java.util.List;

public class WebSocketMessageHandler implements MessageHandler {

    OrderBook orderBook;

    public WebSocketMessageHandler() {
        orderBook = null;
    }

    public void handleMessage(String message) {
        String responseType = PayloadHelper.getResponseType(message);
        if (PayloadHelper.SNAPSHOT.equals(responseType)) {
            orderBook = PayloadHelper.buildOrderBookFromSnapshot(message);
            List<List<Float>> orderBookNLevels = orderBook.buildOrderBookNLevels();
            UIHelper.display(orderBookNLevels.get(0), orderBookNLevels.get(1));
        } else if (PayloadHelper.L2_UPDATE.equals(responseType)) {
            OrderBookUpdate orderBookUpdate = PayloadHelper.buildOrderBookUpdateFromL2Update(message);
            if (orderBook != null) {
                orderBook.updateOrderBook(orderBookUpdate);
                List<List<Float>> orderBookNLevels = orderBook.buildOrderBookNLevels();
                UIHelper.display(orderBookNLevels.get(0), orderBookNLevels.get(1));
            }
        }
    }
}
