package server;

import org.junit.Assert;
import org.junit.Test;

public class StreamHandlerTest {
    @Test
    public void toByteArrayTest1() {
        Assert.assertNull(StreamHandler.toByteArray(null));
    }

    @Test
    public void toStringTest1() {
        Assert.assertNull(StreamHandler.toString(null));
    }
}
