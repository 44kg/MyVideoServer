package server;

import org.junit.Assert;
import org.junit.Test;
import server.html.HTML;

public class HTMLTest {
    @Test
    public void getHtmlAsStringTest1() {
        Assert.assertEquals("", HTML.getHtmlAsString(null));
    }

    @Test
    public void getHtmlAsStringTest2() {
        Assert.assertEquals("", HTML.getHtmlAsString("Any String"));
    }

    @Test
    public void getHtmlAsStringTest3() {
        Assert.assertNotNull(HTML.getHtmlAsString(HTML.ADMIN));
    }

    @Test
    public void getHtmlAsStringTest4() {
        Assert.assertNotNull(HTML.getHtmlAsString(HTML.ERROR_500));
    }
}
