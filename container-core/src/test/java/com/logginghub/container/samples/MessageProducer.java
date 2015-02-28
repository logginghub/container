package com.logginghub.container.samples;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by james on 25/02/15.
 */
public class MessageProducer {

    private List<MessageConsumer> consumers = new CopyOnWriteArrayList<MessageConsumer>();

    public String send(String url, String payload) {
        Message message = new Message();
        message.url = url;
        message.payload = payload;

        String response = null;
        if(consumers.isEmpty()) {
            throw new RuntimeException("No consumers");
        }
        else {
            for (MessageConsumer consumer : consumers) {
                response = consumer.consume(message);
            }
        }

        return response;
    }

    public void subscribe(MessageConsumer consumer) {
        consumers.add(consumer);
    }

}
