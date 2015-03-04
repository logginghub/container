package com.logginghub.container.loader;

import com.logginghub.container.Container;

import java.io.InputStream;

/**
 * @author cspiking
 */
public class InstantiatingContainerLoader {

    private final Instantiator instantiator;
    private final ContainerLoader containerLoader;

    public InstantiatingContainerLoader(Instantiator instantiator, ContainerLoader containerLoader) {
        this.instantiator = instantiator;
        this.containerLoader = containerLoader;
    }

    public Container loadFromResource(String string) {
        final Container container = containerLoader.loadFromResource(string);
        instantiator.instantiate(container);
        return container;
    }

    public Container loadFromString(String string) {
        final Container container = containerLoader.loadFromString(string);
        instantiator.instantiate(container);
        return container;
    }

    public Container loadFromStream(InputStream is) {
        final Container container = containerLoader.loadFromStream(is);
        instantiator.instantiate(container);
        return container;
    }
}
