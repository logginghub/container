package com.logginghub.container;

import java.util.*;

/**
 * Created by james on 10/02/15.
 */
public class Container implements Asynchronous {

    private List<Module> modules = new ArrayList<Module>();

    private Map<String, Module> modulesById = new HashMap<String, Module>();

    public void add(Module module) {
        modules.add(module);
        modulesById.put(module.getId(), module);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    @Override
    public void start() {
        for (Module module : modules) {
            module.start();
        }
    }

    @Override
    public void stop() {
        ListIterator li = modules.listIterator();
        while(li.hasPrevious()) {
            Module module = (Module) li.previous();
            module.stop();
        }
    }
}
