package com.gerardnico.kafka.demo;

public class TopicNumber implements Topic {

    public static final String NUMBER_TOPIC = "NumberTopic";
    private int i = 0;

    public String getName() {
        return NUMBER_TOPIC;
    }

    public String get(){
        i++;
        return String.valueOf(i);
    }

    @Override
    public void close() throws Exception {

    }
}
