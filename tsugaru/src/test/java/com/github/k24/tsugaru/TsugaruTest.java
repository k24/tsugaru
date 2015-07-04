package com.github.k24.tsugaru;

import com.github.k24.tsugaru.lane.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import net.arnx.jsonic.JSON;
import org.jdeferred.DonePipe;
import org.jdeferred.FailPipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Testing for Tsugaru.
 * <p/>
 * Created by k24 on 2015/06/22.
 */
public class TsugaruTest {

    @Before
    public void setUp() {
        Tsugaru.sInstance = null;
    }

    @Test
    public void undefined() {
        try {
            Tsugaru.json();
            Assert.fail("Not Thrown");
        } catch (NullPointerException e) {
            // OK
        }

        try {
            Tsugaru.store();
            Assert.fail("Not Thrown");
        } catch (NullPointerException e) {
            // OK
        }

        try {
            Tsugaru.bus();
            Assert.fail("Not Thrown");
        } catch (NullPointerException e) {
            // OK
        }

        try {
            Tsugaru.network();
            Assert.fail("Not Thrown");
        } catch (NullPointerException e) {
            // OK
        }

        try {
            Tsugaru.promise();
            Assert.fail("Not Thrown");
        } catch (NullPointerException e) {
            // OK
        }

        try {
            Tsugaru.logger();
            Assert.fail("Not Thrown");
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void undefinedLane() {
        Tsugaru.Configuration.configurator()
                .apply();

        Assert.assertNull(Tsugaru.json());
        Assert.assertNull(Tsugaru.store());
        Assert.assertNull(Tsugaru.bus());
        Assert.assertNull(Tsugaru.network());
        Assert.assertNull(Tsugaru.promise());
        Assert.assertNull(Tsugaru.logger());
    }

    @Test
    public void configure() {
        // json
        JsonLane jsonLane = Mockito.mock(JsonLane.class);

        Tsugaru.Configuration.configurator()
                .json(jsonLane)
                .apply();

        Tsugaru.json().decode("{}", Object.class);
        Tsugaru.json().encode(new HashMap<>());

        Mockito.verify(jsonLane).decode("{}", Object.class);
        Mockito.verify(jsonLane).encode(new HashMap<>());

        // store
        StoreLane storeLane = Mockito.mock(StoreLane.class);

        Tsugaru.Configuration.configurator()
                .store(storeLane)
                .apply();

        Tsugaru.store().load("int", 0);
        Tsugaru.store().save("key", "value");

        Mockito.verify(storeLane).load("int", 0);
        Mockito.verify(storeLane).save("key", "value");

        // bus
        EventBusLane busLane = Mockito.mock(EventBusLane.class);

        Tsugaru.Configuration.configurator()
                .bus(busLane)
                .apply();

        EventBusLane.Subscriber<Object> subscriber = new EventBusLane.Subscriber<Object>() {
            @Override
            public void onEvent(Object event) {
            }
        };
        EventBusLane.Registration registration = new EventBusLane.Registration() {
        };
        Mockito.when(busLane.register(subscriber)).thenReturn(registration);
        Object event = new Object();

        Tsugaru.bus().register(subscriber);
        Tsugaru.bus().post(event);
        Tsugaru.bus().unregister(subscriber, registration);

        Mockito.verify(busLane).register(subscriber);
        Mockito.verify(busLane).post(event);
        Mockito.verify(busLane).unregister(subscriber, registration);

        // network
        NetworkLane networkLane = Mockito.mock(NetworkLane.class);

        Tsugaru.Configuration.configurator()
                .network(networkLane)
                .apply();

        NetworkLane.Request request = new NetworkLane.Request() {
            @Override
            public String url() {
                return "OK";
            }

            @Override
            public <T> T option(String key, T defaultValue) {
                return null;
            }

            @Override
            public void onResponse(NetworkLane.Response response) {
            }
        };

        Tsugaru.network().call(request);

        Mockito.verify(networkLane).call(request);

        // promise
        PromiseLane promiseLane = Mockito.mock(PromiseLane.class);

        Tsugaru.Configuration.configurator()
                .promise(promiseLane)
                .apply();

        Exception reason = new Exception();

        Tsugaru.promise().resolved("OK");
        Tsugaru.promise().rejected(reason);

        Mockito.verify(promiseLane).resolved("OK");
        Mockito.verify(promiseLane).rejected(reason);

        // logger
        LoggerLane loggerLane = Mockito.mock(LoggerLane.class);

        Tsugaru.Configuration.configurator()
                .logger(loggerLane)
                .apply();

        String arg1 = "1";
        Integer arg2 = 2;

        Tsugaru.logger().log("OK", arg1, arg2);

        Mockito.verify(loggerLane).log("OK", arg1, arg2);
    }

    @Test
    public void configureWithMediation() throws IOException, InterruptedException {
        Tsugaru.Configuration.apply(new SampleMediation());

        // json
        Object jsonObject = Tsugaru.json().decode("{}", Object.class);
        String jsonString = Tsugaru.json().encode(jsonObject);
        Assert.assertNotNull(jsonObject);
        Assert.assertEquals("{}", jsonString);

        // store
        Integer value = Tsugaru.store().load("int", 0);
        Assert.assertEquals(0, value.intValue());
        Tsugaru.store().save("int", 1);
        value = Tsugaru.store().load("int", 0);
        Assert.assertEquals(1, value.intValue());

        // bus
        final AtomicReference<String> result = new AtomicReference<>();
        EventBusLane.Subscriber<String> subscriber = new EventBusLane.Subscriber<String>() {
            public void onEvent(String string) {
                result.set(string);
            }
        };
        Tsugaru.bus().register(subscriber);
        Tsugaru.bus().post("Posted by EventBus");
        Assert.assertEquals("Posted by EventBus", result.get());
        result.set("");
        Tsugaru.bus().unregister(subscriber);
        Tsugaru.bus().post("Not reached");
        Assert.assertEquals("", result.get());

        // network
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("ok"));
        server.start();
        final URL url = server.getUrl("/ok");
        try {
            final AtomicReference<NetworkLane.Response> responseRef = new AtomicReference<>();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            Tsugaru.network().call(new NetworkLane.Request() {
                @Override
                public String url() {
                    return url.toString();
                }

                @Override
                public <T> T option(String key, T defaultValue) {
                    return null;
                }

                @Override
                public void onResponse(NetworkLane.Response response) {
                    responseRef.set(response);
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            NetworkLane.Response response = responseRef.get();
            Assert.assertNull(response.error());
            Assert.assertEquals("ok", new String(response.body()));
        } finally {
            server.shutdown();
        }

        // promise
        final AtomicReference<String> resultValue = new AtomicReference<>();
        final AtomicReference<Exception> resultReason = new AtomicReference<>();
        PromiseLane.Promise<String> promise = Tsugaru.promise().resolved("O").then(new PromiseLane.OnResolved<String>() {
            @Override
            public String onResolved(String value) throws Exception {
                return value + "K";
            }
        }).then(new PromiseLane.OnResolved<String>() {
            @Override
            public String onResolved(String value) throws Exception {
                resultValue.set(value);
                throw new RuntimeException("reason");
            }
        }).rescue(new PromiseLane.OnRejected<String>() {
            @Override
            public String onRejected(Exception reason) throws Exception {
                resultReason.set(reason);
                throw reason;
            }
        });
        promise.waitForCompletion();
        Assert.assertEquals("OK", resultValue.get());
        Assert.assertEquals("reason", resultReason.get().getMessage());

        // logger
        Tsugaru.logger().log("message: %d", 226);
        Tsugaru.logger().log(new RuntimeException(), "error: %f", 33.4);
    }

    public static class SampleMediation implements Mediation {

        private JsonLane jsonLane = new JsonLane() {
            @Override
            public <T> T decode(String string, Class<T> clazz) {
                return JSON.decode(string, clazz);
            }

            @Override
            public String encode(Object object) {
                return JSON.encode(object);
            }
        };
        private StoreLane storeLane = new StoreLane() {
            Map<String, Object> map = new HashMap<>();

            @SuppressWarnings("unchecked")
            @Override
            public <T> T load(String key, T defaultValue) {
                return (T) (map.containsKey(key) ? map.get(key) : defaultValue);
            }

            @Override
            public void save(String key, Object value) {
                map.put(key, value);
            }
        };
        private EventBusLane busLane = new EventBusLane() {
            final EventBus bus = new EventBus();
            final IdentityHashMap<Subscriber, List<Registration>> subscribers = new IdentityHashMap<>();

            @Override
            public <T> Registration register(final Subscriber<T> subscriber) {
                List<Registration> registrations = subscribers.get(subscriber);
                if (registrations == null) {
                    registrations = new LinkedList<>();
                    subscribers.put(subscriber, registrations);
                }
                Registration registration = new Registration() {
                    @Subscribe
                    public void onEvent(T event) {
                        subscriber.onEvent(event);
                    }
                };
                registrations.add(registration);
                bus.register(registration);
                return registration;
            }

            @Override
            public void unregister(Subscriber<?> subscriber) {
                List<Registration> registrations = subscribers.remove(subscriber);
                if (registrations == null) return;
                for (Registration registration : registrations) {
                    bus.unregister(registration);
                }
            }

            @Override
            public void unregister(Subscriber<?> subscriber, Registration registration) {
                List<Registration> registrations = subscribers.get(subscriber);
                if (registrations == null) return;
                registrations.remove(registration);
                bus.unregister(registration);
            }

            @Override
            public void post(Object object) {
                bus.post(object);
            }
        };
        private NetworkLane networkLane = new NetworkLane() {
            @Override
            public void call(Request request) {
                try {
                    com.squareup.okhttp.Response response = new OkHttpClient().newCall(new com.squareup.okhttp.Request.Builder()
                            .url(request.url())
                            .build())
                            .execute();
                    if (response.isSuccessful()) {
                        final byte[] body = response.body().bytes();
                        request.onResponse(new Response() {
                            @Override
                            public byte[] body() {
                                return body;
                            }

                            @Override
                            public Exception error() {
                                return null;
                            }
                        });
                    } else {
                        throw new RuntimeException("" + response.code());
                    }
                } catch (final Exception e) {
                    request.onResponse(new Response() {
                        @Override
                        public byte[] body() {
                            return null;
                        }

                        @Override
                        public Exception error() {
                            return e;
                        }
                    });
                }
            }
        };
        private PromiseLane promiseLane = new PromiseLane() {
            @Override
            public <T> Promise<T> promise(PromiseCallback promiseCallback) {
                final DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                promiseCallback.call(new Result<T>() {
                    @Override
                    public void resolve(T value) {
                        deferred.resolve(value);
                    }

                    @Override
                    public void reject(Exception reason) {
                        deferred.reject(reason);
                    }
                });
                return new PromiseDeferredAdapter<>(deferred.promise());
            }

            @Override
            public <T> Promise<T> resolved(T value) {
                DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                deferred.resolve(value);
                return new PromiseDeferredAdapter<>(deferred.promise());
            }

            @Override
            public <T> Promise<T> rejected(Exception reason) {
                DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                deferred.reject(reason);
                return new PromiseDeferredAdapter<>(deferred.promise());
            }
        };
        private LoggerLane loggerLane = new LoggerLane() {
            @Override
            public void log(String format, Object... args) {
                LoggerFactory.getLogger("Tsugaru").debug(format, args);
            }

            @Override
            public void log(Throwable throwable, String format, Object... args) {
                LoggerFactory.getLogger("Tsugaru").debug(String.format(format, args), throwable);
            }
        };

        @Override
        public JsonLane mediate(JsonLane.Buoy... buoys) {
            return jsonLane;
        }

        @Override
        public StoreLane mediate(StoreLane.Buoy... buoys) {
            return storeLane;
        }

        @Override
        public EventBusLane mediate(EventBusLane.Buoy... buoys) {
            return busLane;
        }

        @Override
        public NetworkLane mediate(NetworkLane.Buoy... buoys) {
            return networkLane;
        }

        @Override
        public PromiseLane mediate(PromiseLane.Buoy... buoys) {
            return promiseLane;
        }

        @Override
        public LoggerLane mediate(LoggerLane.Buoy... buoys) {
            return loggerLane;
        }
    }

    private static class PromiseDeferredAdapter<T> implements PromiseLane.Promise<T> {
        private Promise<T, Exception, Void> promise;

        public PromiseDeferredAdapter(org.jdeferred.Promise<T, Exception, Void> promise) {
            this.promise = promise;
        }

        @Override
        public PromiseLane.Promise<T> then(final PromiseLane.OnResolved<T> onResolved, final PromiseLane.OnRejected<T> onRejected) {
            DonePipe<T, T, Exception, Void> donePipe = null;
            FailPipe<Exception, T, Exception, Void> failPipe = null;
            final Promise<T, Exception, Void> current = promise;
            if (onResolved != null) {
                donePipe = new DonePipe<T, T, Exception, Void>() {
                    @Override
                    public Promise<T, Exception, Void> pipeDone(T t) {
                        try {
                            T value = onResolved.onResolved(t);
                            if (value == t) return current; // Returned as is

                            DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                            deferred.resolve(value);
                            return deferred.promise();
                        } catch (Exception e) {
                            DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                            deferred.reject(e);
                            return deferred.promise();
                        }
                    }
                };
            }
            if (onRejected != null) {
                failPipe = new FailPipe<Exception, T, Exception, Void>() {
                    @Override
                    public Promise<T, Exception, Void> pipeFail(Exception e) {
                        try {
                            // Twist
                            DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                            deferred.resolve(onRejected.onRejected(e));
                            return deferred.promise();
                        } catch (Exception e1) {
                            if (e == e1) return current; // Rethrown as is

                            DeferredObject<T, Exception, Void> deferred = new DeferredObject<>();
                            deferred.reject(e1);
                            return deferred.promise();
                        }
                    }
                };
            }
            promise = promise.then(donePipe, failPipe);
            return this;
        }

        @Override
        public PromiseLane.Promise<T> then(PromiseLane.OnResolved<T> onResolved) {
            return then(onResolved, null);
        }

        @Override
        public PromiseLane.Promise<T> rescue(PromiseLane.OnRejected<T> onRejected) {
            return then(null, onRejected);
        }

        @Override
        public void waitForCompletion() throws InterruptedException {
            this.promise.waitSafely();
        }

        @Override
        public boolean waitForCompletion(long millis) throws InterruptedException {
            this.promise.waitSafely(millis);
            return !promise.isPending();
        }
    }
}
