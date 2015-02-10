package com.logginghub.container;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by james on 10/02/15.
 */
public class ContainerCollection {

    private Map<String, Container> containers = new HashMap<String, Container>();

    public void put(String id, Container container) {
        containers.put(id, container);
    }

    public Container get(String id) {
        return containers.get(id);
    }

    public Set<String> getContainerIds() {
        return Collections.unmodifiableSet(containers.keySet());
    }


}
