package com.logginghub.container.samples;

import com.logginghub.container.Container;
import com.logginghub.container.ContainerBuilderLoader;
import com.logginghub.container.ContainerJSONLoader;
import com.logginghub.container.ContainerXMLLoader;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by james on 10/02/15.
 */
public class TestAmbiguousRefs {

    @Test public void test_xml() {
        ContainerXMLLoader loader = new ContainerXMLLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");
        Container container = loader.loadFromResource("samples/ambiguous_refs.xml");
        validate(container);
    }

    @Test public void test_json() {
        ContainerJSONLoader loader = new ContainerJSONLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");
        Container container = loader.loadFromResource("samples/ambiguous_refs.json");
        validate(container);
    }

    @Test public void test_builder() {
        ContainerBuilderLoader loader = new ContainerBuilderLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");

        loader.addModule("producer");
        loader.addModule("producer").id("producer2");
        loader.addModule("consumer").id("consumer1");
        loader.addModule("consumer").id("consumer2").attribute("producerRef", "producer2");

        Container container = loader.load();
        validate(container);
    }


    private void validate(Container container) {
        assertThat(container.getModules().size(), is(4));

        assertThat(container.getModules().get(0).getInstance(), is(instanceOf(Producer.class)));
        assertThat(container.getModules().get(1).getInstance(), is(instanceOf(Producer.class)));
        assertThat(container.getModules().get(2).getInstance(), is(instanceOf(Consumer.class)));
        assertThat(container.getModules().get(3).getInstance(), is(instanceOf(Consumer.class)));

        assertThat(container.getModules().get(0).getId(), is(nullValue()));
        assertThat(container.getModules().get(1).getId(), is("producer2"));
        assertThat(container.getModules().get(2).getId(), is("consumer1"));
        assertThat(container.getModules().get(3).getId(), is("consumer2"));

        Producer producerA = (Producer) container.getModules().get(0).getInstance();
        Producer producerB = (Producer) container.getModules().get(1).getInstance();
        Consumer consumerA = (Consumer) container.getModules().get(2).getInstance();
        Consumer consumerB = (Consumer) container.getModules().get(3).getInstance();

        assertThat(consumerA.getEvents().size(), is(0));
        assertThat(consumerB.getEvents().size(), is(0));

        producerA.produce("test message");
        assertThat(consumerA.getEvents().size(), is(1));
        assertThat(consumerB.getEvents().size(), is(0));

        producerB.produce("test message");
        assertThat(consumerA.getEvents().size(), is(1));
        assertThat(consumerB.getEvents().size(), is(1));
    }
}
