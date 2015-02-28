package com.logginghub.container.samples;

import com.logginghub.container.Container;
import org.junit.Test;

/**
 * Created by james on 28/02/15.
 */
public class TestPartial {
    @Test
    public void test() {

        Producer producer = new Producer() {
            @Override
            public void produce(String message) {
                super.produce(message + " Aha!");
            }
        };

        Container container = new Container();

        // Register the external module as a potential collaborator for things loaded later
        container.addExternalModule("producer", producer);

        // Load the stuff in the xml file - in this case its just a consumer; without the producer having been provided externally, the configuration would have failed
        container.loadFromXml("samples/ambiguous.xml");

    }
}
