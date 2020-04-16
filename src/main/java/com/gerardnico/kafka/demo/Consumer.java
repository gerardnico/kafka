package com.gerardnico.kafka.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class Consumer {

    /**
     * The whole thing is partition based
     * The subscription to a topic is a scam, subscribe to partitions
     *
     * Code is from
     * https://kafka.apache.org/20/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
     * Same as:
     *
     * kafka-console-consumer.sh --bootstrap-server localhost:9092     --topic mytopic     --from-beginning     --formatter kafka.tools.DefaultMessageFormatter     --property print.key=true     --property print.value=true     -- property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer     --property value.deserializer=org.apache.kafka.common.serialization.StringSerializer
     * @param args
     */
    public static void main(String[] args) {

        // To report on a specific topic, change this value
        // String topicName = TopicNumber.NUMBER_TOPIC;
        String topicName = "WordsWithCountsTopic"; //TopicText.TEXT_LINES_TOPIC;

        // http://kafka.apache.org/documentation.html#consumerconfigs
        Properties props = new Properties();
        // The connection server (a list)
        props.put("bootstrap.servers", "localhost:9092");
        //  to be able to track the source of requests
        props.put("client.id", "test");
        // A unique string that identifies the consumer group this consumer belongs to.
        props.put("group.id", "test");
        // consumer.poll() will return all message (committed or not)
        props.put("isolation.level", "read_uncommitted");
        // the consumer's offset will be periodically committed in the background.
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        System.out.println("Topics List:");
        Map<String, List<PartitionInfo>> topics = consumer.listTopics();

        if (!topics.keySet().contains(topicName)) {
            System.err.println("Topic not found. We can also see this topics");
            topics.forEach((t, v) -> System.out.println(" * " + t));
            throw new RuntimeException("Topic (" + topicName + ") not found or unable to see it");
        }

        System.out.println("Topic (" + topicName + ") found");

        System.out.println("Partition info for topic (" + topicName + "): ");
        final Set<TopicPartition> partitions = new HashSet<>();
        for (final PartitionInfo partition : consumer.partitionsFor(topicName)) {
            partitions.add(new TopicPartition(partition.topic(), partition.partition()));
        }
        consumer.assign(partitions);
        Set<TopicPartition> assignedPartitions = consumer.assignment();
        System.out.println("Assigned Partition (" + assignedPartitions.size() + "): ");

        System.out.println("  * " + partitions.size() + " partitions found");
        Map<TopicPartition, Long> offsetsPartitions = consumer.beginningOffsets(partitions);
        offsetsPartitions.forEach((t, l) -> {
            System.out.println("   * Partition (" + t + ") has a beginning   offset at " + l);
            System.out.println("   * Partition (" + t + ") has a next record offset at " + consumer.position(t));
        });

        // Go to the beginning
        consumer.seekToBeginning(partitions);


        final int minBatchSize = 10;
        List<ConsumerRecord<String, String>> consumerRecords = new ArrayList<>();
        while (true) {
            System.out.println("Polling");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));

            partitions.forEach(t->{
                System.out.println("   * Partition (" + t + ") has a next record offset at " + consumer.position(t));
            });

            for (ConsumerRecord<String, String> record : records) {
                consumerRecords.add(record);
            }
            System.out.println("consumerRecords.size: " + consumerRecords.size());
            if (consumerRecords.size() >= minBatchSize) {
                insertWhereYouWant(consumerRecords);
                consumer.commitSync();
                consumerRecords.clear();
            }
        }

    }

    /**
     * File, Db, Stdout
     *
     * @param buffer
     */
    private static void insertWhereYouWant(List<ConsumerRecord<String, String>> buffer) {
        for (ConsumerRecord<String, String> record : buffer)
            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
    }
}
