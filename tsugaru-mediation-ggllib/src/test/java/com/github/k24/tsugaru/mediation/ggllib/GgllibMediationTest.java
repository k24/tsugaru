package com.github.k24.tsugaru.mediation.ggllib;

import com.github.k24.tsugaru.Tsugaru;
import com.github.k24.tsugaru.lane.EventBusLane;
import com.github.k24.tsugaru.lane.JsonLane;
import com.github.k24.tsugaru.lane.NetworkLane;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test GgllibMediation.
 * <p/>
 * Created by k24 on 2015/07/05.
 */
@RunWith(org.robolectric.RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GgllibMediationTest {

    private GgllibMediation mediation;

    @Before
    public void setUp() {
        mediation = new GgllibMediation(ShadowApplication.getInstance().getApplicationContext());
        Tsugaru.Configuration.apply(mediation);
    }

    @After
    public void tearDown() {
        VolleyNetworkLane volleyLane = mediation.getImplementation(NetworkLane.class);
        volleyLane.stop();
    }

    @Test
    public void bus() {
        final AtomicReference<String> eventRef = new AtomicReference<>();
        EventBusLane.Subscriber<String> subscriber = new EventBusLane.Subscriber<String>() {
            @Override
            public void onEvent(String s) {
                eventRef.set(s);
            }
        };
        EventBusLane.Registration registration = Tsugaru.bus().register(subscriber);
        // Just it!
        Tsugaru.bus().post("event");
        Assertions.assertThat(eventRef.get())
                .isEqualTo("event");
        // Twice
        Tsugaru.bus().post("event2");
        Assertions.assertThat(eventRef.get())
                .isEqualTo("event2");
        // Not matching type
        Tsugaru.bus().post(100);
        Assertions.assertThat(eventRef.get())
                .isEqualTo("event2");
        // Unregistered
        Tsugaru.bus().unregister(subscriber, registration);
        Tsugaru.bus().post("event3");
    }

    @Test
    public void json() {
        String encoded = Tsugaru.json().encode(new Sample("str", 1984));
        Sample decoded = Tsugaru.json().decode(encoded, Sample.class);

        Assertions.assertThat(decoded.string)
                .isEqualTo("str");
        Assertions.assertThat(decoded.integer)
                .isEqualTo(1984);
    }

    @Test
    public void jsonWithBuoy() {
        String encoded = Tsugaru.json(new JsonLane.GenericBuoy.Builder()
                .setNamingRule(JsonLane.GenericBuoy.NAMING_UPPER_CAMEL)
                .create()).encode(new Sample("str", 1984));
        Sample decoded = Tsugaru.json().decode(encoded.toLowerCase(), Sample.class);

        Assertions.assertThat(encoded)
                .contains("String", "Integer"); // See FieldNamingPolicy
        Assertions.assertThat(decoded.string)
                .isEqualTo("str");
        Assertions.assertThat(decoded.integer)
                .isEqualTo(1984);
    }

    @Test
    public void network() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody("responded"));

        server.start();

        try {
            final URL url = server.getUrl("/test");

            final AtomicReference<NetworkLane.Response> responseRef = new AtomicReference<>();
            final CountDownLatch latch = new CountDownLatch(1);
            Tsugaru.network().request(url.toString())
                    .call(new NetworkLane.OnResponseListener() {
                        @Override
                        public void onResponse(NetworkLane.Response response) {
                            responseRef.set(response);
                            latch.countDown();
                        }
                    });

            // Verify Request
            RecordedRequest recordedRequest = server.takeRequest(500, TimeUnit.MILLISECONDS);
            Assertions.assertThat(recordedRequest.getMethod())
                    .isEqualTo("GET");

            // Verify Response

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Robolectric.flushForegroundThreadScheduler();
                }
            }).start();

            latch.await(1000, TimeUnit.MILLISECONDS);
            NetworkLane.Response response = responseRef.get();
            Assertions.assertThat(response.error())
                    .isNull();
            Assertions.assertThat(response.content(String.class))
                    .isEqualTo("responded");
        } finally {
            server.shutdown();
        }
    }

    private static class Sample {
        public String string;
        public int integer;

        public Sample(String string, int integer) {
            this.string = string;
            this.integer = integer;
        }
    }
}
