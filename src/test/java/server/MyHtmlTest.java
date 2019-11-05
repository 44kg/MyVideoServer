package server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MyHtmlTest {
    private MyHtml myHtml;
    private MyHttpServer myHttpServer;

    public MyHtmlTest() {
        try {
            myHttpServer = new MyHttpServer("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void startTest() {
        myHtml = new MyHtml(myHttpServer);
    }

    @Test
    public void getHtmlAsStringTest1() {
        Assert.assertEquals("", myHtml.getHtmlAsString(null));
    }

    @Test
    public void getHtmlAsStringTest2() {
        Assert.assertEquals("", myHtml.getHtmlAsString("anyWrongHtmlName"));
    }

    @Test
    public void getHtmlAsStringTest3() {
        Assert.assertEquals("", myHtml.getHtmlAsString(null, "anyInfo"));
    }

    @Test
    public void getHtmlAsStringTest4() {
        Assert.assertEquals("", myHtml.getHtmlAsString("anyWrongHtmlName", "anyInfo"));
    }

    @Test
    public void getHtmlAsStringTest5() {
        Assert.assertNotNull(myHtml.getHtmlAsString(MyHtml.ERROR_500, null));
    }

    @Test
    public void getHtmlAsByteArrayTest1() {
        Assert.assertNotNull(myHtml.getHtmlAsByteArray(null));
    }

    @Test
    public void getHtmlAsByteArrayTest2() {
        Assert.assertNotNull(myHtml.getHtmlAsByteArray("anyWrongHtmlName"));
    }

    @Test
    public void getHtmlAsByteArrayTest3() {
        Assert.assertNotNull(myHtml.getHtmlAsByteArray(null, "anyInfo"));
    }

    @Test
    public void getHtmlAsByteArrayTest4() {
        Assert.assertNotNull(myHtml.getHtmlAsByteArray("anyWrongHtmlName", "anyInfo"));
    }

    @Test
    public void getHtmlAsByteArrayTest5() {
        Assert.assertNotNull(myHtml.getHtmlAsByteArray(MyHtml.ERROR_500, null));
    }

    @After
    public void endTest() {
        myHttpServer.stop();
    }
}
