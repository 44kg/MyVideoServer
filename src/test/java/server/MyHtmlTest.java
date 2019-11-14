package server;

import org.junit.Assert;
import org.junit.Test;

public class MyHtmlTest {
    @Test
    public void getHtmlAsStreamTest1() {
        Assert.assertNull(MyHtml.getHtmlAsStream(null));
    }

    @Test
    public void getHtmlAsStreamTest2() {
        Assert.assertNull(MyHtml.getHtmlAsStream("Any String"));
    }

    @Test
    public void getHtmlAsStreamTest3() {
        Assert.assertNotNull(MyHtml.getHtmlAsStream(MyHtml.ADMIN));
    }

    @Test
    public void getHtmlAsStreamTest4() {
        Assert.assertNotNull(MyHtml.getHtmlAsStream(MyHtml.ERROR_500));
    }

    @Test
    public void getHtmlAsStringTest1() {
        Assert.assertEquals("", MyHtml.getHtmlAsString(null));
    }

    @Test
    public void getHtmlAsStringTest2() {
        Assert.assertEquals("", MyHtml.getHtmlAsString("Any String"));
    }

    @Test
    public void getHtmlAsStringTest3() {
        Assert.assertNotNull(MyHtml.getHtmlAsString(MyHtml.ADMIN));
    }

    @Test
    public void getHtmlAsStringTest4() {
        Assert.assertNotNull(MyHtml.getHtmlAsString(MyHtml.ERROR_500));
    }
}
