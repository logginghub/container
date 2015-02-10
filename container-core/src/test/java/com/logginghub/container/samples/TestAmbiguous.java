package com.logginghub.container.samples;

import com.logginghub.container.Container;
import com.logginghub.container.ContainerBuilderLoader;
import com.logginghub.container.ContainerJSONLoader;
import com.logginghub.container.ContainerXMLLoader;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by james on 10/02/15.
 */
public class TestAmbiguous {

    @Test public void test_xml() {

        ContainerXMLLoader loader = new ContainerXMLLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");

        Container container = loader.load("/samples/ambiguous.xml");

        validate(container);
    }


    @Test public void test_json() {

        ContainerJSONLoader loader = new ContainerJSONLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");

        Container container = loader.load("/samples/ambiguous.json");

        validate(container);
    }

    @Test public void test_builder() {

        ContainerBuilderLoader loader = new ContainerBuilderLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");

        loader.add("producer").add("producer").add("consumer");

        Container container = loader.load();

        validate(container);
    }


    private void validate(Container container) {
        assertThat(container.getModules().size(), is(3));

        assertThat(container.getModules().get(0), is(instanceOf(Producer.class)));
        assertThat(container.getModules().get(1), is(instanceOf(Producer.class)));
        assertThat(container.getModules().get(2), is(instanceOf(Consumer.class)));

        Producer producerA = (Producer) container.getModules().get(0);
        Producer producerB = (Producer) container.getModules().get(1);
        Consumer consumer = (Consumer) container.getModules().get(2);

        // Verify they have been correctly bound
        assertThat(consumer.getEvents().size(), is(0));
        producerA.produce("test message");
        assertThat(consumer.getEvents().size(), is(1));

        // Make sure the second producer hasn't been bound
        producerB.produce("test message");
        assertThat(consumer.getEvents().size(), is(1));
    }
}
