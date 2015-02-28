package com.logginghub.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The intermediate model that contains all the information required to instantiate and configure an individual instance
 */
public class Module {

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

    public String getAttribute(String key) { return attributes.get(key); }

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

    /**
     * Encapsulates sub-element configuration for the module
     */
    public static class SubElement {
        private List<SubElement> subElements = new ArrayList<SubElement>();

        private Map<String, String> attributes = new HashMap<String, String>();

        private final String name;

        public SubElement(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<SubElement> getSubElements() {
            return subElements;
        }

        public Map<String, String> getAttributes() {
            return attributes;
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
    }
}
