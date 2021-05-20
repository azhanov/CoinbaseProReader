package com.alexz.coinbase;

import com.alexz.coinbase.model.OrderBook;
import com.alexz.coinbase.model.OrderBookUpdate;
import com.alexz.coinbase.model.OrderBookElement;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class to deal with JSON payload used in the CoinbaseProd WebSocket API requests and responses.
 * Parses json simple library objects into CoinbaseProReader POJOs.
 */
public class PayloadHelper {

    public static final String ASKS = "asks";
    public static final String BIDS = "bids";
    public static final String SNAPSHOT = "snapshot";
    public static final String L2_UPDATE = "l2update";
    public static final String CHANGES = "changes";
    public static final String BUY_UPDATE = "buy";
    public static final String SELL_UPDATE = "sell";
    public static final Integer NUMBER_OF_LEVELS = 10;
    public static final JSONParser parser = new JSONParser();

    /**
     * Helper method to get the response type.
     * @param response response string
     * @return String value for the response type
     */
    public static String getResponseType(String response) {
        String responseType = null;
        // Sanity check - fail-fast
        if (response == null) {
            return null;
        }
        try {
            JSONObject jsonObject = (JSONObject) PayloadHelper.parser.parse(response);
            responseType =  (String) jsonObject.get("type");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return responseType;
    }

    /**
     * Helper method to build a JSON object representing Coinbase Pro WebSocket simple API request.
     * @see  <a href="https://docs.pro.coinbase.com/#protocol-overview"/>
     * @param instrument instrument name, e.g. "ETH-USD"
     * @return String - JSON object's string representation
     *
     * <b>Example: </b> {"type": "subscribe", "channels": [{ "name": "ticker", "product_ids": ["BTC-EUR"] }] }
     */
    @SuppressWarnings("unchecked")
    public static String createSimpleSubscribeMessage(String instrument) {
        // Sanity check - fail-fast
        if (instrument == null || instrument.trim().length() == 0) {
            System.err.println("Instrument name is required.");
            System.exit(1);
        }

        JSONObject simpleSubscribeMessage = new JSONObject();
        simpleSubscribeMessage.put("type", "subscribe");
        JSONArray channels = new JSONArray();
        JSONObject channelValue = new JSONObject();
        channelValue.put("name", "ticker");
        JSONArray productIds = new JSONArray();
        productIds.add(instrument);
        channelValue.put("product_ids", productIds);
        channels.add(channelValue);
        simpleSubscribeMessage.put("channels", channels);

        return simpleSubscribeMessage.toJSONString();
    }

    /**
     * Helper method to build a JSON object representing Coinbase Pro WebSocket L2 API request.
     * @see  <a href="https://docs.pro.coinbase.com/#protocol-overview"/>
     * @param instrument instrument name, e.g. "ETH-USD"
     * @return String - JSON object's string representation
     *
     * <b>Example: </b> {"type": "subscribe", "product_ids": ["ETH-USD", "ETH-EUR"], "channel": ["level", "heartbeat", {"name": "ticker", "product_ids": ["ETH-BTC", "ETH-USD"]}]}
     */
    @SuppressWarnings("unchecked")
    public static String createL2SubscribeMessage(String instrument) {
        // Sanity check - fail-fast
        if (instrument == null || instrument.trim().length() == 0) {
            System.err.println("Instrument name is required.");
            System.exit(1);
        }

        JSONObject simpleSubscribeMessage = new JSONObject();
        simpleSubscribeMessage.put("type", "subscribe");

        JSONArray productIds = new JSONArray();
        productIds.add(instrument);
        simpleSubscribeMessage.put("product_ids", productIds);

        JSONArray channels = new JSONArray();
        channels.add("level2");
        channels.add("heartbeat");
        JSONObject channelValue = new JSONObject();
        channelValue.put("name", "ticker");
        channelValue.put("product_ids", productIds);
        channels.add(channelValue);
        simpleSubscribeMessage.put("channels", channels);

        return simpleSubscribeMessage.toJSONString();
    }

    /**
     * Helper method to parse out N asks and bids prices from a snapshot response.
     * @param snapshotResponse response message
     * @return orderBook, a container of two elements where the first one is asks, and second - bids
     */
    public static OrderBook buildOrderBookFromSnapshot(String snapshotResponse) {
        // Sanity check - fail-fast
        if (snapshotResponse == null || snapshotResponse.trim().isEmpty()) {
            return null;
        }

        Map<Float, Float> asks = null;
        Map<Float, Float> bids = null;
        try {
            Object obj = PayloadHelper.parser.parse(snapshotResponse);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray asksList = (JSONArray) jsonObject.get(PayloadHelper.ASKS);
            JSONArray bidsList = (JSONArray) jsonObject.get(PayloadHelper.BIDS);
            asks = PayloadHelper.getAllPrices(asksList);
            bids = PayloadHelper.getAllPrices(bidsList);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new OrderBook(asks, bids);
    }

    /**
     * Helper method to parse out the type and the price of an update response.
     * @param l2UpdateResponse response message
     * @return OrderBookUpdate bean
     */
    public static OrderBookUpdate buildOrderBookUpdateFromL2Update(String l2UpdateResponse) {
        // Sanity check - fail-fast
        if (l2UpdateResponse == null || l2UpdateResponse.trim().isEmpty()) {
            return null;
        }

        OrderBookUpdate buySellUpdates = null;
        try {
            Object obj = PayloadHelper.parser.parse(l2UpdateResponse);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray changesList = (JSONArray) jsonObject.get(PayloadHelper.CHANGES);
            buySellUpdates = PayloadHelper.buildOrderBookUpdateFromCollection(changesList);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return buySellUpdates;
    }

    /**
     * Helper method to extract all of price points, given an array of arrays (price, size).
     * @param jsonArray prices as JSONArray
     * @return List of N prices
     */
    private static Map<Float, Float> getAllPrices(JSONArray jsonArray) {
        // Sanity check - fail-fast
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }

        Map<Float, Float> prices = new HashMap<>();
        for (JSONArray priceAndSize : (Iterable<JSONArray>) jsonArray) {
            Float price = Float.parseFloat((String) (priceAndSize).get(0));
            Float size = Float.parseFloat((String) (priceAndSize).get(1));
            prices.put(price, size);
        }

        return prices;
    }

    /**
     * Given an array from L2 update payload response in the form of (side, price, size),
     * parse it into two collections of buy updates: (price, size) and sell updates (price, size),
     * while also converting string values to floats.
     * @param jsonArray a collection of (side, price, size) elements
     * @return List of two elements where the first one is a list of buy updates, and second - sell updates
     */
    private static OrderBookUpdate buildOrderBookUpdateFromCollection(JSONArray jsonArray) {
        // Sanity check - fail-fast
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }

        List<OrderBookElement> buyUpdates = new ArrayList<>();
        List<OrderBookElement> sellUpdates = new ArrayList<>();
        for (JSONArray sidePriceSize : (Iterable<JSONArray>) jsonArray) {
            String side = (String) sidePriceSize.get(0);
            float price = Float.parseFloat((String) sidePriceSize.get(1));
            float size = Float.parseFloat((String) sidePriceSize.get(2));
            OrderBookElement orderBookElement = new OrderBookElement(price, size);
            if (PayloadHelper.BUY_UPDATE.equals(side))
                buyUpdates.add(orderBookElement);
            else if (PayloadHelper.SELL_UPDATE.equals(side))
                sellUpdates.add(orderBookElement);
            else
                System.err.println("Unsupported side value: " + side);
        }

        return new OrderBookUpdate(buyUpdates, sellUpdates);
    }
}
