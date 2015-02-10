package com.logginghub.container.samples;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 10/02/15.
 */
public class Consumer {

    private List<String> events = new ArrayList<String>();

    public Consumer(Producer producer) {
        producer.addListener(new Producer.Listener() {
            @Override public void onEvent(String produced) {
                events.add(produced);
            }
        });
    }

    public List<String> getEvents() {
        return events;
    }
}
