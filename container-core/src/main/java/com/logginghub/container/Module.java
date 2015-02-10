package com.logginghub.container;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by james on 10/02/15.
 */
public class Module {

    private final String name;
    private Object instance;
    private String id;
    private Map<String, String> attributes = new HashMap<String, String>();

    public Module(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) { return attributes.get(key); }
}
