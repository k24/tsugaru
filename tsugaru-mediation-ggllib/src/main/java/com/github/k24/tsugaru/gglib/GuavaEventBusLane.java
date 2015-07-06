package com.github.k24.tsugaru.gglib;

import com.github.k24.tsugaru.lane.EventBusLane;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by k24 on 2015/07/05.
 */
public class GuavaEventBusLane implements EventBusLane {
    final EventBus bus = new EventBus();
    final IdentityHashMap<Subscriber, List<Registration>> subscribers = new IdentityHashMap<>();

    @Override
    public <T> Registration register(final Subscriber<T> subscriber) {
        List<Registration> registrations = subscribers.get(subscriber);
        if (registrations == null) {
            registrations = new LinkedList<>();
            subscribers.put(subscriber, registrations);
        }
        Registration registration = new Registration() {
            @Subscribe
            public void onEvent(T event) {
                subscriber.onEvent(event);
            }
        };
        registrations.add(registration);
        bus.register(registration);
        return registration;
    }

    @Override
    public void unregister(Subscriber<?> subscriber) {
        List<Registration> registrations = subscribers.remove(subscriber);
        if (registrations == null) return;
        for (Registration registration : registrations) {
            bus.unregister(registration);
        }
    }

    @Override
    public void unregister(Subscriber<?> subscriber, Registration registration) {
        List<Registration> registrations = subscribers.get(subscriber);
        if (registrations == null) return;
        registrations.remove(registration);
        bus.unregister(registration);
    }

    @Override
    public void post(Object object) {
        bus.post(object);
    }

}
