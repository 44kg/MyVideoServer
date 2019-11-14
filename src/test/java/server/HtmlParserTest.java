package server;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HtmlParserTest {
    private String admin;
    private String error500;
    private List<String> list;

    public HtmlParserTest() {
        admin = MyHtml.getHtmlAsString(MyHtml.ADMIN);
        error500 = MyHtml.getHtmlAsString(MyHtml.ERROR_500);
        list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        list.add("test4");
        list.add("test5");
    }

    @Test (expected = NullPointerException.class)
    public void parseAdminTest1() {
        HtmlParser.parseAdmin(admin, null);
    }

    @Test
    public void parseAdminTest2() {
        Assert.assertNull(HtmlParser.parseAdmin(null, new ArrayList<>()));
    }

    @Test
    public void parseAdminTest3() {
        Assert.assertEquals(admin, HtmlParser.parseAdmin(admin, new ArrayList<>()));
    }

    @Test
    public void parseAdminTest4() {
        Assert.assertNotEquals(admin, HtmlParser.parseAdmin(admin, list));
    }

    @Test
    public void parseAdminTest5() {
        String s = HtmlParser.parseAdmin(admin, list);
        Assert.assertTrue(s.contains("test1") && s.contains("test2") && s.contains("test3")
                && s.contains("test4") && s.contains("test5"));
    }

    @Test (expected = NullPointerException.class)
    public void parseError500Test1() {
        HtmlParser.parseError500(null, null);
    }

    @Test
    public void parseError500Test2() {
        Assert.assertNotNull(HtmlParser.parseError500("Any String", null));
    }

    @Test
    public void parseError500Test3() {
        String s = HtmlParser.parseError500(error500, "ERROR12345");
        Assert.assertTrue(s.contains("ERROR12345"));
    }
}
