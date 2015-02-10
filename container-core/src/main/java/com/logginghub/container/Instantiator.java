package com.logginghub.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by james on 10/02/15.
 */
public class Instantiator {

    private List<String> packagePrefixes = new ArrayList<String>();

    public void addClassnameResolutionPackage(String packagePrefix) {
        packagePrefixes.add(packagePrefix);
    }

    public void removeClassnameResolutionPackage(String packagePrefix) {
        packagePrefixes.remove(packagePrefix);
    }

    public void instantiate(Container container) {

        final List<Module> modules = container.getModules();

        List<Module> done = new ArrayList<Module>(modules);
        List<Module> todo = new ArrayList<Module>(modules);

        boolean progress = true;

        while (progress && !todo.isEmpty()) {

            progress = false;

            final Iterator<Module> iterator = todo.iterator();
            while (iterator.hasNext()) {

                final Module module = iterator.next();

                final String name = module.getName();

                Class<?> resolved = resolveClass(name);

                if (resolved == null) {
                    throw new ContainerException(String.format("Failed to resolve module class '%s'", name));
                }

                // TODO : might need to pull in references from other containers/container collections?
                Object instance = createInstance(resolved, module, done);

                if (resolved == null) {
                    throw new ContainerException(String.format("Failed to create an instance of module class '%s'", name));
                }

                module.setInstance(instance);
                done.add(module);
                iterator.remove();
                progress = true;
            }
        }

    }

    private Object createInstance(Class<?> clazz, Module module, List<Module> potentialCollaborators) {

        final Constructor<?>[] constructors = clazz.getConstructors();

        Object instance = null;

        for (Constructor<?> constructor : constructors) {

            final Class<?>[] parameterTypes = constructor.getParameterTypes();

            Object[] potentialArguments = new Object[parameterTypes.length];

            boolean viable = true;

            for (int i = 0; i < parameterTypes.length; i++) {

                final Class<?> parameterType = parameterTypes[i];
                final String parameterTypeShortName = dropCaps(parameterType.getSimpleName());
                final String referenceKey = parameterTypeShortName + "Ref";

                final String attribute = module.getAttribute(referenceKey);
                Object collaborator;
                if (attribute != null) {
                    collaborator = findCollaboratorWithId(parameterType, attribute, potentialCollaborators);
                } else {
                    collaborator = findCollaborator(parameterType, potentialCollaborators);
                }

                if (collaborator == null) {
                    viable = false;
                    break;
                } else {
                    potentialArguments[i] = collaborator;
                }
            }

            if (viable) {
                try {
                    instance = constructor.newInstance(potentialArguments);
                    break;
                } catch (InstantiationException e) {
                    throw new ContainerException(String.format("Failed to instantiate '%s'", clazz.getName()), e);
                } catch (IllegalAccessException e) {
                    throw new ContainerException(String.format("Failed to instantiate '%s'", clazz.getName()), e);
                } catch (InvocationTargetException e) {
                    throw new ContainerException(String.format("Failed to instantiate '%s'", clazz.getName()), e);
                }
            }

        }

        return instance;
    }

    private Object findCollaboratorWithId(Class<?> parameterType, String id, List<Module> potentialCollaborators) {

        Object found = null;

        for (Module module : potentialCollaborators) {
            final Object instance = module.getInstance();
            if (id.equals(module.getId()) && parameterType.isAssignableFrom(instance.getClass())) {
                found = instance;
                break;
            }
        }

        return found;
    }

    private Object findCollaborator(Class<?> parameterType, List<Module> potentialCollaborators) {

        Object found = null;

        for (Module module : potentialCollaborators) {
            final Object instance = module.getInstance();
            if (parameterType.isAssignableFrom(instance.getClass())) {
                found = instance;
                break;
            }
        }

        return found;
    }

    private Class<?> resolveClass(String name) {

        String fixedName = capitalise(name);

        Class<?> resolved = null;

        // Check to see if it is fully qualified already
        try {
            resolved = Class.forName(fixedName);
        } catch (ClassNotFoundException e) {
            // Nope, so try the resolution packages
            for (String packagePrefix : packagePrefixes) {
                String attemptedName = packagePrefix + "." + fixedName;
                try {
                    resolved = Class.forName(attemptedName);
                    break;
                } catch (ClassNotFoundException e1) {
                }
            }
        }

        return resolved;
    }

    private String capitalise(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private String dropCaps(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }


}
