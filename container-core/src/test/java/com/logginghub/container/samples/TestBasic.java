package com.logginghub.container.samples;

import com.logginghub.container.Container;
import com.logginghub.container.loader.ContainerBuilder;
import com.logginghub.container.loader.ContainerJSONLoader;
import com.logginghub.container.loader.ContainerXMLLoader;
import com.logginghub.container.loader.InstantiatingContainerLoader;
import com.logginghub.container.loader.Instantiator;
import com.logginghub.container.loader.PojoInstantiator;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by james on 10/02/15.
 */
public class TestBasic {

    @Test public void test_xml() {

        Instantiator instantiator = new PojoInstantiator();
        instantiator.addClassnameResolutionPackage("com.logginghub.container.samples");
        InstantiatingContainerLoader loader = new InstantiatingContainerLoader(instantiator, new ContainerXMLLoader());

        Container container = loader.loadFromResource("samples/basic.xml");

        validate(container);
    }


    @Test public void test_json() {

        Instantiator instantiator = new PojoInstantiator();
        instantiator.addClassnameResolutionPackage("com.logginghub.container.samples");
        InstantiatingContainerLoader loader = new InstantiatingContainerLoader(instantiator, new ContainerJSONLoader());

        Container container = loader.loadFromResource("samples/basic.json");

        validate(container);
    }

    @Test public void test_builder() {

        Instantiator instantiator = new PojoInstantiator();
        instantiator.addClassnameResolutionPackage("com.logginghub.container.samples");
        ContainerBuilder loader = new ContainerBuilder();

        loader.add("producer").add("consumer");

        Container container = loader.build();
        instantiator.instantiate(container);
        validate(container);
    }


    private void validate(Container container) {
        assertThat(container.getModules().size(), is(2));

        // Producer will be first because we can't instantiate Consumer without it
        assertThat(container.getModules().get(0).getInstance(), is(instanceOf(Producer.class)));
        assertThat(container.getModules().get(1).getInstance(), is(instanceOf(Consumer.class)));

        Producer producer = (Producer) container.getModules().get(0).getInstance();
        Consumer consumer = (Consumer) container.getModules().get(1).getInstance();

        // Verify they have been correctly bound
        assertThat(consumer.getEvents().size(), is(0));
        producer.produce("test message");
        assertThat(consumer.getEvents().size(), is(1));
    }
}
