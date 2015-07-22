package com.github.k24.tsugaru.json.jsonic;

import com.github.k24.tsugaru.LazyMultipleton;
import com.github.k24.tsugaru.lane.JsonLane;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.NamingStyle;

/**
 * JsonLane implementation with JSONIC.
 * <p/>
 * Created by k24 on 2015/07/04.
 */
public class JsonicJsonLane implements JsonLane.Arrangeable {

    final JSON jsonToDecode;
    final JSON jsonToEncode;
    private JsonFactory factory;
    private InstanceMap instanceMap;

    public JsonicJsonLane() {
        this(new JsonFactory() {
            @Override
            public JSON newJsonDefault() {
                return new JSON();
            }
        });
    }

    public JsonicJsonLane(JsonFactory factory) {
        this(factory.newJsonDefault());
        this.factory = factory;
        this.instanceMap = new InstanceMap();
    }

    protected JsonicJsonLane(JSON json) {
        this(json, json);
    }

    public JsonicJsonLane(JSON jsonToDecode, JSON jsonToEncode) {
        this.jsonToDecode = jsonToDecode;
        this.jsonToEncode = jsonToEncode;
    }

    @Override
    public <T> T decode(String string, Class<T> clazz) {
        return jsonToDecode.parse(string, clazz);
    }

    @Override
    public String encode(Object object) {
        return jsonToEncode.format(object);
    }

    @Override
    public JsonLane arrange(Buoy buoy) {
        if (buoy.isRequired()) throw new UnsupportedOperationException("Override me for " + buoy);
        return this;
    }

    @Override
    public JsonLane arrange(GenericBuoy buoy) {
        boolean alreadyArranged = instanceMap.containsKey(buoy);
        JsonicJsonLane instance = instanceMap.getInstance(buoy);
        if (alreadyArranged) return instance;
        NamingStyle namingStyleToDecode = getNamingStyleFromGeneric(buoy.getNamingRuleToDecode());
        NamingStyle namingStyleToEncode = getNamingStyleFromGeneric(buoy.getNamingRuleToEncode());
        instance.jsonToDecode.setPropertyStyle(namingStyleToDecode);
        instance.jsonToEncode.setPropertyStyle(namingStyleToEncode);
        return instance;
    }

    private static NamingStyle getNamingStyleFromGeneric(String namingRule) {
        if (namingRule == null) return NamingStyle.NOOP;
        switch (namingRule) {
            case GenericBuoy.NAMING_LOWER_CAMEL:
                return NamingStyle.LOWER_CAMEL;
            case GenericBuoy.NAMING_LOWER_SNAKE:
                return NamingStyle.LOWER_UNDERSCORE;
            case GenericBuoy.NAMING_UPPER_CAMEL:
                return NamingStyle.UPPER_CAMEL;
            case GenericBuoy.NAMING_UPPER_SNAKE:
                return NamingStyle.UPPER_UNDERSCORE;
            default:
                return NamingStyle.NOOP;
        }
    }

    private class InstanceMap extends LazyMultipleton<JsonLane.Buoy, JsonicJsonLane> {

        @Override
        protected JsonicJsonLane newInstance(Buoy key) {
            return new JsonicJsonLane(factory.newJsonDefault(), factory.newJsonDefault());
        }
    }

    public interface JsonFactory {
        JSON newJsonDefault();
    }
}
