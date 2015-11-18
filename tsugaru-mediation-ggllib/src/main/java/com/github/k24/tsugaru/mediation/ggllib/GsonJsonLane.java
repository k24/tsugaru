package com.github.k24.tsugaru.mediation.ggllib;

import com.github.k24.tsugaru.LazyMultipleton;
import com.github.k24.tsugaru.lane.JsonLane;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JsonLane implementation with GSON.
 * <p/>
 * Created by k24 on 2015/07/04.
 */
public class GsonJsonLane implements JsonLane.Arrangeable {
    final Gson gsonToDecode;
    final Gson gsonToEncode;
    private final GsonBuilder gsonBuilder;
    private final InstanceMap instanceMap = new InstanceMap();

    public GsonJsonLane() {
        this(new Gson());
    }

    public GsonJsonLane(GsonBuilder gsonBuilder) {
        gsonToDecode = gsonToEncode = gsonBuilder.create();
        this.gsonBuilder = gsonBuilder;
    }

    protected GsonJsonLane(Gson gson) {
        this(gson, gson);
    }

    public GsonJsonLane(Gson gsonToDecode, Gson gsonToEncode) {
        this(gsonToDecode, gsonToEncode, new GsonBuilder());
    }

    protected GsonJsonLane(Gson gsonToDecode, Gson gsonToEncode, GsonBuilder gsonBuilder) {
        this.gsonToDecode = gsonToDecode;
        this.gsonToEncode = gsonToEncode;
        this.gsonBuilder = gsonBuilder;
    }

    @Override
    public <T> T decode(String string, Class<T> clazz) {
        return gsonToDecode.fromJson(string, clazz);
    }

    @Override
    public String encode(Object object) {
        return gsonToEncode.toJson(object);
    }

    @Override
    public JsonLane arrange(Buoy buoy) {
        if (buoy.isRequired()) throw new UnsupportedOperationException("Override me for " + buoy);
        return this;
    }

    @Override
    public JsonLane arrange(GenericBuoy buoy) {
        boolean alreadyArranged = instanceMap.containsKey(buoy);
        if (alreadyArranged) return instanceMap.getInstance(buoy);
        return instanceMap.newInstance(buoy);
    }

    private static FieldNamingPolicy getNamingStyleFromGeneric(String namingRule) {
        if (namingRule == null) return FieldNamingPolicy.IDENTITY;
        switch (namingRule) {
            case GenericBuoy.NAMING_LOWER_CAMEL:
                throw new UnsupportedOperationException(namingRule + " is not supported.");
            case GenericBuoy.NAMING_LOWER_SNAKE:
                return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;
            case GenericBuoy.NAMING_UPPER_CAMEL:
                return FieldNamingPolicy.UPPER_CAMEL_CASE;
            case GenericBuoy.NAMING_UPPER_SNAKE:
                throw new UnsupportedOperationException(namingRule + " is not supported.");
            default:
                return FieldNamingPolicy.IDENTITY;
        }
    }

    private class InstanceMap extends LazyMultipleton<Buoy, GsonJsonLane> {

        @Override
        protected GsonJsonLane newInstance(Buoy key) {
            return new GsonJsonLane(gsonBuilder().create());
        }

        public GsonJsonLane newInstance(GenericBuoy buoy) {
            FieldNamingPolicy namingStyleToDecode = getNamingStyleFromGeneric(buoy.getNamingRuleToDecode());
            FieldNamingPolicy namingStyleToEncode = getNamingStyleFromGeneric(buoy.getNamingRuleToEncode());
            return new GsonJsonLane(new GsonBuilder()
                    .setFieldNamingPolicy(namingStyleToDecode)
                    .create(),
                    new GsonBuilder()
                            .setFieldNamingPolicy(namingStyleToEncode)
                            .create());
        }
    }

    protected GsonBuilder gsonBuilder() {
        return new GsonBuilder();
    }
}
