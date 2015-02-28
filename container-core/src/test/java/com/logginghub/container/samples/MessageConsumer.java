package com.logginghub.container.samples;

/**
 * Created by james on 25/02/15.
 */
public class MessageConsumer {

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String consume(Message message) {
        return id + " - " + message.payload;
    }
}
