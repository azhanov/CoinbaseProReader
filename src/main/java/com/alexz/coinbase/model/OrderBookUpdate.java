package com.alexz.coinbase.model;

import java.util.List;

public class OrderBookUpdate {
    private final List<OrderBookElement> buyUpdates;
    private final List<OrderBookElement> sellUpdates;

    public OrderBookUpdate(List<OrderBookElement> buyUpdatesPar, List<OrderBookElement> sellUpdatesPar) {
        buyUpdates = buyUpdatesPar;
        sellUpdates = sellUpdatesPar;
    }

    public List<OrderBookElement> getBuyUpdates() {
        return buyUpdates;
    }

    public List<OrderBookElement> getSellUpdates() {
        return sellUpdates;
    }
}
