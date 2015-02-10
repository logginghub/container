package com.logginghub.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by james on 10/02/15.
 */
public class Container {

    private List<Module> modules = new ArrayList<Module>();
    private Map<String, Module> modulesById = new HashMap<String, Module>();

    public void add(Module module) {
        modules.add(module);
        modulesById.put(module.getId(), module);
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }
}
