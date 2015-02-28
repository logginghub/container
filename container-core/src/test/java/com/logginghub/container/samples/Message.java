package com.logginghub.container.samples;

/**
 * Created by james on 25/02/15.
 */
public class Message {
    public String url;
    public String payload;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("url='").append(url).append('\'');
        sb.append(", payload='").append(payload).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
