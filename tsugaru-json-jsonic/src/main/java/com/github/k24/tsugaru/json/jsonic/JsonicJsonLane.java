package com.github.k24.tsugaru.json.jsonic;

import com.github.k24.tsugaru.buoy.Sticky;
import com.github.k24.tsugaru.lane.JsonLane;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.NamingStyle;

/**
 * JsonLane implementation with JSONIC.
 * <p/>
 * Created by k24 on 2015/07/04.
 */
public class JsonicJsonLane implements JsonLane {

    final JSON json;

    public JsonicJsonLane() {
        this(new JSON());
    }

    public JsonicJsonLane(JSON json) {
        this.json = json;
    }

    @Override
    public <T> T decode(String string, Class<T> clazz) {
        return json.parse(string, clazz);
    }

    @Override
    public String encode(Object object) {
        return json.format(object);
    }

    @Sticky
    public static JsonLane.Buoy prettyPrint(final boolean prettyPrint) {
        return new JsonLane.Buoy() {
            @Override
            public JsonLane arrange(JsonLane jsonLane) {
                JsonicJsonLane jsonicLane = (JsonicJsonLane) jsonLane;
                jsonicLane.json.setPrettyPrint(prettyPrint);
                return jsonicLane;
            }
        };
    }

    @Sticky
    public static JsonLane.Buoy propertyStyle(final NamingStyle namingStyle) {
        return new JsonLane.Buoy() {
            @Override
            public JsonLane arrange(JsonLane jsonLane) {
                JsonicJsonLane jsonicLane = (JsonicJsonLane) jsonLane;
                jsonicLane.json.setPropertyStyle(namingStyle);
                return jsonicLane;
            }
        };
    }

}
