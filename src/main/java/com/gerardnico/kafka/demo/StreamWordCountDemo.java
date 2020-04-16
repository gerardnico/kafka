package com.gerardnico.kafka.demo;

/*
 * From:
 * https://github.com/apache/kafka/blob/trunk/streams/examples/src/main/java/org/apache/kafka/streams/examples/wordcount/WordCountDemo.java
 * https://github.com/confluentinc/examples/blob/kafka-0.10.0.0-cp-3.0.0/kafka-streams/src/main/java/io/confluent/examples/streams/WordCountLambdaExample.java
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Demonstrates, using the high-level KStream DSL, how to implement the WordCount program
 * that computes a simple word occurrence histogram from an input text.
 * <p>
 * In this example, the input stream reads from a topic named "streams-plaintext-input", where the values of messages
 * represent lines of text; and the histogram output is written to topic "streams-wordcount-output" where each record
 * is an updated count of a single word.
 * <p>
 * Before running this example you must create the input topic and the output topic (e.g. via
 * bin/kafka-topics.sh --create ...), and write some data to the input topic (e.g. via
 * bin/kafka-console-producer.sh). Otherwise you won't see any data arriving in the output topic.
 */
public class StreamWordCountDemo {

    // Serdes are Serializers/deserializers (serde) for String and Long types
    // Ser-/Deserialization (key.serde, value.serde):
    // Serialization and deserialization in Kafka Streams happens whenever data needs to be materialized,

    public static void main(String[] args) throws Exception {

        // The configuration of Kafka Streams is done by specifying parameters in a StreamsConfig instance.
        Properties props = new Properties();
        // Required
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Serializers/deserializers (serde) for String and Long types
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


        StreamsBuilder builder = buildStream();

        final KafkaStreams streams = new KafkaStreams(
                builder.build(),
                props);

        // Arg: the number of times countDown must be invoked before threads can pass through await
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler to catch control-c
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread("streams-wordcount-shutdown-hook") {
                            @Override
                            public void run() {
                                streams.close();
                                latch.countDown();
                            }
                        });


        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }


    private static StreamsBuilder buildStream() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
        // represent lines of text (for the sake of this example, we ignore whatever may be stored
        // in the message keys).
        // KStream is an abstraction of a record stream of KeyValue pairs,
        KStream<String, String> kStream =
                streamsBuilder.stream(
                        TopicText.TEXT_LINES_TOPIC,
                        Consumed.with(
                                Serdes.String(),
                                Serdes.String())
                );


        // KTable is an abstraction of a changelog stream from a primary-keyed table.
        // Each record in this changelog stream is an update on the primary-keyed table with the record key as the primary key.
        KTable<String, Long> kTable = kStream
                // Split each text line, by whitespace, into words.
                .flatMapValues(value -> {
                    List<String> strings = Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "));
                    System.out.println("Raw Message:" + strings.toString());
                    return strings;
                })
                // Group the text words as message keys
                .groupBy((key, value) -> {
                    System.out.println("Processed Message: key:" + key + ", value:" + value);
                    return value;
                })
                // Count the occurrences of each word (message key).
                .count();

        // Transform the Ktable as a stream to be able to:
        //    * iterate over
        //    * and save it
        KStream<String, Long> kTableStream = kTable.toStream();

        // Iteration
        kTableStream.foreach(
                (key, value) -> {
                    System.out.println("kTable Message: key:" + key + ", value:" + value);
                }
        );

        // Store the running counts as a changelog stream to the output topic.
        kTableStream
                .to(
                        "streams-wordcount-output",
                        Produced.with(
                                Serdes.String(),
                                Serdes.Long()
                        )
                );


        return streamsBuilder;

    }
}