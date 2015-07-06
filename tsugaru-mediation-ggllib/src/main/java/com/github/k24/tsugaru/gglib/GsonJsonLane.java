package com.github.k24.tsugaru.gglib;

import com.github.k24.tsugaru.buoy.Sticky;
import com.github.k24.tsugaru.lane.JsonLane;
import com.google.gson.Gson;

/**
 * Created by k24 on 2015/07/04.
 */
public class GsonJsonLane implements JsonLane {
    Gson gson = new Gson();

    @Override
    public <T> T decode(String string, Class<T> clazz) {
        return gson.fromJson(string, clazz);
    }

    @Override
    public String encode(Object object) {
        return gson.toJson(object);
    }

    @Sticky
    public static JsonLane.Buoy arrangeGson(final Arranger arranger) {
        return new JsonLane.Buoy() {
            @Override
            public JsonLane arrange(JsonLane jsonLane) {
                GsonJsonLane gsonLane = (GsonJsonLane) jsonLane;
                arranger.arrange(gsonLane.gson);
                return gsonLane;
            }
        };
    }

    public interface Arranger {
        void arrange(Gson gson);
    }
}
