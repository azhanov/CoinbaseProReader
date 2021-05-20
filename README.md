**Hello and welcome to CoinbaseProReader.**

This Java module demonstrates how to:
- connect to CoinbasePro Websocket API.
- subscribe to exchange notifications.
- read, parse and display an order book of 10 levels.

---

The order book of 10 levels processing:

Vocabulary:
> *bid price* - the highest price a buyer is willing to pay, normally shown in green  
> *ask price* - the minimum the seller is willing to accept, normally shown in red  
> *order book* - electronic list of buy and sell orders for a specific security or financial instrument organized by price level. "The Order Book displays a real-time list of outstanding orders for a specific asset within the exchange. These orders represent how much interest there is from buyers and sellers in the form of Asks and Bids."  
> *10 levels* - top 10 highest bids and lowest 10 asks

CoinbaseProReader starts with loading a snapshot (current state of affairs)
 for given instrument. That gives as a complete picture at the moment of 
issuing the call. This is followed by update messages that are emitted by the server every
time a match happens as per API docs. The general logic behind order book 
of 10 levels is:
- start with parsing out 10 highest bids and 10 lowest asks and update the UI.
- upon receiving an update and given it's type ("buy" or "sell") check to see if 
the corresponding order book should be updated with the new price and update the screen.

