package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

/**
 * Lane to ship JSON.
 * <p/>
 * Created by k24 on 2015/06/22.
 */
public interface JsonLane {
    /**
     * Decode a JSON string to an object.
     *
     * @param <T>   destiny type to decode
     * @param string  string to decode as JSON
     * @param clazz destiny class to decode
     * @return an instance decoded
     */
    <T> T decode(String string, Class<T> clazz);

    /**
     * Encode a JSON object to encode as JSON
     *
     * @param object to encode as JSON
     * @return a string encoded
     */
    String encode(Object object);

    /**
     * To arrange the lane.
     * <p/>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<JsonLane> {
        @Override
        public JsonLane arrange(JsonLane lane) {
            if(lane instanceof Acceptable) {
                return ((Acceptable)lane).accept(this);
            }
            throwIfRequired();
            return lane;
        }
    }

    interface Acceptable {
        JsonLane accept(Buoy buoy);
    }

}
