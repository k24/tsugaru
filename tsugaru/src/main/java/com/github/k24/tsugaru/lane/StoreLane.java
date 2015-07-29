package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

/**
 * Lane to a storage.
 * <p>
 * Created by k24 on 2015/06/23.
 */
public interface StoreLane {
    /**
     * Load a value from a storage.
     *
     * @param key          of a stored value
     * @param defaultValue the default value if none
     * @param <T>          type of the value
     * @return the stored value
     */
    <T> T load(String key, T defaultValue);

    /**
     * Save a value to a storage.
     *
     * @param key   of a value to store
     * @param value to store
     */
    void save(String key, Object value);

    /**
     * To arrange the lane.
     * <p>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<StoreLane> {
        @Override
        public StoreLane placeTo(StoreLane lane) {
            if (lane instanceof Arrangeable) {
                return accept((Arrangeable) lane);
            }
            throwIfRequired();
            return lane;
        }

        protected abstract StoreLane accept(Arrangeable arrangeable);
    }

    interface Arrangeable extends StoreLane {
        StoreLane arrange(Buoy buoy);
    }
}
