import com.github.k24.tsugaru.Tsugaru;
import com.github.k24.tsugaru.json.jsonic.JsonicJsonLane;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by k24 on 2015/07/04.
 */
public class JsonicJsonLaneTest {

    @Test
    public void test() {
        Tsugaru.Configuration.configurator()
                .json(new JsonicJsonLane())
                .apply();

        String encoded = Tsugaru.json().encode(new SomeObject("Hi", 4649));
        SomeObject decoded = Tsugaru.json().decode(encoded, SomeObject.class);
        Assert.assertEquals("Hi", decoded.string);
        Assert.assertEquals(4649, decoded.integer);
    }

    public static class SomeObject {
        public String string;
        public int integer;

        public SomeObject() {
        }

        public SomeObject(String string, int integer) {
            this.string = string;
            this.integer = integer;
        }
    }
}
