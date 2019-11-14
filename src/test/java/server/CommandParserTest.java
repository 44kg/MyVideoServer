package server;

import org.junit.Assert;
import org.junit.Test;

public class CommandParserTest {
    @Test
    public void parseCpuLoadTest1() {
        Assert.assertEquals("", CommandParser.parseCpuLoad(null, 0));
    }

    @Test
    public void parseCpuLoadTest2() {
        Assert.assertEquals("", CommandParser.parseCpuLoad(null, 4));
    }

    @Test
    public void parseCpuLoadTest3() {
        Assert.assertEquals("", CommandParser.parseCpuLoad("Any String", 0));
    }

    @Test
    public void parseCpuLoadTest4() {
        Assert.assertEquals("", CommandParser.parseCpuLoad("", 1000000));
    }

    @Test
    public void parseCpuLoadTest5() {
        String string = StreamHandler.toString(ClassLoader.getSystemResourceAsStream("test/cpuLoad.txt"));
        Assert.assertEquals("30.6%", CommandParser.parseCpuLoad(string, 2));
    }

    @Test
    public void parseNumberOfCPUsTest1() {
        Assert.assertEquals("", CommandParser.parseNumberOfCPUs(null));
    }

    @Test
    public void parseNumberOfCPUsTest2() {
        Assert.assertEquals("", CommandParser.parseNumberOfCPUs("Any String"));
    }

    @Test
    public void parseNumberOfCPUsTest3() {
        Assert.assertEquals("", CommandParser.parseNumberOfCPUs(""));
    }

    @Test
    public void parseNumberOfCPUsTest4() {
        String string = StreamHandler.toString(ClassLoader.getSystemResourceAsStream("test/numberOfCPUs.txt"));
        Assert.assertEquals("4", CommandParser.parseNumberOfCPUs(string));
    }

    @Test
    public void parseFreeSpaceTest1() {
        Assert.assertEquals("", CommandParser.parseFreeSpace(null));
    }

    @Test
    public void parseFreeSpaceTest2() {
        Assert.assertEquals("", CommandParser.parseFreeSpace("Any String"));
    }

    @Test
    public void parseFreeSpaceTest3() {
        Assert.assertEquals("", CommandParser.parseFreeSpace(""));
    }

    @Test
    public void parseFreeSpaceTest4() {
        String string = StreamHandler.toString(ClassLoader.getSystemResourceAsStream("test/freeSpace.txt"));
        Assert.assertEquals("849GB", CommandParser.parseFreeSpace(string));
    }

    @Test
    public void parseArchiveSizeTest1() {
        Assert.assertEquals("", CommandParser.parseArchiveSize(null));
    }

    @Test
    public void parseArchiveSizeTest2() {
        Assert.assertEquals("", CommandParser.parseArchiveSize("Any String"));
    }

    @Test
    public void parseArchiveSizeTest3() {
        Assert.assertEquals("", CommandParser.parseArchiveSize(""));
    }

    @Test
    public void parseArchiveSizeTest4() {
        String string = StreamHandler.toString(ClassLoader.getSystemResourceAsStream("test/archiveSize.txt"));
        Assert.assertEquals("255MB", CommandParser.parseArchiveSize(string));
    }

    @Test
    public void parseNumberOfConnectionsTest1() {
        Assert.assertEquals("", CommandParser.parseNumberOfConnections(null, null));
    }

    @Test
    public void parseNumberOfConnectionsTest2() {
        Assert.assertEquals("", CommandParser.parseNumberOfConnections("", "anyPort"));
    }

    @Test
    public void parseNumberOfConnectionsTest3() {
        Assert.assertEquals("", CommandParser.parseNumberOfConnections("Any String", ""));
    }

    @Test
    public void parseNumberOfConnectionsTest4() {
        String string = StreamHandler.toString(ClassLoader.getSystemResourceAsStream("test/numberOfConnections.txt"));
        Assert.assertEquals("9", CommandParser.parseNumberOfConnections(string, "443"));
    }
}
