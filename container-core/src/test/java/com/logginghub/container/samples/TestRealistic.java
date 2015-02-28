package com.logginghub.container.samples;

import com.logginghub.container.Container;
import com.logginghub.container.ContainerXMLLoader;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by james on 10/02/15.
 */
public class TestRealistic {

    @Test
    public void test_xml() {
        ContainerXMLLoader loader = new ContainerXMLLoader();
        loader.addClassnameResolutionPackage("com.logginghub.container.samples");
        Container container = loader.loadFromResource("samples/realistic.1.xml");
        validate(container);
    }


    @Test @Ignore
    public void test_json() {
        fail();
    }

    @Test @Ignore
    public void test_builder() {
        fail();
    }


    private void validate(Container container) {
        assertThat(container.getModules().size(), is(4));

        assertThat(container.getModules().get(0).getInstance(), is(instanceOf(MessageProducer.class)));
        assertThat(container.getModules().get(1).getInstance(), is(instanceOf(MessageConsumer.class)));
        assertThat(container.getModules().get(2).getInstance(), is(instanceOf(MessageConsumer.class)));
        assertThat(container.getModules().get(3).getInstance(), is(instanceOf(MessageRouter.class)));

        MessageProducer producer = (MessageProducer) container.getModules().get(0).getInstance();
        MessageConsumer consumer1 = (MessageConsumer) container.getModules().get(1).getInstance();
        MessageConsumer consumer2 = (MessageConsumer) container.getModules().get(2).getInstance();

        MessageRouter router = (MessageRouter) container.getModules().get(3).getInstance();
        assertThat(router.getRoutes().size(), is(2));

        assertThat(consumer1.getId(), is("db"));
        assertThat(consumer2.getId(), is("cache"));

        // Verify they have been correctly bound

        assertThat(producer.send("/route1/a", "Hello route 1/a"), is("cache - Hello route 1/a"));
        assertThat(producer.send("/route2/b", "Hello route 2/b"), is("db - Hello route 2/b"));
    }
}
