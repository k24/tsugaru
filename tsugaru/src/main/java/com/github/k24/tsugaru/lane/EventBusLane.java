package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

/**
 * Lane to ship an event via a bus.
 * <p>
 * Created by k24 on 2015/06/24.
 */
public interface EventBusLane {

    /**
     * Register a subscriber to listen events.
     *
     * @param subscriber to listen events
     * @param <T>        type of the event
     * @return an instance to unregister
     */
    <T> Registration register(Subscriber<T> subscriber);

    /**
     * Unregister a subscriber.
     *
     * @param subscriber to unregister
     */
    void unregister(Subscriber<?> subscriber);

    /**
     * Unregister a registration of a subscriber.
     *
     * @param subscriber   contains the registration to unregister
     * @param registration to unregister
     */
    void unregister(Subscriber<?> subscriber, Registration registration);

    /**
     * Post an event to subscribers.
     *
     * @param event to post
     */
    void post(Object event);

    /**
     * Subscriber for an event.
     *
     * @param <T> type of the event.
     */
    interface Subscriber<T> {
        void onEvent(T event);
    }

    /**
     * Registration to unregister.
     */
    interface Registration {
    }

    /**
     * To arrange the lane.
     * <p>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<EventBusLane> {
        @Override
        public EventBusLane placeTo(EventBusLane lane) {
            if (lane instanceof Arrangeable) {
                return accept((Arrangeable) lane);
            }
            throwIfRequired();
            return lane;
        }

        protected abstract EventBusLane accept(Arrangeable arrangeable);
    }

    interface Arrangeable extends EventBusLane {
        EventBusLane arrange(Buoy buoy);
    }
}
