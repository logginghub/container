package com.logginghub.container.loader;

import com.logginghub.container.Container;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by james on 10/02/15.
 */
public class ContainerCMLLoader implements ContainerLoader {

    public Container loadFromResource(String string) {
        return loadFromStream(ClassLoader.getSystemResourceAsStream(string));
    }

    public Container loadFromString(String string) {
        return loadFromStream(new ByteArrayInputStream(string.getBytes()));
    }

    public Container loadFromStream(InputStream is) {
        final Container container = new Container();





        return container;
    }

}
