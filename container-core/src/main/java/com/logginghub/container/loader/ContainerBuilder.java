package com.logginghub.container.loader;

import com.logginghub.container.Container;
import com.logginghub.container.Module;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cspiking
 */
public class ContainerBuilder {

    private Container container = new Container();

    private AtomicBoolean alreadyBuilt = new AtomicBoolean(false);

    public Container build() {
        if(alreadyBuilt.compareAndSet(false, true)) {
            return container;
        } else {
            throw new IllegalStateException("Builder already used. Instantiate a new builder");
        }
    }

    public ContainerBuilder add(String name) {
        checkBuiltAndThrowIfAlreadyBuilt();
        container.add(new Module(name));
        return this;
    }

    public ModuleBuilder addModule(String name) {
        checkBuiltAndThrowIfAlreadyBuilt();
        final Module module = new Module(name);
        container.add(module);
        return new ModuleBuilder(module);
    }

    private void checkBuiltAndThrowIfAlreadyBuilt() {
        if(alreadyBuilt.get() == true) {
            throw new IllegalStateException("Builder already used. Instantiate a new builder");
        }
    }

    public static class ModuleBuilder {
        private final Module module;

        public ModuleBuilder(Module module) {
            this.module = module;
        }

        public ModuleBuilder id(String id) {
            module.setId(id);
            return this;
        }

        public void attribute(String key, String value) {
            module.addAttribute(key, value);
        }
    }

}
