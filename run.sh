#!/bin/bash

# Run from a command line like so:
# ./run.sh ETH-USD

echo "Running coinbase pro reader for instrument: " $1
export CLASSPATH=.:./lib/*:./out/production/CoinbaseProReader
echo $CLASSPATH

java com.alexz.coinbase.Application $1

