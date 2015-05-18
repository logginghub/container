package com.logginghub.container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The intermediate model that contains all the information required to instantiate and configure an individual instance
 */
public class Module implements Asynchronous {

    private final String name;
    private Object instance;
    private String id;

    private List<SubElement> subElements = new ArrayList<SubElement>();

    private Map<String, String> attributes = new HashMap<String, String>();

    public Module(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public List<SubElement> getSubElements() {
        return subElements;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Module{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", instance=").append(instance);
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void start() {
        invokeOptionalMethod("start");
    }

    private void invokeOptionalMethod(String method) {
        try {
            Method start = instance.getClass().getMethod(method, new Class[]{});
            start.invoke(instance);
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        invokeOptionalMethod("stop");
    }

    /**
     * Encapsulates sub-element configuration for the module
     */
    public static class SubElement {
        private List<SubElement> subElements = new ArrayList<SubElement>();

        private List<Attribute> attributes = new ArrayList<Attribute>();
        private Map<String, String> attributesMap = new HashMap<String, String>();

        private final String name;

        /**
         * Used for xml characters <tag>characters</tag> approach
         */
        private String subElementValue;

        public SubElement(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<SubElement> getSubElements() {
            return subElements;
        }

        public Map<String, String> getAttributesMap() {
            return attributesMap;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("SubElement{");
            sb.append("name='").append(name).append('\'');
            sb.append(", attributes=").append(attributes);
            sb.append(", subElements=").append(subElements);
            sb.append('}');
            return sb.toString();
        }

        public void addAttribute(String key, String value) {
            Attribute attribute = new Attribute();
            attribute.key = key;
            attribute.value = value;
            attributes.add(attribute);
            attributesMap.put(key, value);
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public void setSubElementValue(String subElementValue) {
            this.subElementValue = subElementValue;
        }

        public String getSubElementValue() {
            return subElementValue;
        }
    }

    public static class Attribute {
        public String key;
        public String value;
    }
}
