package com.github.k24.tsugaru.lane;

import com.github.k24.tsugaru.buoy.BuoyTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Lane to ship JSON.
 * <p/>
 * Created by k24 on 2015/06/22.
 */
public interface JsonLane {
    /**
     * Decode a JSON string to an object.
     *
     * @param <T>    destiny type to decode
     * @param string string to decode as JSON
     * @param clazz  destiny class to decode
     * @return an instance decoded
     */
    <T> T decode(String string, Class<T> clazz);

    /**
     * Encode a JSON object to encode as JSON
     *
     * @param object to encode as JSON
     * @return a string encoded
     */
    String encode(Object object);

    /**
     * To arrange the lane.
     * <p/>
     * By default, this has no meaning.
     */
    abstract class Buoy extends BuoyTemplate<JsonLane> {
        @Override
        public JsonLane placeTo(JsonLane lane) {
            if (lane instanceof Arrangeable) {
                return accept((Arrangeable) lane);
            }
            throwIfRequired();
            return lane;
        }

        protected abstract JsonLane accept(Arrangeable arrangeable);
    }

    interface Arrangeable extends JsonLane {
        JsonLane arrange(Buoy buoy);

        JsonLane arrange(GenericBuoy buoy);
    }

    final class GenericBuoy extends Buoy {
        public static final String NAMING_LOWER_CAMEL = "lowerCamel";
        public static final String NAMING_LOWER_SNAKE = "lower_snake";
        public static final String NAMING_UPPER_CAMEL = "UpperCamel";
        public static final String NAMING_UPPER_SNAKE = "UPPER_SNAKE";

        private final String namingRuleToDecode;
        private final String namingRuleToEncode;

        GenericBuoy(String namingRuleToDecode, String namingRuleToEncode) {
            this.namingRuleToDecode = namingRuleToDecode;
            this.namingRuleToEncode = namingRuleToEncode;
        }

        @Override
        protected JsonLane accept(Arrangeable arrangeable) {
            return arrangeable.arrange(this);
        }

        @Override
        public boolean isRequired() {
            return true;
        }

        public String getNamingRuleToDecode() {
            return namingRuleToDecode;
        }

        public String getNamingRuleToEncode() {
            return namingRuleToEncode;
        }

        //region Generated
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GenericBuoy that = (GenericBuoy) o;

            if (namingRuleToDecode != null ? !namingRuleToDecode.equals(that.namingRuleToDecode) : that.namingRuleToDecode != null)
                return false;
            return !(namingRuleToEncode != null ? !namingRuleToEncode.equals(that.namingRuleToEncode) : that.namingRuleToEncode != null);

        }

        @Override
        public int hashCode() {
            int result = namingRuleToDecode != null ? namingRuleToDecode.hashCode() : 0;
            result = 31 * result + (namingRuleToEncode != null ? namingRuleToEncode.hashCode() : 0);
            return result;
        }
        //endregion

        public static class Builder {
            Map<String, String> params = new HashMap<>();

            public Builder setNamingRule(String namingRule) {
                params.put("namingRuleToDecode", namingRule);
                params.put("namingRuleToEncode", namingRule);
                return this;
            }

            public Builder setNamingRule(String namingRuleToDecode, String namingRuleToEncode) {
                params.put("namingRuleToDecode", namingRuleToDecode);
                params.put("namingRuleToEncode", namingRuleToEncode);
                return this;
            }

            public GenericBuoy create() {
                return new GenericBuoy(params.get("namingRuleToDecode"), params.get("namingRuleToEncode"));
            }
        }
    }
}
