package com.gerardnico.kafka.demo;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * See:
 * https://kafka.apache.org/10/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
 *
 */
public class Producer {

    public static void main(String[] args) throws UnknownHostException {

        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "all");
        config.put("retries", 0);
        config.put("batch.size", 16384);
        config.put("linger.ms", 1);
        config.put("buffer.memory", 33554432);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (
        // The producer consists of a pool of buffer space that holds records that haven't yet been transmitted to the server
        // as well as a background I/O thread that is responsible for turning these records into requests and transmitting them to the cluster.
        KafkaProducer kafkaProducer = new KafkaProducer<String, String>(config);
        ) {

            // The topic will be created if not present
            String topic = Topics.MY_TOPIC;
            String key;
            String value;

            for (int i = 0; i < 100; i++) {
                key = "k" + Integer.toString(i);
                value = Integer.toString(i);
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

                // All writes are asynchronous by default.
                // The Java producer includes a send() API which returns a future which can be polled to get the result of the send.
                // The send() method is asynchronous.
                // When called it adds the record to a buffer of pending record sends and immediately returns.
                Future<RecordMetadata> future = kafkaProducer.send(producerRecord);

                try {
                    // To make writes synchronous, just wait on the returned future.
                    RecordMetadata metadata = future.get();
                    System.out.println(metadata);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            // Failure to close the producer after use will leak these resources.
        }

    }

    //$
    // kafka-console-consumer.sh --bootstrap-server localhost:9092     --topic mytopic     --from-beginning     --formatter kafka.tools.DefaultMessageFormatter     --property print.key=true     --property print.value=true     -- property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer     --property value.deserializer=org.apache.kafka.common.serialization.StringSerializer

}
