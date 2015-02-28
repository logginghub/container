package com.logginghub.container.samples;

import com.logginghub.container.ContainerParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by james on 25/02/15.
 */
public class MessageRouter extends MessageConsumer {

    public static class Route {
        MessageProducer producer;
        MessageConsumer consumer;
        String urlStartsWith;
    }

    private Set<MessageProducer> subscribedToProducers = new HashSet<MessageProducer>();
    private List<Route> routes = new CopyOnWriteArrayList<Route>();

    public void addRoute(MessageProducer producer, MessageConsumer consumer, @ContainerParam(value = "urlStartsWith") String urlStartsWith) {

        Route route = new Route();
        route.consumer = consumer;
        route.producer = producer;
        route.urlStartsWith = urlStartsWith;

        routes.add(route);

        if (!subscribedToProducers.contains(producer)) {
            producer.subscribe(this);
            subscribedToProducers.add(producer);
        }

    }

    public List<Route> getRoutes() {
        return routes;
    }

    @Override
    public String consume(Message message) {

        String response = null;
        boolean foundRoute = false;
        for (Route route : routes) {
            if(message.url.startsWith(route.urlStartsWith)) {
                response = route.consumer.consume(message);
                foundRoute = true;
                break;
            }
        }

        if(!foundRoute) {
            throw new RuntimeException("No route found : " + message);
        }

        return response;
    }
}
