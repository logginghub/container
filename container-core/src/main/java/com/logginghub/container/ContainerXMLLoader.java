package com.logginghub.container;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        try {
            SAXParser parser = parserFactor.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                Module currentModule = null;

                //Triggered when the start of tag is found.
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (qName.equals("container")) {

                    } else {
                        currentModule = new Module(qName);
                        int numberOfAttributes = attributes.getLength();
                        for (int i = 0; i < numberOfAttributes; i++) {
                            currentModule.addAttribute(attributes.getQName(i), attributes.getValue(i));
                        }
                    }
                }

                @Override public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equals("container")) {

                    } else {
                        container.add(currentModule);
                        currentModule = null;
                    }

                }

            };
            parser.parse(is, handler);
        } catch (ParserConfigurationException e) {
            throw new ContainerException(e);
        } catch (SAXException e) {
            throw new ContainerException(e);
        } catch (IOException e) {
            throw new ContainerException(e);
        }

        instantiate(container);

        return container;
    }

}
