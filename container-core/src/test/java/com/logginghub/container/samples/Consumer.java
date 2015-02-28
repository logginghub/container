package com.logginghub.container.samples;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 10/02/15.
 */
public class Consumer {

    private List<String> events = new ArrayList<String>();
    private Producer producer;

    public Consumer(Producer producer) {
        this.producer = producer;
        producer.addListener(new Producer.Listener() {
            @Override public void onEvent(String produced) {
                events.add(produced);
            }
        });
    }

    public Producer getProducer() {
        return producer;
    }

    public List<String> getEvents() {
        return events;
    }
}
