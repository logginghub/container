package com.logginghub.container.samples

import com.logginghub.container.Container
import com.logginghub.container.loader.*
import com.logginghub.container.samples.fixtures.Consumer
import com.logginghub.container.samples.fixtures.Producer
import spock.lang.Specification
/**
 * Created by james on 01/04/15.
 */
class TestBasicSpock extends Specification  {

    def instantiator = new Instantiator("com.logginghub.container.samples.fixtures")
    def loader


    def "test_xml"() {
        when:
        loader = new InstantiatingContainerLoader(instantiator, new ContainerXMLLoader())
        Container container = loader.loadFromResource("samples/basic.xml");

        then:
        validate(container)
    }

    def "test_json"() {
        when:
        loader = new InstantiatingContainerLoader(instantiator, new ContainerJSONLoader());
        Container container = loader.loadFromResource("samples/basic.json");

        then:
        validate(container)
    }

    def "test_builder"() {
        when:
        loader = new ContainerBuilder();
        loader.add("producer").add("consumer");
        Container container = loader.build();
        instantiator.instantiate(container);

        then:
        validate(container)
    }

    void validate(container) {
        assert container.getModules().size() == 2
        assert container.getModules().get(0).getInstance() instanceof Producer
        assert container.getModules().get(1).getInstance() instanceof Consumer

        Consumer consumer = (Consumer) container.getModules().get(1).getInstance();
        assert consumer.getEvents().size() == 0

        Producer producer = (Producer) container.getModules().get(0).getInstance();
        producer.produce("test message");

        assert consumer.getEvents().size() == 1
    }

}
