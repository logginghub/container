package com.logginghub.container.loader;

import com.logginghub.container.Container;
import com.logginghub.container.Module;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Created by james on 10/02/15.
 */
public class ContainerXMLLoader implements ContainerLoader {

    public Container loadFromResource(String string) {
        return loadFromStream(ClassLoader.getSystemResourceAsStream(string));
    }

    public Container loadFromString(String string) {
        return loadFromStream(new ByteArrayInputStream(string.getBytes()));
    }

    public Container loadFromStream(InputStream is) {
        final Container container = new Container();
        final XMLInputFactory f = XMLInputFactory.newInstance();
        try {
            final XMLEventReader r = f.createXMLEventReader(new InputStreamReader(is));
            Module currentModule = null;

            while (r.hasNext()) {
                final XMLEvent event = r.nextEvent();
                if (event.isStartElement()) {
                    final StartElement element = (StartElement) event;
                    final String elementName = element.getName().toString();

                    if (elementName.equals("container")) {

                    } else {
                        if (currentModule != null) {
                            // This is a sub-element of a module
                            final Module.SubElement subElement = new Module.SubElement(elementName);
                            final Iterator attributeIterator = element.getAttributes();
                            while (attributeIterator.hasNext()) {
                                final Attribute attribute = (Attribute) attributeIterator.next();
                                final String key = attribute.getName().toString();
                                final String value = attribute.getValue();
                                subElement.getAttributes().put(key, value);
                            }
                            currentModule.getSubElements().add(subElement);
                        } else {
                            currentModule = new Module(elementName);
                            final Iterator attributeIterator = element.getAttributes();
                            while (attributeIterator.hasNext()) {
                                final Attribute attribute = (Attribute) attributeIterator.next();
                                final String key = attribute.getName().toString();
                                final String value = attribute.getValue();

                                if ("id".equals(key)) {
                                    currentModule.setId(value);
                                }
                                currentModule.addAttribute(key, value);
                            }
                        }
                    }
                } else if (event.isEndElement()) {
                    final EndElement element = (EndElement) event;
                    final String elementName = element.getName().toString();
                    if (elementName.equals("container")) {

                    } else if (elementName.equals(currentModule.getName())) {
                        container.add(currentModule);
                        currentModule = null;
                    }
                }

            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return container;
    }

}
