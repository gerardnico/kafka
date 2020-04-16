package com.gerardnico.kafka.demo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TopicText implements Topic, AutoCloseable {

    public static final String TEXT_LINES_TOPIC = "TextLinesTopic";
    private BufferedReader text = new BufferedReader(new InputStreamReader(TopicText.class.getResourceAsStream("/loremipsum.txt")));

    public String getName() {
        return TEXT_LINES_TOPIC;
    }

    public String get(){
        try {
            return text.readLine();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void close() throws Exception {
        text.close();
    }
}
