package com.logginghub.container;

/**
 * Created by james on 10/02/15.
 */
public abstract class ContainerLoaderBase implements ContainerLoader {

    private Instantiator instantiator = new Instantiator();

    public void addClassnameResolutionPackage(String packagePrefix) {
        instantiator.addClassnameResolutionPackage(packagePrefix);
    }

    public void removeClassnameResolutionPackage(String packagePrefix) {
        instantiator.removeClassnameResolutionPackage(packagePrefix);
    }

    protected void instantiate(Container container) {
        instantiator.instantiate(container);
    }

}
