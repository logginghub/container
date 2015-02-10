package com.logginghub.container;

/**
 * Created by james on 10/02/15.
 */
public class ContainerException extends RuntimeException {
    public ContainerException() {
    }

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}
