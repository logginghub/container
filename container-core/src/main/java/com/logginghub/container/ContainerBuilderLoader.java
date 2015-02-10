package com.logginghub.container;

/**
 * Created by james on 10/02/15.
 */
public class ContainerBuilderLoader extends ContainerLoaderBase {

    private Container container = new Container();

    public Container load() {
        // Nothing to do, the builder has already got the intermediate objects required
        instantiate(container);
        return container;
    }

    public ContainerBuilderLoader add(String name) {
        container.add(new Module(name));
        return this;
    }

    public ModuleBuilder addModule(String name) {
        final Module module = new Module(name);
        container.add(module);
        return new ModuleBuilder(module);
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
