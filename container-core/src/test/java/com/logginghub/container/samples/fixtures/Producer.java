package com.logginghub.container.samples.fixtures;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by james on 10/02/15.
 */
public class Producer {

    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void produce(String message) {
        for (Listener listener : listeners) {
            listener.onEvent(message);
        }
    }

    public interface Listener {
        void onEvent(String produced);
    }
}
