package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

/**
 * Lane to a promise
 * <p/>
 * Created by k24 on 2015/06/24.
 */
public interface PromiseLane {

    /**
     * Promise to defer an operation.
     *
     * @param promiseCallback for deferred
     * @param <T>             type to resolve
     * @return an instance of the promise
     */
    <T> Promise<T> promise(PromiseCallback promiseCallback);

    /**
     * Promise for resolved.
     *
     * @param value resolved
     * @param <T>   type to resolve
     * @return an instance of the promise
     */
    <T> Promise<T> resolved(T value);

    /**
     * Promise for rejected.
     *
     * @param reason rejected
     * @param <T>    type to resolve
     * @return an instance of the promise
     */
    <T> Promise<T> rejected(Exception reason);

    /**
     * Callback for Promise.
     */
    interface PromiseCallback {
        /**
         * Called when prepared to promise.
         *
         * @param result to pass a value or a reason
         * @param <T>    type to resolve
         */
        <T> void call(Result<T> result);
    }

    /**
     * Promise as thenable.
     *
     * @param <T> type to resolve
     */
    interface Promise<T> {
        /**
         * Do after completion.
         *
         * @param onResolved to handle a value when resolved
         * @param onRejected to handle an error when rejected
         * @return an instance of the promise
         */
        Promise<T> then(OnResolved<T> onResolved, OnRejected<T> onRejected);

        /**
         * Do after resolved.
         *
         * @param onResolved to handle a value when resolved
         * @return an instance of the promise
         */
        Promise<T> then(OnResolved<T> onResolved);

        /**
         * Do after rejected.
         *
         * @param onRejected to handle an error when rejected
         * @return an instance of the promise
         */
        Promise<T> rescue(OnRejected<T> onRejected);

        /**
         * Wait for completion.
         *
         * @throws InterruptedException if interrupted.
         */
        void waitForCompletion() throws InterruptedException;

        /**
         * Wait for completion.
         *
         * @param millis to timeout
         * @return true: completed, false: not completed
         * @throws InterruptedException if interrupted
         */
        boolean waitForCompletion(long millis) throws InterruptedException;
    }

    /**
     * @param <T>
     */
    interface Result<T> {
        void resolve(T value);

        void reject(Exception reason);
    }

    /**
     * Handler on resolved.
     *
     * @param <T> type to resolve
     */
    interface OnResolved<T> {
        /**
         * Called when resolved.
         * <p/>
         * Return a value to keep resolved.
         * Or for notifying an error, throw Exception.
         *
         * @param value resolved
         * @return an instance of a value to resolve
         * @throws Exception to reject
         */
        T onResolved(T value) throws Exception;
    }

    /**
     * Handler on rejected.
     *
     * @param <T> type to resolve
     */
    interface OnRejected<T> {
        /**
         * Called when rejected.
         * <p/>
         * Rethrow Exception to keep rejected.
         * Or for twisting to success, return a value.
         *
         * @param reason rejected
         * @return an instance of a value to resolve
         * @throws Exception to reject
         */
        T onRejected(Exception reason) throws Exception;
    }

    /**
     * To arrange the lane.
     * <p/>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<PromiseLane> {
        @Override
        public PromiseLane placeTo(PromiseLane lane) {
            if(lane instanceof Acceptable) {
                return ((Acceptable)lane).accept(this);
            }
            throwIfRequired();
            return lane;
        }
    }

    interface Acceptable {
        PromiseLane accept(Buoy buoy);
    }
}
