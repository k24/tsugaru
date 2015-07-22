package com.github.k24.tsugaru;

import com.github.k24.tsugaru.buoy.BuoyTemplate;
import com.github.k24.tsugaru.lane.*;

/**
 * Simple implementation of Mediation.
 * <p/>
 * Created by k24 on 2015/06/30.
 */
public abstract class SimpleMediation implements Mediation {
    @SafeVarargs
    public static <T> T arrange(T lane, BuoyTemplate<T>... buoys) {
        for (BuoyTemplate<T> buoy : buoys) {
            lane = buoy.placeTo(lane);
        }
        return lane;
    }

    protected abstract <T> T getLane(Class<T> clazz);

    @Override
    public JsonLane mediate(JsonLane.Buoy... buoys) {
        return arrange(getLane(JsonLane.class), buoys);
    }

    @Override
    public StoreLane mediate(StoreLane.Buoy... buoys) {
        return arrange(getLane(StoreLane.class), buoys);
    }

    @Override
    public EventBusLane mediate(EventBusLane.Buoy... buoys) {
        return arrange(getLane(EventBusLane.class), buoys);
    }

    @Override
    public NetworkLane mediate(NetworkLane.Buoy... buoys) {
        return arrange(getLane(NetworkLane.class), buoys);
    }

    @Override
    public PromiseLane mediate(PromiseLane.Buoy... buoys) {
        return arrange(getLane(PromiseLane.class), buoys);
    }

    @Override
    public LoggerLane mediate(LoggerLane.Buoy... buoys) {
        return arrange(getLane(LoggerLane.class), buoys);
    }
}
