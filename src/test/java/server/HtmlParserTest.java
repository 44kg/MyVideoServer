package server;

import org.junit.Assert;
import org.junit.Test;
import server.html.HtmlParser;
import server.html.HTML;

import java.util.ArrayList;
import java.util.List;

public class HtmlParserTest {
    private String admin;
    private String error500;
    private String statistics;
    private String referenceStates;
    private List<String> list;

    public HtmlParserTest() {
        admin = HTML.getHtmlAsString(HTML.ADMIN);
        statistics = HTML.getHtmlAsString(HTML.STATISTICS);
        referenceStates = HTML.getHtmlAsString(HTML.REFERENCE_STATE);
        error500 = HTML.getHtmlAsString(HTML.ERROR_500);
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

    @Test
    public void parseStatisticsTest1() {
        HtmlParser.parseStatistic(statistics, null, null, null, null);
        Assert.assertEquals(statistics, HtmlParser.parseStatistic(statistics, null, null, null, null));
    }

    @Test
    public void parseStatisticsTest2() {
        Assert.assertNull(HtmlParser.parseStatistic(null, "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void parseStatisticsTest3() {
        Assert.assertEquals(statistics, HtmlParser.parseStatistic(statistics, "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    public void parseStatisticsTest4() {
        List<List<String>> l = new ArrayList<>();
        l.add(new ArrayList<>());
        Assert.assertEquals(statistics, HtmlParser.parseStatistic(statistics, "dates", l, list, list));
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
