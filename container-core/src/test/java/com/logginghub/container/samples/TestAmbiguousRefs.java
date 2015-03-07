package com.logginghub.container.samples;

import com.logginghub.container.Container;
import com.logginghub.container.loader.ContainerBuilder;
import com.logginghub.container.loader.ContainerJSONLoader;
import com.logginghub.container.loader.ContainerXMLLoader;
import com.logginghub.container.loader.InstantiatingContainerLoader;
import com.logginghub.container.loader.Instantiator;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by james on 10/02/15.
 */
public class TestAmbiguousRefs {

    @Test public void test_xml() {
        Instantiator instantiator = new Instantiator();
        instantiator.addClassnameResolutionPackage("com.logginghub.container.samples");
        InstantiatingContainerLoader loader = new InstantiatingContainerLoader(instantiator, new ContainerXMLLoader());

        Container container = loader.loadFromResource("samples/ambiguous_refs.xml");
        validate(container);
    }

    @Test public void test_json() {
        Instantiator instantiator = new Instantiator();
        instantiator.addClassnameResolutionPackage("com.logginghub.container.samples");
        InstantiatingContainerLoader loader = new InstantiatingContainerLoader(instantiator, new ContainerJSONLoader());

        Container container = loader.loadFromResource("samples/ambiguous_refs.json");
        validate(container);
    }

    @Test public void test_builder() {
        Instantiator instantiator = new Instantiator();
        instantiator.addClassnameResolutionPackage("com.logginghub.container.samples");
        ContainerBuilder loader = new ContainerBuilder();

        loader.addModule("producer");
        loader.addModule("producer").id("producer2");
        loader.addModule("consumer");
        loader.addModule("consumer").id("consumer2").attribute("producerRef", "producer2");

        Container container = loader.build();
        instantiator.instantiate(container);

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
        assertThat(container.getModules().get(2).getId(), is(nullValue()));
        assertThat(container.getModules().get(3).getId(), is("consumer2"));

        Producer producerA = (Producer) container.getModules().get(0).getInstance();
        Producer producerB = (Producer) container.getModules().get(1).getInstance();
        Consumer consumerA = (Consumer) container.getModules().get(2).getInstance();
        Consumer consumerB = (Consumer) container.getModules().get(3).getInstance();

        assertThat(consumerA.getProducer(), is(producerA));
        assertThat(consumerB.getProducer(), is(producerB));

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
