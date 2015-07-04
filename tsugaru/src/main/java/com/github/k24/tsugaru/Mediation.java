package com.github.k24.tsugaru;

import com.github.k24.tsugaru.lane.*;

/**
 * Mediation to mediate lanes,
 * <p/>
 * Created by k24 on 2015/06/22.
 */
public interface Mediation {
    /**
     * Mediate a lane to ship JSON.
     *
     * @param buoys to determine which lane to use
     * @return an instance of the lane
     */
    JsonLane mediate(JsonLane.Buoy... buoys);

    /**
     * Mediate a lane to a storage.
     *
     * @param buoys to determine which lane to use
     * @return an instance of the lane
     */
    StoreLane mediate(StoreLane.Buoy... buoys);

    /**
     * Mediate a lane to ship an event via a bus.
     * @param buoys to determine which lane to use
     * @return an instance of the lane
     */
    EventBusLane mediate(EventBusLane.Buoy... buoys);

    /**
     * Mediate a lane to network.
     *
     * @param buoys to determine which lane to use
     * @return an instance of the lane
     */
    NetworkLane mediate(NetworkLane.Buoy... buoys);

    /**
     * Mediate a lane to a promise.
     * @param buoys to determine which lane to use
     * @return an instance of the lane
     */
    PromiseLane mediate(PromiseLane.Buoy... buoys);

    /**
     * Mediate a lane to ship a message to a logger.
     *
     * @param buoys to determine which lane to use
     * @return an instance of the lane
     */
    LoggerLane mediate(LoggerLane.Buoy...buoys);
}
