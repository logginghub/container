package com.logginghub.container.samples
import com.logginghub.container.loader.ContainerCMLLoader
import com.logginghub.container.loader.InstantiatingContainerLoader
import com.logginghub.container.loader.Instantiator
import com.logginghub.container.samples.fixtures.MessageConsumer
import com.logginghub.container.samples.fixtures.MessageProducer
import com.logginghub.container.samples.fixtures.MessageRouter
import spock.lang.Ignore
import spock.lang.Specification
/**
 * Created by james on 02/04/15.
 */
class TestNewParser extends Specification{

    def configuration = File.createTempFile("test", ".xml")
    def instantiator = new Instantiator("com.logginghub.container.samples.fixtures")
    def loader = new InstantiatingContainerLoader(instantiator, new ContainerCMLLoader())

    @Ignore
    def "test CML"() {

        given:
        configuration.deleteOnExit()
        configuration << "<container>"
        configuration << "<messageProducer/>"
        configuration << "<messageConsumer/>"
        configuration << "<messageRouter>"
        configuration << "   <routeAlternative name='test' source='messageProducer' destination='messageConsumer' url='*'/>"
        configuration << "</messageRouter>"
        configuration << "</container>"

        when:
        def stream = new FileInputStream(configuration)
        def container = loader.loadFromStream(stream)

        then:
        def router = container.findFirst(MessageRouter.class).instance
        router.routes.size() == 1
        router.routes[0].name == "test"
        router.routes[0].source instanceof MessageProducer
        router.routes[0].destination instanceof MessageConsumer
        router.routes[0].url == "*"

    }
}
