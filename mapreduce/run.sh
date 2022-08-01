#!/usr/bin/env bash

# stop when a error occurs
set -e

# to find com.opencsv
export HADOOP_CLASSPATH="$(pwd)/lib/*:$HADOOP_CLASSPATH"

rm -rf output output-*

# counting occurences of source
hadoop jar ./out/artifacts/mapreduce_jar/mapreduce.jar countsum:0 ./merged_dataset2.csv ./output-count-source
# counting occurences of type
hadoop jar ./out/artifacts/mapreduce_jar/mapreduce.jar countsum:2 ./merged_dataset2.csv ./output-count-type
# counting occurences of brand
hadoop jar ./out/artifacts/mapreduce_jar/mapreduce.jar countsum:3 ./merged_dataset2.csv ./output-count-brand


# computing average of price difference between list price and deal price
hadoop jar ./out/artifacts/mapreduce_jar/mapreduce.jar pricediffavg ./merged_dataset2.csv ./output-pricediffavg


# counting average rating (grouped in bins of 0.5) per type
hadoop jar ./out/artifacts/mapreduce_jar/mapreduce.jar rating:2 ./merged_dataset2.csv ./output-rating-type
# computing average rating (grouped in bins of 0.5) per brand
hadoop jar ./out/artifacts/mapreduce_jar/mapreduce.jar rating:3 ./merged_dataset2.csv ./output-rating-brand

