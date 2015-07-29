package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

/**
 * Lane to network.
 * <p>
 * Created by k24 on 2015/06/24.
 */
public interface NetworkLane {

    Request request(String url);

    interface Request {
        Request header(String name, String value);

        Request body(byte[] bytes);

        Request field(String name, String value);

        Connection call(OnResponseListener onResponseListener);
    }

    interface Connection {
        void cancel();

        float progress();
    }

    interface OnResponseListener {
        void onResponse(Response response);
    }

    /**
     * Response gotten.
     */
    interface Response {
        String CLASS_STRING = "java.lang.String";
        String CLASS_INPUT_STREAM = "java.io.InputStream";
        String CLASS_READER = "java.io.Reader";
        String CLASS_BYTES = "[B";
        String CLASS_CHARS = "[C";
        String CLASS_INTS = "[I";

        /**
         * Content gotten.
         *
         * @return the object matched an acceptable class of the content
         */
        Object content(Class<?>... acceptableClasses);

        /**
         * Error occurred.
         *
         * @return null if no error
         */
        Exception error();
    }

    /**
     * To arrange the lane.
     * <p>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<NetworkLane> {
        @Override
        public NetworkLane placeTo(NetworkLane lane) {
            if (lane instanceof Arrangeable) {
                return accept((Arrangeable) lane);
            }
            throwIfRequired();
            return lane;
        }

        protected abstract NetworkLane accept(Arrangeable arrangeable);
    }

    interface Arrangeable extends NetworkLane {
        NetworkLane arrange(Buoy buoy);
    }
}
