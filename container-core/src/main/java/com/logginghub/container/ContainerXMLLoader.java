package com.logginghub.container;

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
public class ContainerXMLLoader extends ContainerLoaderBase {

    public Container loadFromResource(String string) {
        return loadFromStream(ClassLoader.getSystemResourceAsStream(string));
    }

    public Container loadFromString(String string) {
        return loadFromStream(new ByteArrayInputStream(string.getBytes()));
    }

    public Container loadFromStream(InputStream is) {

        final Container container = new Container();

        XMLInputFactory f = XMLInputFactory.newInstance();

        try {
            XMLEventReader r = f.createXMLEventReader(new InputStreamReader(is));

            Module currentModule = null;

            while (r.hasNext()) {
                XMLEvent event = r.nextEvent();

                if (event.isStartElement()) {
                    StartElement element = (StartElement) event;

                    String elementName = element.getName().toString();

                    if (elementName.equals("container")) {

                    } else {
                        if (currentModule != null) {
                            // This is a sub-element of a module

                            Module.SubElement subElement = new Module.SubElement(elementName);

                            Iterator attributeIterator = element.getAttributes();
                            while (attributeIterator.hasNext()) {
                                Attribute attribute = (Attribute) attributeIterator.next();
                                String key = attribute.getName().toString();
                                String value = attribute.getValue();
                                subElement.getAttributes().put(key, value);
                            }

                            currentModule.getSubElements().add(subElement);

                        } else {
                            currentModule = new Module(elementName);

                            Iterator attributeIterator = element.getAttributes();
                            while (attributeIterator.hasNext()) {
                                Attribute attribute = (Attribute) attributeIterator.next();
                                String key = attribute.getName().toString();
                                String value = attribute.getValue();

                                if ("id".equals(key)) {
                                    currentModule.setId(value);
                                }

                                currentModule.addAttribute(key, value);

                            }
                        }
                    }

                } else if (event.isEndElement()) {
                    EndElement element = (EndElement) event;
                    String elementName = element.getName().toString();

                    if (elementName.equals("container")) {

                    } else if (elementName.equals(currentModule.getName())) {
                        container.add(currentModule);
                        currentModule = null;
                    }
                }

            }

            instantiate(container);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return container;
    }

}
