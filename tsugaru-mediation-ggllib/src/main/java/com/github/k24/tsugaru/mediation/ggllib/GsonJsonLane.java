package com.github.k24.tsugaru.mediation.ggllib;

import com.github.k24.tsugaru.buoy.Volatile;
import com.github.k24.tsugaru.lane.JsonLane;
import com.google.gson.Gson;

/**
 * JsonLane implementation with GSON.
 * <p/>
 * Created by k24 on 2015/07/04.
 */
public class GsonJsonLane implements JsonLane {
    final Gson gson;

    public GsonJsonLane() {
        this(new Gson());
    }

    protected GsonJsonLane(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> T decode(String string, Class<T> clazz) {
        return gson.fromJson(string, clazz);
    }

    @Override
    public String encode(Object object) {
        return gson.toJson(object);
    }

    @Volatile
    public static JsonLane.Buoy arrangeGson(final Arranger arranger) {
        return new JsonLane.Buoy() {
            @Override
            public JsonLane arrange(JsonLane jsonLane) {
                return new GsonJsonLane(arranger.arrange());
            }
        };
    }

    public interface Arranger {
        Gson arrange();
    }
}
