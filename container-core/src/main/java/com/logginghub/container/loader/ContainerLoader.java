package com.logginghub.container.loader;

import com.logginghub.container.Container;

import java.io.InputStream;

/**
 * @author cspiking
 */
public interface ContainerLoader {

    Container loadFromResource(String string) ;

    Container loadFromString(String string);

    Container loadFromStream(InputStream is);

}
