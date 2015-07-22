import com.github.k24.tsugaru.Tsugaru;
import com.github.k24.tsugaru.json.jsonic.JsonicJsonLane;
import com.github.k24.tsugaru.lane.JsonLane;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.NamingStyle;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test JsonicJsonLane.
 * <p/>
 * Created by k24 on 2015/07/04.
 */
public class JsonicJsonLaneTest {

    @Test
    public void encodeAndDecodeNormally() {
        Tsugaru.Configuration.configurator()
                .json(new JsonicJsonLane())
                .apply();

        String encoded = Tsugaru.json().encode(new SomeObject("Hi", 4649));
        SomeObject decoded = Tsugaru.json().decode(encoded, SomeObject.class);
        Assert.assertEquals("Hi", decoded.string);
        Assert.assertEquals(4649, decoded.integer);
    }

    @Test
    public void encodeAndDecodeWithBuoy() {
        Tsugaru.Configuration.configurator()
                .json(new JsonicJsonLane(new JsonicJsonLane.JsonFactory() {
                    @Override
                    public JSON newJsonDefault() {
                        JSON json = new JSON();
                        json.setPrettyPrint(true);
                        return json;
                    }
                }))
                .apply();

        String encoded = Tsugaru.json(new JsonLane.GenericBuoy.Builder()
                .setNamingRule(JsonicJsonLane.GenericBuoy.NAMING_UPPER_CAMEL)
                .create()).encode(new SomeObject("Hi", 4649));
        Assert.assertTrue(encoded.contains("String"));
        Assert.assertTrue(encoded.contains("Integer"));
        Assert.assertTrue(encoded, encoded.split("\n").length == 4);
    }

    public static class SomeObject {
        public String string;
        public int integer;

        // For JSONIC
        @SuppressWarnings("unused")
        public SomeObject() {
        }

        public SomeObject(String string, int integer) {
            this.string = string;
            this.integer = integer;
        }
    }
}
