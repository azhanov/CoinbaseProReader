package com.alexz.coinbase.model;

import com.alexz.coinbase.PayloadHelper;

import java.util.*;

/**
 * Class to hold order book and perform relevant operations such as add/remove price points.
 */
public class OrderBook {

    private final SortedMap<Float, Float> asks;
    private final SortedMap<Float, Float> bids;

    public OrderBook(Map<Float, Float> asksPar, Map<Float, Float> bidsPar) {
        // Asks will be sorted in the ascending order, thus naturally allowing to select N lowest asks from the start
        asks = new TreeMap<>(asksPar);
        // Bids on the contrary sorted in the descending order, thus allowing to select N highest bids from the start
        bids = new TreeMap<>(Collections.reverseOrder());
        bids.putAll(bidsPar);
    }

    /**
     * Update order book given a list of (price, size) combos.
     * Note: As per CoinbasePro API - a size 0 means removal.
     */
    public void updateOrderBook(OrderBookUpdate orderBookUpdate) {
        // Are there any buy updates?
        List<OrderBookElement> buyUpdates = orderBookUpdate.getBuyUpdates();
        for (OrderBookElement orderBookElement : buyUpdates) {
            if (orderBookElement.getSize() == 0) {
                // remove a price point due to size being 0
                bids.remove(orderBookElement.getPrice());
            } else {
                // insert the new price point according to the natural order
                bids.put(orderBookElement.getPrice(), orderBookElement.getSize());
            }
        }
        // Are there any  sell updates?
        List<OrderBookElement> sellUpdates = orderBookUpdate.getSellUpdates();
        for (OrderBookElement orderBookElement : sellUpdates) {
            if (orderBookElement.getSize() == 0) {
                // remove a price point due to size being 0
                asks.remove(orderBookElement.getPrice());
            } else {
                // insert the new price point according to the natural order
                asks.put(orderBookElement.getPrice(), orderBookElement.getSize());
            }
        }
    }

    /**
     * Helper method to extract N levels of prices (asks/bids) from an OrderBook
     * @return a List of two elements, first is N asks, second is N bids
     */
    public List<List<Float>> buildOrderBookNLevels() {
        List<Float> nAsks = getNPrices(asks);
        List<Float> nBids = getNPrices(bids);
        return Arrays.asList(nAsks, nBids);
    }

    private List<Float> getNPrices(SortedMap<Float, Float> prices) {
        List<Float> result = new ArrayList<>();
        Iterator<Float> iterator = prices.keySet().iterator();
        int count = 0;
        while(iterator.hasNext()) {
            result.add(iterator.next());
            count ++;
            if (count >= PayloadHelper.NUMBER_OF_LEVELS)
                break;
        }
        return result;
    }
}
