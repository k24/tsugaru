package com.github.k24.tsugaru.mediation.gglib;

import android.content.Context;

import com.github.k24.tsugaru.SimpleMediation;
import com.github.k24.tsugaru.lane.EventBusLane;
import com.github.k24.tsugaru.lane.JsonLane;
import com.github.k24.tsugaru.lane.NetworkLane;

import java.util.HashMap;

/**
 * Mediation implementation with G's libraries.
 * <p/>
 * Created by k24 on 2015/07/04.
 */
public class GgllibMediation extends SimpleMediation {
    private final HashMap<Class<?>, Object> instanceMap = new HashMap<>();
    private final Context context;

    public GgllibMediation(Context context) {
        this.context = context;
    }

    public void clear() {
        instanceMap.clear();
    }

    @SuppressWarnings("unchecked")
    public <Lane, Impl extends Lane> Impl getImplementation(Class<Lane> laneClass) {
        return (Impl) getLane(laneClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T getLane(Class<T> clazz) {
        Object lane = instanceMap.get(clazz);
        if (lane == null) {
            lane = newLane(clazz);
            instanceMap.put(clazz, lane);
        }
        return (T) lane;
    }

    protected Object newLane(Class<?> clazz) {
        switch (clazz.getSimpleName()) {
            case "JsonLane":
                return newJsonLane();
            case "EventBusLane":
                return newEventBusLane();
            case "NetworkLane":
                return newNetworkLane();
            default:
                throw new UnsupportedOperationException("Unknown Lane:" + clazz.getName());
        }
    }

    protected JsonLane newJsonLane() {
        return new GsonJsonLane();
    }

    protected EventBusLane newEventBusLane() {
        return new GuavaEventBusLane();
    }

    protected NetworkLane newNetworkLane() {
        return new VolleyNetworkLane(context);
    }
}
