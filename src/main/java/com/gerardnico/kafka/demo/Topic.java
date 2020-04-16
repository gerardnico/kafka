package com.gerardnico.kafka.demo;

public interface Topic extends AutoCloseable {

    String getName();

    String get();

}
