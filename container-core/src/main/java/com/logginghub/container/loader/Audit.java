package com.logginghub.container.loader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 02/04/15.
 */
public class Audit {

    private List<String> messages = new ArrayList<String>();

    public void append(String message, Object... params) {
        messages.add(String.format(message, params));
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return  messages.toString();
    }
}
