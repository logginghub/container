package com.logginghub.container.samples.fixtures;

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
        String name;
        MessageProducer source;
        MessageConsumer destination;
        String url;
    }

    private Set<MessageProducer> subscribedToProducers = new HashSet<MessageProducer>();
    private List<Route> routes = new CopyOnWriteArrayList<Route>();

    // CCCT = Concrete config context type
    public void addRouteAlternative(Route route) {
        routes.add(route);

        if (!subscribedToProducers.contains(route.source)) {
            route.source.subscribe(this);
            subscribedToProducers.add(route.source);
        }
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void addRoute(MessageProducer producer, MessageConsumer consumer, @ContainerParam(value = "url") String urlStartsWith) {

        Route route = new Route();
        route.destination = consumer;
        route.source = producer;
        route.url = urlStartsWith;

        addRouteAlternative(route);
    }

    @Override
    public String consume(Message message) {

        String response = null;
        boolean foundRoute = false;
        for (Route route : routes) {
            if(message.url.startsWith(route.url)) {
                response = route.destination.consume(message);
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
