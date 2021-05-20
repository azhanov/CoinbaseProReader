package com.alexz.coinbase;

import com.alexz.coinbase.websocket.WebSocketClient;

import java.util.concurrent.CountDownLatch;

public class Application {

    public static String processInput(String[] args) {
        if (args.length == 0) {
            System.err.println("Please, provide an instrument name to query. Exiting...");
            System.exit(1);
        }
        else {
            System.out.printf("Streaming order book for instrument: [%s], press Ctrl-C to quit.%n", args[0]);
        }
        return args[0];
    }

    public static void main(String[] args) {

        // Create barrier and set countdown counter to 1
        CountDownLatch doneSignal = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * Callback for Control-c
             */
            @Override
            public void run() {
                // Unlock the latch with main thread awaiting for it, will continue and do the clean up
                doneSignal.countDown();
            }
        });

        /////////////////////////////////////////////////
        // Connect to WebSocket API and do the processing
        /////////////////////////////////////////////////
        String instrument = Application.processInput(args);
        WebSocketClient wsc = new WebSocketClient();
        wsc.execute(instrument);

        // Block in wait state till unlocked by pressing Control-c
        try {
            doneSignal.await();
            wsc.close();
        } catch (InterruptedException e) {
        }

        System.out.println("Exiting.");
    }
}
