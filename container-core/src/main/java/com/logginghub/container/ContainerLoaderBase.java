package com.logginghub.container;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 10/02/15.
 */
public abstract class ContainerLoaderBase implements ContainerLoader {
    private List<String> packagePrefixes = new ArrayList<String>();

    public void addClassnameResolutionPackage(String packagePrefix) {
        packagePrefixes.add(packagePrefix);
    }

    public void removeClassnameResolutionPackage(String packagePrefix) {
        packagePrefixes.remove(packagePrefix);
    }
}
