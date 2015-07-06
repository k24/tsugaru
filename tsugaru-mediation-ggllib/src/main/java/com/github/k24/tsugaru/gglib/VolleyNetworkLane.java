package com.github.k24.tsugaru.gglib;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.github.k24.tsugaru.lane.NetworkLane;

/**
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
    public void call(final NetworkLane.Request request) {
        int method = request.option(OPTION_METHOD, com.android.volley.Request.Method.GET);
        requestQueue.add(new com.android.volley.Request<byte[]>(method, request.url(), new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError volleyError) {
                request.onResponse(new Response() {
                    @Override
                    public byte[] body() {
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
            protected void deliverResponse(final byte[] object) {
                request.onResponse(new Response() {
                    @Override
                    public byte[] body() {
                        return object;
                    }

                    @Override
                    public Exception error() {
                        return null;
                    }
                });
            }
        });
    }

}
