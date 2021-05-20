package com.alexz.coinbase;

import java.util.List;

public class UIHelper {

    /**
     * Simple console printing method to display N number of pairs
     * @param asks a list of N ask prices
     * @param bids a list of N bid prices
     */
    public static void display(List<Float> asks, List<Float> bids) {
        System.out.println("- ask --- bid -");
        int depth = Math.max(asks.size(), bids.size());
        for (int i = 0; i < depth; i ++) {
            System.out.println("" + asks.get(i) + "\t" + bids.get(i) );
        }
    }
}
