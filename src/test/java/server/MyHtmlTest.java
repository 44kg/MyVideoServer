package server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MyHtmlTest {
    private MyHtml myHtml;

    @Before
    public void startTest() {
        myHtml = new MyHtml();
    }

    @Test
    public void getHtmlAsStringTest1() {
        Assert.assertNull(myHtml.getHtmlAsString(null));
    }

    @Test
    public void getHtmlAsStringTest2() {
        Assert.assertNull(myHtml.getHtmlAsString("anyWrongHtmlName"));
    }

    @Test
    public void getHtmlAsStringTest3() {
        Assert.assertNull(myHtml.getHtmlAsString(null, "anyInfo"));
    }

    @Test
    public void getHtmlAsStringTest4() {
        Assert.assertNull(myHtml.getHtmlAsString("anyWrongHtmlName", "anyInfo"));
    }

    @Test
    public void getHtmlAsStringTest5() {
        Assert.assertNotNull(myHtml.getHtmlAsString(MyHtml.ERROR_500, null));
    }

    @Test
    public void getHtmlAsByteArrayTest1() {
        Assert.assertNull(myHtml.getHtmlAsByteArray(null));
    }

    @Test
    public void getHtmlAsByteArrayTest2() {
        Assert.assertNull(myHtml.getHtmlAsByteArray("anyWrongHtmlName"));
    }

    @Test
    public void getHtmlAsByteArrayTest3() {
        Assert.assertNull(myHtml.getHtmlAsByteArray(null, "anyInfo"));
    }

    @Test
    public void getHtmlAsByteArrayTest4() {
        Assert.assertNull(myHtml.getHtmlAsByteArray("anyWrongHtmlName", "anyInfo"));
    }

    @Test
    public void getHtmlAsByteArrayTest5() {
        Assert.assertNotNull(myHtml.getHtmlAsByteArray(MyHtml.ERROR_500, null));
    }
}
