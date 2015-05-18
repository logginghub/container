package com.logginghub.container.samples

import com.logginghub.container.PropertyResolver
import com.logginghub.container.loader.ContainerXMLLoader
import com.logginghub.container.loader.InstantiatingContainerLoader
import com.logginghub.container.loader.Instantiator
import com.logginghub.container.samples.fixtures.Greeter
import spock.lang.Specification
/**
 * Created by james on 02/04/15.
 */
class TestPropertyInjection extends Specification{

    def configuration = File.createTempFile("test", ".xml")
    def instantiator = new Instantiator("com.logginghub.container.samples.fixtures")
    def loader = new InstantiatingContainerLoader(instantiator, new ContainerXMLLoader())

    def "test string injection"() {

        given:
        def properties = new Properties();
        properties.put("who", "James")
        instantiator.addPropertyResolver(new PropertyResolver.PropertiesPropertyResolver(properties))

        configuration.deleteOnExit()
        configuration << '<container>'
        configuration << '<greeter who="This is ${who} to greet"/>'
        configuration << '</container>'

        when:
        def stream = new FileInputStream(configuration)
        def container = loader.loadFromStream(stream)

        then:
        def greeter = container.findFirst(Greeter.class).instance
        greeter.greet() == "Hello This is James to greet value 10"
    }

    def "test int injection"() {

        given:
        def properties = new Properties();
        properties.put("who", "James")
        properties.put("value", "42")
        instantiator.addPropertyResolver(new PropertyResolver.PropertiesPropertyResolver(properties))

        configuration.deleteOnExit()
        configuration << '<container>'
        configuration << '<greeter who="${who}" value="${value}" />'
        configuration << '</container>'

        when:
        def stream = new FileInputStream(configuration)
        def container = loader.loadFromStream(stream)

        then:
        def greeter = container.findFirst(Greeter.class).instance
        greeter.greet() == "Hello James value 42"
    }
}
