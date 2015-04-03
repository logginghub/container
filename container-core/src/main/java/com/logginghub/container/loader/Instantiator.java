package com.logginghub.container.loader;

import com.logginghub.container.Container;
import com.logginghub.container.ContainerException;
import com.logginghub.container.ContainerParam;
import com.logginghub.container.Module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

    private final List<String> packagePrefixes = new ArrayList<String>();
    private boolean applyInaccessibleFields = true;

    public void addClassnameResolutionPackage(String packagePrefix) {
        packagePrefixes.add(packagePrefix);
    }

    public void removeClassnameResolutionPackage(String packagePrefix) {
        packagePrefixes.remove(packagePrefix);
    }

    public Instantiator() {

    }

    public Instantiator(String... packagePrefixes) {
        for (String packagePrefix : packagePrefixes) {
            addClassnameResolutionPackage(packagePrefix);
        }
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

                Audit audit = new Audit();
                Class<?> resolved = resolveClass(name, audit);

                if (resolved == null) {
                    throw new ContainerException(String.format("Failed to resolve module class '%s' : audit '%s'", name, audit.toString()));
                }

                // TODO : might need to pull in references from other containers/container collections?
                Object instance = createInstance(resolved, module, done);

                if (resolved == null) {
                    throw new ContainerException(String.format("Failed to create an instance of module class '%s'", name));
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

            if (!key.endsWith("Ref")) {
                try {
                    Method[] methods = instanceClass.getMethods();
                    for (Method method : methods) {
                        if (method.getName().equals(setter) && method.getParameterCount() == 1) {
                            method.invoke(instance, new Object[]{coerce(attributeEntry.getValue(), method.getParameterTypes()[0])});
                            break;
                        }
                    }

                    Field[] fields = instanceClass.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equals(key) && field.isAccessible()) {
                            field.set(instance, coerce(attributeEntry.getValue(), field.getType()));
                            break;
                        }
                    }

                    if (applyInaccessibleFields) {
                        for (Field field : fields) {
                            if (field.getName().equals(key)) {
                                boolean fieldAccessibility = field.isAccessible();

                                if (!fieldAccessibility) {
                                    field.setAccessible(true);
                                }

                                Object potentialArgumentValue = findCollaboratorWithIdOrName(field.getType(),
                                                                                             attributeEntry.getValue(),
                                                                                             potentialCollaborators);

                                if (potentialArgumentValue == null) {
                                    try {
                                        potentialArgumentValue = coerce(attributeEntry.getValue(), field.getType());
                                    } catch (IllegalArgumentException e) {
                                        throw new RuntimeException(String.format("Failed to set field '%s' of type '%s' to value '%s'",
                                                                                 key,
                                                                                 field.getType(),
                                                                                 attributeEntry.getValue()));
                                    }
                                }

                                field.set(instance, potentialArgumentValue);

                                if (!fieldAccessibility) {
                                    field.setAccessible(false);
                                }

                                break;
                            }
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

                    boolean viable = true;
                    Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                    Object[] potentialArguments = new Object[parameterTypes.length];

                    // Have we got enough potential arguments?
                    if (declaredMethod.getParameterCount() == subElement.getAttributesMap().size()) {

                        // Great, lets see if they can fit the required types

                        Annotation[][] parametersAnnotations = declaredMethod.getParameterAnnotations();

                        for (int i = 0; i < parameterTypes.length; i++) {

                            final Class<?> parameterType = parameterTypes[i];
                            Annotation[] parameterAnnotations = parametersAnnotations[i];

                            boolean hasAnnotation = false;

                            // TODO : this would be much easier using java 8 with the param names potentially available at runtime
                            if (parameterAnnotations != null) {
                                for (Annotation parameterAnnotation : parameterAnnotations) {
                                    if (parameterAnnotation.annotationType() == ContainerParam.class) {
                                        ContainerParam param = (ContainerParam) parameterAnnotation;
                                        String key = param.value();
                                        String attributeValue = subElement.getAttributesMap().get(key);

                                        // Attribute value could be a coercable primitive, or maybe a reference to a module?
                                        Object potentialArgumentValue = findCollaboratorWithIdOrName(parameterType,
                                                                                                     attributeValue,
                                                                                                     potentialCollaborators);
                                        if (potentialArgumentValue == null) {
                                            // Maybe its a primitive then
                                            try {
                                                potentialArgumentValue = coerce(attributeValue, parameterType);
                                            } catch (IllegalArgumentException e) {
                                                viable = false;
                                                break;
                                            }
                                        }

                                        potentialArguments[i] = potentialArgumentValue;
                                        hasAnnotation = true;
                                        break;
                                    }
                                }
                            }

                            if (!hasAnnotation) {

                                final String parameterTypeShortName = dropCaps(parameterType.getSimpleName());
                                final String referenceKey = parameterTypeShortName + "Ref";

                                final String attribute = subElement.getAttributesMap().get(referenceKey);
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
                        }

                    } else if (declaredMethod.getParameterCount() == 1) {
                        // It could be a CCCT? (a concrete config context type)
                        Class<?> aClass = declaredMethod.getParameterTypes()[0];
                        Field[] declaredFields = aClass.getDeclaredFields();

                        if (declaredFields.length == subElement.getAttributesMap().size()) {
                            // Could be a contender - lets try and instantiate one of these things

                            Module temp = new Module("temp");
                            temp.setAttributes(subElement.getAttributesMap());
                            Object subElementInstance = createInstance(aClass, temp, potentialCollaborators);
                            temp.setInstance(subElementInstance);
                            configure(temp, potentialCollaborators);

                            potentialArguments[0] = subElementInstance;
                            viable = true;
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

            if (!successfullyApplied) {
                throw new ContainerException(String.format("Failed to apply sub-element '%s' to module '%s'", subElement, module));
            }
        }


    }

    private Object coerce(String param, Class<?> type) {
        Object result;

        if (type == String.class) {
            result = param;
        } else if (type == Integer.class || type == Integer.TYPE) {
            result = Integer.parseInt(param);
        } else if (type == Long.class || type == Long.TYPE) {
            result = Long.parseLong(param);
        } else if (type == Short.class || type == Short.TYPE) {
            result = Short.parseShort(param);
        } else if (type == Float.class || type == Float.TYPE) {
            result = Float.parseFloat(param);
        } else if (type == Double.class || type == Double.TYPE) {
            result = Double.parseDouble(param);
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            result = Boolean.parseBoolean(param);
        } else if (type == Byte.class || type == Byte.TYPE) {
            result = Byte.parseByte(param);
        } else {
            throw new IllegalArgumentException("Unable to coerce '" + param + "' into type '" + type.getName() + "'");
        }
        return result;
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

    private Object findCollaboratorWithIdOrName(Class<?> parameterType, String id, List<Module> potentialCollaborators) {

        Object found = null;

        for (Module module : potentialCollaborators) {
            final Object instance = module.getInstance();
            if ((id.equals(module.getId()) || id.equals(module.getName())) && parameterType.isAssignableFrom(instance.getClass())) {
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

    private Class<?> resolveClass(String name, Audit audit) {

        String fixedName = capitalise(name);

        Class<?> resolved = null;

        // Check to see if it is fully qualified already
        String div = "";
        try {
            audit.append("Is it a fully qualified class? : ", fixedName);
            resolved = Class.forName(fixedName);
        } catch (ClassNotFoundException e) {
            // Nope, so try the resolution packages
            for (String packagePrefix : packagePrefixes) {
                String attemptedName = packagePrefix + "." + fixedName;
                audit.append("Is it in this package : ", attemptedName);
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

    public void setApplyInaccessibleFields(boolean applyInaccessibleFields) {
        this.applyInaccessibleFields = applyInaccessibleFields;
    }

    public boolean isApplyInaccessibleFields() {
        return applyInaccessibleFields;
    }
}
