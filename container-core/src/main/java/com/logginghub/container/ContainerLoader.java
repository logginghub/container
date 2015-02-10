package com.logginghub.container;

/**
 * Created by james on 10/02/15.
 */
public interface ContainerLoader {

    void addClassnameResolutionPackage(String packagePrefix);
    void removeClassnameResolutionPackage(String packagePrefix);
}
