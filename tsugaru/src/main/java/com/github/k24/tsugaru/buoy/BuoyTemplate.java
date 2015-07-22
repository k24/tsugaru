package com.github.k24.tsugaru.buoy;

/**
 * Template for each Buoy.
 * <p/>
 * Created by k24 on 2015/06/30.
 */
public abstract class BuoyTemplate<Lane> {
    /**
     * For Arrangement a lane.
     * <p/>
     * If configuring in runtime is needed, use this method and return an instance configured.
     *
     * @param lane to be placed
     * @return an instance of lane
     */
    public abstract Lane placeTo(Lane lane);

    protected void throwIfRequired() {
        if (isRequired()) throw new UnsupportedOperationException();
    }

    public boolean isRequired() {
        return false;
    }
}
