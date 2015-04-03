package com.logginghub.container.parser;

import java.util.List;

/**
 * Created by james on 02/04/15.
 */
public interface Option {

    boolean matches(Pointer pointer);
    Node consume(Pointer pointer);
    List<Option> getOptions();
    boolean isPopOptions();
}
