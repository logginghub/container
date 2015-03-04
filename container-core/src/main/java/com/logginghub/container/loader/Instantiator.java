package com.logginghub.container.loader;

import com.logginghub.container.Container;

/**
 * @author cspiking
 */
public interface Instantiator {

    void addClassnameResolutionPackage(String packagePrefix);

    void removeClassnameResolutionPackage(String packagePrefix);

    void instantiate(Container container);

}
