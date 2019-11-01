package server;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LinuxCommandParserTest {
    @Test
    public void runLinuxCommandTest1() {
        Assert.assertEquals("Hello World\n", LinuxCommandParser.runLinuxCommand("echo Hello World").toString());
    }

    @Test
    public void runLinuxCommandTest2() {
        Assert.assertNull(LinuxCommandParser.runLinuxCommand("enyWrongCommand"));
    }

    @Test
    public void runLinuxCommandTest3() {
        Assert.assertNull(LinuxCommandParser.runLinuxCommand(null));
    }

    @Test
    public void runLinuxCommandTest4() {
        Assert.assertNull(LinuxCommandParser.runLinuxCommand(""));
    }

    @Test
    public void runLinuxCommandTest5() {
        Assert.assertNull(LinuxCommandParser.runLinuxCommand("    "));
    }

    @Test
    public void getCpuLoadTest1() {
        Assert.assertNotNull(LinuxCommandParser.getCpuLoad());
    }

    @Test
    public void getCpuLoadTest2() {
        Assert.assertTrue(LinuxCommandParser.getCpuLoad().length() >= 4);
    }

    @Test
    public void getCpuLoadTest3() {
        String string = LinuxCommandParser.getCpuLoad();
        string = string.substring(0, string.length() - 1);
        float cpuLoad = Float.parseFloat(string);
        Assert.assertTrue(cpuLoad > 0 && cpuLoad <= 100);
    }

    @Test
    public void getNumberOfCPUsTest1() {
        Assert.assertNotNull(LinuxCommandParser.getNumberOfCPUs());
    }

    @Test
    public void getNumberOfCPUsTest2() {
        int cpuNum = Integer.parseInt(LinuxCommandParser.getNumberOfCPUs());
        Assert.assertTrue(cpuNum > 0);
    }

    @Test
    public void getFreeSpaceTest1() {
        Assert.assertNotNull(LinuxCommandParser.getFreeSpace());
    }

    @Test
    public void getFreeSpaceTest2() {
        String string = LinuxCommandParser.getFreeSpace();
        string = string.substring(0, string.length() - 2);
        int freeSpace = Integer.parseInt(string);
        Assert.assertTrue(freeSpace >= 0);
    }

    @Test
    public void getArchiveSizeTest1() {
        Assert.assertNotNull(LinuxCommandParser.getArchiveSize());
    }

    @Test
    public void getArchiveSizeTest2() {
        String string = LinuxCommandParser.getArchiveSize();
        string = string.substring(0, string.length() - 2);
        int archiveSize = Integer.parseInt(string);
        Assert.assertTrue(archiveSize >= 0);
    }

    @Test
    public void getNumberOfConnectionsTest1() {
        Assert.assertNull(LinuxCommandParser.getNumberOfConnections(null, null));
    }

    @Test
    public void getNumberOfConnectionsTest2() {
        Assert.assertEquals(0, Integer.parseInt(LinuxCommandParser
                .getNumberOfConnections(new StringBuilder(), "anyPort")));
    }

    @Test
    public void getNumberOfConnectionsTest3() {
        Assert.assertEquals(0, Integer.parseInt(LinuxCommandParser
                .getNumberOfConnections(new StringBuilder("anyStringBuilder"), "")));
    }
}
