package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

import java.util.Map;

/**
 * Lane to network.
 * <p/>
 * Created by k24 on 2015/06/24.
 */
public interface NetworkLane {

    /**
     * Send a request to get a response.
     *
     * @param request to send
     * @return an instance of the response
     */
    void call(Request request);

    /**
     * Request to send.
     */
    interface Request {
        /**
         * URL.
         *
         * @return as String
         */
        String url();

        /**
         * Get a value for an option.
         *
         * @param key          to a value
         * @param defaultValue of an option
         * @return a value for an option
         */
        <T> T option(String key, T defaultValue);

        void onResponse(Response response);
    }

    /**
     * Response gotten.
     */
    interface Response {
        /**
         * Content gotten.
         *
         * @return bytes of the content
         */
        byte[] body();

        /**
         * Error occurred.
         *
         * @return null if no error
         */
        Exception error();
    }

    /**
     * To arrange the lane.
     * <p/>
     * By default, this has no meaning.
     */
    interface Buoy extends BuoyTemplate<NetworkLane> {
    }
}
