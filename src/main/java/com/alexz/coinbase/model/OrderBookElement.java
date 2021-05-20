package com.alexz.coinbase.model;

public class OrderBookElement {
    private final Float price;
    private final Float size;

    public OrderBookElement(Float pricePar, Float sizePar) {
        price = pricePar;
        size = sizePar;
    }

    public float getPrice() {
        return price;
    }

    public float getSize() {
        return size;
    }
}
