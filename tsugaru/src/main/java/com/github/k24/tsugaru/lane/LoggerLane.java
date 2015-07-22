package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

/**
 * Lane to ship a message to a logger.
 * <p/>
 * Created by k24 on 2015/06/29.
 */
public interface LoggerLane {

    /**
     * Log a message.
     *
     * @param format to log
     * @param args to format by the format
     */
    void log(String format, Object... args);

    /**
     * Log an error with a message.
     * @param throwable to log
     * @param format to log
     * @param args to format by the format
     */
    void log(Throwable throwable, String format, Object... args);

    /**
     * To arrange the lane.
     * <p/>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<LoggerLane> {
        @Override
        public LoggerLane placeTo(LoggerLane lane) {
            if(lane instanceof Acceptable) {
                return ((Acceptable)lane).accept(this);
            }
            throwIfRequired();
            return lane;
        }
    }

    interface Acceptable {
        LoggerLane accept(Buoy buoy);
    }
}
