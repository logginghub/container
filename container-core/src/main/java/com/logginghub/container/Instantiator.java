package com.logginghub.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        List<Module> done = new ArrayList<Module>();
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
                    throw new ContainerException(String.format("Failed to create an instance of module class '%s'",
                                                               name));
                }

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
                    module.setInstance(instance);
                    configure(module, potentialCollaborators);
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

    private void configure(Module module, List<Module> potentialCollaborators) {

        Object instance = module.getInstance();
        Class<?> instanceClass = instance.getClass();

        Map<String, String> attributes = module.getAttributes();
        for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {

            String key = attributeEntry.getKey();
            String setter = "set" + capitalise(key);

            if(!key.endsWith("Ref")) {
                try {
                    Method[] methods = instanceClass.getMethods();
                    for (Method method : methods) {
                        if(method.getName().equals(setter) && method.getParameterCount() == 1) {
                            method.invoke(instance, new Object[] { coerce(attributeEntry.getValue(), method.getParameterTypes()[0])});
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }

        List<Module.SubElement> subElements = module.getSubElements();
        for (Module.SubElement subElement : subElements) {

            String name = subElement.getName();

            // What could this mean... try a few alternatives

            // See if there is an 'add' method
            String addMethodName = "add" + capitalise(name);
            Method[] declaredMethods = instanceClass.getDeclaredMethods();

            boolean successfullyApplied = false;

            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getName().equals(addMethodName)) {
                    // Have we got enough potential arguments?
                    if (declaredMethod.getParameterCount() == subElement.getAttributes().size()) {
                        // Great, lets see if they can fit the required types

                        boolean viable = true;

                        Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                        Annotation[][] parametersAnnotations = declaredMethod.getParameterAnnotations();
                        Object[] potentialArguments = new Object[parameterTypes.length];

                        for (int i = 0; i < parameterTypes.length; i++) {

                            final Class<?> parameterType = parameterTypes[i];
                            Annotation[] parameterAnnotations = parametersAnnotations[i];

                            boolean hasAnnotation = false;

                            // TODO : this would be much easier using java 8 with the param names potentially available at runtime
                            if (parameterAnnotations != null) {
                                for (Annotation parameterAnnotation : parameterAnnotations) {
                                    if (parameterAnnotation.annotationType() == ContainerParam.class) {
                                        ContainerParam param = (ContainerParam)parameterAnnotation;
                                        String key = param.value();
                                        potentialArguments[i] = subElement.getAttributes().get(key);
                                        hasAnnotation = true;
                                        break;
                                    }
                                }
                            }

                            if (!hasAnnotation) {

                                final String parameterTypeShortName = dropCaps(parameterType.getSimpleName());
                                final String referenceKey = parameterTypeShortName + "Ref";

                                final String attribute = subElement.getAttributes().get(referenceKey);
                                Object collaborator;
                                if (attribute != null) {
                                    collaborator = findCollaboratorWithId(parameterType,
                                                                          attribute,
                                                                          potentialCollaborators);
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
                        }

                        if (viable) {
                            try {
                                declaredMethod.invoke(instance, potentialArguments);
                                successfullyApplied = true;
                                break;
                            } catch (IllegalAccessException e) {
                                throw new ContainerException(String.format("Failed to configure '%s'", module), e);
                            } catch (InvocationTargetException e) {
                                throw new ContainerException(String.format("Failed to configure '%s'", module), e);
                            }
                        }
                    }
                }
            }

            if(!successfullyApplied) {
                throw new ContainerException(String.format("Failed to apply sub-element '%s' to module '%s'", subElement, module));
            }
        }


    }

    private Object coerce(String value, Class<?> type) {
        return value;
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
