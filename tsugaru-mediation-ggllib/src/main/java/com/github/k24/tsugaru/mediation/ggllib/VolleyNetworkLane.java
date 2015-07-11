package com.github.k24.tsugaru.mediation.ggllib;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.github.k24.tsugaru.lane.NetworkLane;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Network implementation with Volley.
 * <p/>
 * Created by k24 on 2015/07/05.
 */
public class VolleyNetworkLane implements NetworkLane {
    public static final String OPTION_METHOD = "method";

    protected RequestQueue requestQueue;

    public VolleyNetworkLane(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        start();
    }

    public void start() {
        requestQueue.start();
    }

    public void stop() {
        requestQueue.stop();
    }

    @Override
    public Request request(final String url) {
        return new Request() {
            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            byte[] body;
            LinkedHashMap<String, String> fields = new LinkedHashMap<>();

            @Override
            public Request header(String name, String value) {
                headers.put(name, value);
                return this;
            }

            @Override
            public Request body(byte[] bytes) {
                body = bytes;
                return this;
            }

            @Override
            public Request field(String name, String value) {
                fields.put(name, value);
                return this;
            }

            @Override
            public Connection call(final OnResponseListener onResponseListener) {
                int method = com.android.volley.Request.Method.GET;
                final com.android.volley.Request<byte[]> request = new com.android.volley.Request<byte[]>(method, url,
                        new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(final VolleyError volleyError) {
                                onResponseListener.onResponse(new Response() {

                                    @Override
                                    public Object content(Class<?>... acceptableClasses) {
                                        return null;
                                    }

                                    @Override
                                    public Exception error() {
                                        return volleyError;
                                    }
                                });
                            }
                        }) {
                    @Override
                    protected com.android.volley.Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
                        if (networkResponse.statusCode >= 400) {
                            return com.android.volley.Response.error(new ServerError(networkResponse));
                        }
                        return com.android.volley.Response.success(networkResponse.data, HttpHeaderParser.parseCacheHeaders(networkResponse));
                    }

                    @Override
                    protected void deliverResponse(final byte[] bytes) {
                        if (onResponseListener == null) return;
                        onResponseListener.onResponse(new Response() {
                            @Override
                            public Object content(Class<?>... acceptableClasses) {
                                for (Class<?> acceptableClass : acceptableClasses) {
                                    switch (acceptableClass.getName()) {
                                        case CLASS_STRING:
                                            return new String(bytes);
                                        case CLASS_BYTES:
                                            return bytes;
                                        case CLASS_INPUT_STREAM:
                                            return new ByteArrayInputStream(bytes);
                                        default:
                                            // continue;
                                    }
                                }
                                return bytes;
                            }

                            @Override
                            public Exception error() {
                                return null;
                            }
                        });
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return super.getHeaders();
                    }
                };
                requestQueue.add(request);
                return new Connection() {
                    long beginAt = System.currentTimeMillis();
                    long timeout = request.getTimeoutMs();

                    @Override
                    public void cancel() {
                        request.cancel();
                    }

                    @Override
                    public float progress() {
                        if (timeout == 0) return 0;
                        return 1f * (System.currentTimeMillis() - beginAt) / timeout;
                    }
                };
            }
        };
    }
}
