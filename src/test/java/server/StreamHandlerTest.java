package server;

import org.junit.Assert;
import org.junit.Test;

public class StreamHandlerTest {
    @Test
    public void toByteArrayTest() {
        Assert.assertNull(StreamHandler.toByteArray(null));
    }

    @Test
    public void toStringTest() {
        Assert.assertNull(StreamHandler.toString(null));
    }
}
