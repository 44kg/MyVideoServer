package server;

import org.junit.Assert;
import org.junit.Test;
import server.html.HtmlParser;
import server.html.MyHtml;

import java.util.ArrayList;
import java.util.List;

public class HtmlParserTest {
    private String admin;
    private String error500;
    private String statistics;
    private String referenceStates;
    private List<String> list;

    public HtmlParserTest() {
        admin = MyHtml.getHtmlAsString(MyHtml.ADMIN);
        statistics = MyHtml.getHtmlAsString(MyHtml.STATISTICS);
        referenceStates = MyHtml.getHtmlAsString(MyHtml.REFERENCE_STATE);
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
    public void parseReferenceStateTest1() {
        HtmlParser.parseReferenceState(referenceStates, null);
    }

    @Test
    public void parseReferenceStateTest2() {
        Assert.assertNull(HtmlParser.parseReferenceState(null, new ArrayList<>()));
    }

    @Test
    public void parseReferenceStateTest3() {
        Assert.assertEquals(referenceStates, HtmlParser.parseReferenceState(referenceStates, new ArrayList<>()));
    }

    @Test
    public void parseReferenceStateTest4() {
        Assert.assertNotEquals(referenceStates, HtmlParser.parseReferenceState(referenceStates, list));
    }

    @Test
    public void parseReferenceStateTest5() {
        String s = HtmlParser.parseReferenceState(referenceStates, list);
        Assert.assertTrue(s.contains("test1") && s.contains("test2") && s.contains("test3")
                && s.contains("test4") && s.contains("test5"));
    }

    @Test (expected = NullPointerException.class)
    public void parseStatisticsTest1() {
        HtmlParser.parseStatistics(statistics, null, null, null, null);
    }

    @Test
    public void parseStatisticsTest2() {
        Assert.assertNull(HtmlParser.parseStatistics(null, "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void parseStatisticsTest3() {
        Assert.assertEquals(statistics, HtmlParser.parseStatistics(statistics, "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void parseStatisticsTest4() {
        List<List<String>> l = new ArrayList<>();
        l.add(new ArrayList<>());
        Assert.assertNotEquals(statistics, HtmlParser.parseStatistics(statistics, "dates", l, list, list));
    }

    @Test
    public void parseStatisticsTest5() {
        List<List<String>> l = new ArrayList<>();
        l.add(new ArrayList<>());
        String s = HtmlParser.parseStatistics(statistics, "dates", l, list, list);
        Assert.assertTrue(s.contains("test1") && s.contains("test2") && s.contains("test3")
                && s.contains("test4") && s.contains("test5") && s.contains("dates"));
    }

    @Test (expected = NullPointerException.class)
    public void parseError500Test1() {
        Assert.assertNull(HtmlParser.parseError500(null, null));
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
