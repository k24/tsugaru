import com.github.k24.tsugaru.Tsugaru;
import com.github.k24.tsugaru.json.jsonic.JsonicJsonLane;

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
                .json(new JsonicJsonLane())
                .apply();

        String encoded = Tsugaru.json(JsonicJsonLane.prettyPrint(true), JsonicJsonLane.propertyStyle(NamingStyle.UPPER_CASE)).encode(new SomeObject("Hi", 4649));
        Assert.assertTrue(encoded.contains("STRING"));
        Assert.assertTrue(encoded.contains("INTEGER"));
        Assert.assertTrue(encoded.split("\n").length == 4);
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
