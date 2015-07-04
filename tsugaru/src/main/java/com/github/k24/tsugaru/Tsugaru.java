package com.github.k24.tsugaru;

import com.github.k24.tsugaru.lane.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Thin wrapper for well-known libraries.
 * <p/>
 * Created by k24 on 2015/06/22.
 */
public final class Tsugaru {
    public static final double VERSION = 0.05_00;

    public static final int VERSION_INT = (int) (VERSION * 1_00_00);

    //region Internal
    static Tsugaru sInstance;

    private Mediation mediation;

    static synchronized Tsugaru getInstance() {
        if (sInstance == null) {
            sInstance = new Tsugaru();
        }
        return sInstance;
    }

    Tsugaru() {
    }

    void setMediation(Mediation mediation) {
        this.mediation = mediation;
    }
    //endregion

    //region Lane

    /**
     * Get a lane to ship JSON.
     *
     * @param buoys to mediate the lane
     * @return an instance of the lane
     */
    public static JsonLane json(JsonLane.Buoy... buoys) {
        return getInstance().mediation.mediate(buoys);
    }

    /**
     * Get a lane to a storage.
     *
     * @param buoys to mediate the lane
     * @return an instance of the lane
     */
    public static StoreLane store(StoreLane.Buoy... buoys) {
        return getInstance().mediation.mediate(buoys);
    }

    /**
     * Get a lane to ship an event via a bus.
     *
     * @param buoys to mediate the lane
     * @return an instance of the lane
     */
    public static EventBusLane bus(EventBusLane.Buoy... buoys) {
        return getInstance().mediation.mediate(buoys);
    }

    /**
     * Get a lane to network.
     *
     * @param buoys to mediate the lane
     * @return an instance of the lane
     */
    public static NetworkLane network(NetworkLane.Buoy... buoys) {
        return getInstance().mediation.mediate(buoys);
    }

    /**
     * Get a lane to a promise.
     *
     * @param buoys to mediate the lane
     * @return an instance of the lane
     */
    public static PromiseLane promise(PromiseLane.Buoy... buoys) {
        return getInstance().mediation.mediate(buoys);
    }

    /**
     * Get a lane to ship a message to a logger
     *
     * @param buoys to mediate the lane
     * @return an instance of the lane
     */
    public static LoggerLane logger(LoggerLane.Buoy... buoys) {
        return getInstance().mediation.mediate(buoys);
    }

    //endregion

    //region Configure

    public static class Configuration {
        /**
         * Configure {@link Tsugaru} with mediation.
         * <p/>
         * The mediation should arrange if configuring in runtime is needed.
         *
         * @param mediation to resolve implementation
         */
        public static void apply(Mediation mediation) {
            getInstance().setMediation(mediation);
        }

        /**
         * Configure {@link Tsugaru} with each parameter.
         *
         * @return an instance to configure {@link Tsugaru}
         */
        public static Configurator configurator() {
            return new Configurator(getInstance());
        }

        /**
         * Configure {@link Tsugaru} with mediation.
         * <p/>
         * The mediation should arrange if configuring in runtime is needed.
         *
         * @param instanceHolder to keep the instance
         */
        public static void keepInstance(InstanceHolder instanceHolder) {
            instanceHolder.keepInstance(getInstance());
        }
    }

    /**
     * To configure {@link Tsugaru}.
     */
    public static class Configurator {
        private final Tsugaru tsugaru;
        private final Map<Class<?>, Object> lanes = new HashMap<>();

        Configurator(Tsugaru tsugaru) {
            this.tsugaru = tsugaru;
        }

        public Configurator json(JsonLane lane) {
            lanes.put(JsonLane.class, lane);
            return this;
        }

        public Configurator store(StoreLane lane) {
            lanes.put(StoreLane.class, lane);
            return this;
        }

        public Configurator bus(EventBusLane lane) {
            lanes.put(EventBusLane.class, lane);
            return this;
        }

        public Configurator network(NetworkLane lane) {
            lanes.put(NetworkLane.class, lane);
            return this;
        }

        public Configurator promise(PromiseLane lane) {
            lanes.put(PromiseLane.class, lane);
            return this;
        }

        public Configurator logger(LoggerLane lane) {
            lanes.put(LoggerLane.class, lane);
            return this;
        }


        public void apply() {
            tsugaru.setMediation(newMediation());
        }

        @SuppressWarnings("unchecked")
        <T> T getLane(Class<T> clazz) {
            return (T) lanes.get(clazz);
        }

        Mediation newMediation() {
            return new SimpleMediation() {
                @Override
                protected <T> T getLane(Class<T> clazz) {
                    return Configurator.this.getLane(clazz);
                }
            };
        }
    }

    public interface InstanceHolder {
        void keepInstance(Tsugaru tsugaru);
    }

    //endregion
}
