package com.logginghub.container.samples.fixtures;

/**
 * Created by james on 18/05/2015.
 */
public class Greeter {
    private String id;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public String who = "???";
    public int value = 10;

    public String greet() {
        return "Hello " + who + " value " + Integer.toString(value);
    }
}
