package com.github.k24.tsugaru.buoy;

/**
 * Template for each Buoy.
 * <p/>
 * Created by k24 on 2015/06/30.
 */
public interface BuoyTemplate<Lane> {
    /**
     * Arrange this for a lane.
     * <p/>
     * If configuring in runtime is needed, use this method and return an instance configured.
     *
     * @param lane to arrange this for
     * @return an instance of lane arranged this
     */
    Lane arrange(Lane lane);
}